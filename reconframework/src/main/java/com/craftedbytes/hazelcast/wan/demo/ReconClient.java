package com.craftedbytes.hazelcast.wan.demo;

import com.craftedbytes.hazelcast.wan.recon.ClusterReconResults;
import com.craftedbytes.hazelcast.wan.recon.ClusterReconTask;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

public class ReconClient {

    public static void main(String args[]){

        ClientConfig clientConfig1 = new ClientConfig();
        clientConfig1.getNetworkConfig().addAddress("127.0.0.1:5801");
        HazelcastInstance localInstance = HazelcastClient.newHazelcastClient(clientConfig1);


        ClientConfig clientConfig2 = new ClientConfig();
        clientConfig2.getNetworkConfig().addAddress("127.0.0.1:5701");
        HazelcastInstance remoteInstance = HazelcastClient.newHazelcastClient(clientConfig2);

        ClusterReconTask clusterReconTask = new ClusterReconTask(localInstance, remoteInstance);

        ClusterReconTask clusterReconTask1 = clusterReconTask;

        ClusterReconResults reconResults = null;

        try {
            reconResults = clusterReconTask1.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(reconResults);

    }

}
