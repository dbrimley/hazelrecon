package com.craftedbytes.hazelcast.wan.demo;

import com.craftedbytes.hazelcast.wan.demo.domain.PortableFactory;
import com.craftedbytes.hazelcast.wan.recon.ClusterReconResults;
import com.craftedbytes.hazelcast.wan.recon.ClusterReconTask;
import com.craftedbytes.hazelcast.wan.recon.ClusterRepairTask;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class ReconClient extends ClientHelper{

    public static void main(String args[]){

        //ClientConfig remoteConfig = getUSClientConfig();
        ClientConfig remoteConfig = getCluster2();

        remoteClient = HazelcastClient.newHazelcastClient(remoteConfig);

        Map usCarMap = remoteClient.getMap("cars");

        System.out.println(usCarMap.size());

        //ClientConfig localConfig = getEUClientConfig();
        Config localConfig = getHCCluster1();

        localClient = Hazelcast.newHazelcastInstance(localConfig);
        localClient.getMap("cars");

        ClusterReconTask clusterReconTask = new ClusterReconTask(localClient, localClusterName, remoteClient, remoteClusterName);

        ClusterReconTask clusterReconTask1 = clusterReconTask;

        ClusterReconResults reconResults = null;

        try {
            reconResults = clusterReconTask1.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(reconResults.getMissingKeysByMap().size());

        System.out.println(reconResults.getMissingKeysByMap().toString());

        // Run Repair Task if needed
        if (reconResults.getMissingKeysByMap().size() > 0){
            ClusterRepairTask clusterRepairTask = new ClusterRepairTask(localClient, localClusterName, remoteClient, remoteClusterName, reconResults);
            try {
                clusterRepairTask.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
