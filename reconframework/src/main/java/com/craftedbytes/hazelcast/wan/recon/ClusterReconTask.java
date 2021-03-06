package com.craftedbytes.hazelcast.wan.recon;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Map;
import java.util.concurrent.Callable;

public class ClusterReconTask implements Callable<ClusterReconResults>{

    private HazelcastInstance localCluster;
    private String localClusterName;
    private HazelcastInstance remoteCluster;
    private String remoteClusterName;
    private String ignoredMap = "replication";

    public ClusterReconTask(HazelcastInstance localCluster,
                            String localClusterName,
                            HazelcastInstance remoteCluster,
                            String remoteClusterName) {

        this.localCluster = localCluster;
        this.localClusterName = localClusterName;
        this.remoteCluster = remoteCluster;
        this.remoteClusterName = remoteClusterName;
    }

    @Override
    public ClusterReconResults call()
            throws Exception {

        ClusterReconResults results = new ClusterReconResults(localClusterName,remoteClusterName);

        Map<String, MapConfig> mapConfigs = localCluster.getConfig().getMapConfigs();

        for (String mapName:mapConfigs.keySet()){

            if(ignoredMap.equals(mapName)) continue;

            IMap<DataSerializable, DataSerializable> localClusterMap = localCluster.getMap(mapName);
            IMap<DataSerializable, DataSerializable> remoteClusterMap = remoteCluster.getMap(mapName);

            for (DataSerializable localKey:localClusterMap.keySet()){
                if (remoteClusterMap.containsKey(localKey)){
                    DataSerializable remoteValue = remoteClusterMap.get(localKey);
                    DataSerializable localValue = remoteClusterMap.get(localKey);
                    if(!remoteValue.equals(localValue)){
                        results.getDifferentValuesByMap().put(mapName,localKey);
                    }
                } else {
                    results.getMissingKeysByMap().put(mapName,localKey);
                }
            }

        }

        return results;

    }
}
