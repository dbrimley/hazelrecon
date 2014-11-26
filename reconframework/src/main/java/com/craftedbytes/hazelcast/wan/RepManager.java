package com.craftedbytes.hazelcast.wan;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RepManager is responsible for setting up the Replication proxy map in the Hazelcast Cluster and for initiating
 * EntryListeners on this map and for all other maps.
 *
 * The EntryListener on the Replication proxy map actives when records come in from other clusters.
 *
 * The EntryListeners on the standard user maps are responsible for passing on the MapEntry into the Replication proxy map.
 *
 */
public class RepManager implements DistributedObjectListener {

    public static final int TIME_TO_LIVE_SECONDS = 1;
    public static final boolean INCLUDE_VALUE = true;
    private HazelcastInstance hazelcastInstance;
    private String repMapName;
    private String repSchemeRef;
    private IMap<RepKey,DataSerializable> repMap;
    private String groupConfig;

    private Map<String, IMap> maps = new HashMap<String,IMap>();

    private Map<String, String> outboundListenerIDPerMap = new HashMap<String, String>();

    private Logger logger = Logger.getLogger(RepManager.class.toString());

    public RepManager(HazelcastInstance hazelcastInstance,
                      String repMapName,
                      String repSchemeRef) {

        if (hazelcastInstance == null) throw new IllegalArgumentException("hazelcastInstance is null");
        if (repMapName == null) throw new IllegalArgumentException("repMapName is null");
        if (repSchemeRef == null) throw new IllegalArgumentException("repSchemeRef is null");

        this.hazelcastInstance = hazelcastInstance;
        this.repMapName = repMapName;
        this.repSchemeRef = repSchemeRef;

        createReplicationMap();
    }

    public RepManager(HazelcastInstance hazelcastInstance,
                      IMap<RepKey,DataSerializable> repMap) {

        if (hazelcastInstance == null) throw new IllegalArgumentException("hazelcastInstance is null");
        if (repMap == null) throw new IllegalArgumentException("repMap is null");

        this.hazelcastInstance = hazelcastInstance;
        this.repMap= repMap;
        this.groupConfig = hazelcastInstance.getConfig().getGroupConfig().toString();
    }

    public void start() {
        //addListenerForNewMapsAdded();
        loadExistingMaps();
        addOutboundEntryListenerToMaps();
        addInboundEntryListenerToReplicationMap();

    }

    public void stop() {
        logger.info("Stopping RepManager");
        for(String map:outboundListenerIDPerMap.keySet()){
            hazelcastInstance.getMap(map).removeEntryListener(outboundListenerIDPerMap.get(map));
            logger.info("Removing Outbound EntryListener for map : " + map);
        }
        logger.info("Stopped RepManager");
    }

    private void addInboundEntryListenerToReplicationMap() {
        repMap.addEntryListener(new InboundReplicationEntryListener(hazelcastInstance), INCLUDE_VALUE);
    }

    private void addListenerForNewMapsAdded() {
        hazelcastInstance.addDistributedObjectListener(this);
    }

    /**
     * Adds an EntryListener for each User Map.  The Entry Listener is responsible for passing on entries
     * to the Replication Map.
     */
    private void addOutboundEntryListenerToMaps() {
        Collection<IMap> iMaps = maps.values();
        for(IMap map:iMaps){
            if (!map.getName().equals(repMap.getName())) {
                addOutboundEntryListenerToMap(map);
            }
        }
    }

    private void addOutboundEntryListenerToMap(IMap map) {
        String listenerID = map.addLocalEntryListener(new OutboundReplicationEntryListener(repMap, map.getName(), groupConfig));
        outboundListenerIDPerMap.put(map.getName(),listenerID);
        logger.info("Adding Replication Map Proxy for " + map.getName());
    }

    private void createReplicationMap() {
        MapConfig repMapConfig = new MapConfig();
        repMapConfig.setName(repMapName);
        repMapConfig.setTimeToLiveSeconds(TIME_TO_LIVE_SECONDS);
        repMapConfig.setWanReplicationRef(new WanReplicationRef(repSchemeRef, PassThroughMergePolicy.class.getName()));
        hazelcastInstance.getConfig().addMapConfig(repMapConfig);
        repMap = hazelcastInstance.getMap(repMapName);
        logger.info("Creating Replication Map with name of " + repMap.getName());
    }

    private void loadExistingMaps() {
        Collection<DistributedObject> objects = hazelcastInstance.getDistributedObjects();
        for ( DistributedObject distributedObject : objects ) {
            if ( distributedObject instanceof IMap) {
                if (!distributedObject.getName().equals(repMap.getName())) {
                    IMap map = (IMap) distributedObject;
                    maps.put(map.getName(), map);
                    logger.info("Loaded " + map.getName() + " into RepManager");
                }
            }
        }
    }

    public IMap<RepKey, DataSerializable> getRepMap() {
        return repMap;
    }

    @Override
    public void distributedObjectCreated(DistributedObjectEvent event) {
        logger.log(Level.FINE, event.toString());
        if(event.getDistributedObject() instanceof IMap){
            IMap iMap = (IMap) event.getDistributedObject();
            if (!iMap.getName().equals(repMap.getName())) {
                maps.put(repMap.getName(), iMap);
                addOutboundEntryListenerToMap(iMap);
            }
        }
    }

    @Override
    public void distributedObjectDestroyed(DistributedObjectEvent event) {
        logger.log(Level.FINE, event.toString());
        if(event.getDistributedObject() instanceof IMap){
            maps.remove(repMap.getName());
        }
    }

    /**
     * A Listener that is placed on each User Map to pass on events to the Replication Map
     */
    private class OutboundReplicationEntryListener extends EntryAdapter<Object,Object>{

        private IMap<RepKey,DataSerializable> replicationMap;
        private String mapName;
        private String groupConfig;

        private OutboundReplicationEntryListener(IMap<RepKey, DataSerializable> replicationMap,
                                                 String mapName,
                                                 String groupConfig) {

            if (replicationMap == null) throw new IllegalArgumentException("replicationMap is null");
            if (mapName == null) throw new IllegalArgumentException("mapName is null");

            this.replicationMap = replicationMap;
            this.mapName = mapName;
            this.groupConfig = groupConfig;

            logger.info("Created Outbound Replication Entry Listener for map " + mapName);

        }

        @Override
        public void entryAdded(EntryEvent<Object, Object> event) {

            if (event.getKey() instanceof DataSerializable && event.getValue() instanceof DataSerializable){
                DataSerializable dataSerializableKey = (DataSerializable) event.getKey();

                // If the Map Entry passing through originated in this cluster then let it go onto Replication Map
                // Otherwise stop it here and return as we don't want a loop.
                if (dataSerializableKey instanceof OriginAware) {
                    OriginAware originAware = (OriginAware) dataSerializableKey;
                    if (!originAware.getOriginGroupConfig().equals(groupConfig)) return;
                }

                DataSerializable dataSerializableValue = (DataSerializable) event.getValue();
                replicationMap.set(new RepKey(dataSerializableKey, mapName), dataSerializableValue);
                logger.info("Passed " + event + " to " + repMap.getName());
            } else {
                logger.warning(event.toString() + " has non DataSerializable types, could not pass onto Replication Put");
            }

        }

        @Override
        public void entryRemoved(EntryEvent<Object, Object> event) {

            if (event.getKey() instanceof DataSerializable && event.getValue() instanceof DataSerializable){
                DataSerializable dataSerializableKey = (DataSerializable) event.getKey();
                replicationMap.remove(new RepKey(dataSerializableKey, mapName));
                logger.info("Passed " + event + " to " + repMap.getName());
            } else {
                logger.warning(event.toString() + " has non DataSerializable types, could not pass onto Replication Remove");
            }

        }

        @Override
        public void entryUpdated(EntryEvent<Object, Object> event) {

            if (event.getKey() instanceof DataSerializable && event.getValue() instanceof DataSerializable){
                DataSerializable dataSerializableKey = (DataSerializable) event.getKey();

                // If the Map Entry passing through originated in this cluster then let it go onto Replication Map
                // Otherwise stop it here and return as we don't want a loop.
                if (dataSerializableKey instanceof OriginAware) {
                    OriginAware originAware = (OriginAware) dataSerializableKey;
                    if (!originAware.getOriginGroupConfig().equals(groupConfig)) return;
                }

                DataSerializable dataSerializableValue = (DataSerializable) event.getValue();
                replicationMap.set(new RepKey(dataSerializableKey, mapName), dataSerializableValue);
                logger.info("Passed " + event + " to " + repMap.getName());
            } else {
                logger.warning(event.toString() + " has non DataSerializable types, could not pass onto Replication Update");
            }

        }

    }

    /**
     * Listeners for inbound objects from other clusters, unwraps the RepKey and places the data into
     * the destination Map.
     */
    private class InboundReplicationEntryListener extends EntryAdapter<RepKey, DataSerializable> {

        private HazelcastInstance hazelcastInstance;

        private InboundReplicationEntryListener(HazelcastInstance hazelcastInstance) {
            if (hazelcastInstance == null) throw new IllegalArgumentException("hazelcastInstance is null");

            this.hazelcastInstance = hazelcastInstance;
        }

        @Override
        public void entryMerged(EntryEvent<RepKey, DataSerializable> event) {

            RepKey repKey = event.getKey();
            Object key = repKey.getUserKey();
            DataSerializable value = event.getValue();
            String destinationMapName = repKey.getDestinationMap();

            MapConfig mapConfig = hazelcastInstance.getConfig().getMapConfig(destinationMapName);

            if (mapConfig == null) logger.warning("No explicit configuration for map " + destinationMapName);

            IMap<Object, Object> destinationMap = hazelcastInstance.getMap(destinationMapName);

            destinationMap.put(key,value);

            logger.info("Entry Merged \n" + "k=" + key + "\n" + "v=" + value + "\n" + "to=" + destinationMapName);

        }

    }
}
