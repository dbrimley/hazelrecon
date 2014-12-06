package com.craftedbytes.hazelcast.wan.recon;

import com.craftedbytes.hazelcast.wan.RepKey;
import com.google.common.collect.Multimap;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

public class ClusterRepairTask
        implements Callable<ClusterReconResults>{

    private final ClusterReconResults clusterReconResult;
    private HazelcastInstance localCluster;
    private String localClusterName;
    private HazelcastInstance remoteCluster;
    private String remoteClusterName;
    private String ignoredMap = "replication";

    public ClusterRepairTask(HazelcastInstance localCluster,
                             String localClusterName,
                             HazelcastInstance remoteCluster,
                             String remoteClusterName,
                             ClusterReconResults clusterReconResults
    ) {

        this.localCluster = localCluster;
        this.localClusterName = localClusterName;
        this.remoteCluster = remoteCluster;
        this.remoteClusterName = remoteClusterName;
        this.clusterReconResult = clusterReconResults;
    }

    @Override
    public ClusterReconResults call()
            throws Exception {

        Multimap<String, DataSerializable> missingKeysByMap = clusterReconResult.getMissingKeysByMap();

        IMap<Object, Object> replication = remoteCluster.getMap("replication");

        for(String map:missingKeysByMap.keySet()){
            Map remoteMap = remoteCluster.getMap(map);
            System.out.println("Repairing Map " + map);
            Collection<DataSerializable> keys = missingKeysByMap.get(map);
            System.out.println("Repairing " + keys.size() + " keys");
            Map currentLocalMap = localCluster.getMap(map);
            for(DataSerializable key:keys){
                System.out.println("Repairing Key " + key);
                Object value = currentLocalMap.get(key);
                remoteMap.put(key,value);
            }
        }

        return clusterReconResult;


    }
}
