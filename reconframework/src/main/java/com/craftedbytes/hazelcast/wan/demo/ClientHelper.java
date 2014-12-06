package com.craftedbytes.hazelcast.wan.demo;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Created by dbrimley on 27/11/14.
 */
public class ClientHelper {

    protected static HazelcastInstance localClient;
    protected static HazelcastInstance remoteClient;
    protected static String localClusterName;
    protected static String remoteClusterName;


    protected static ClientConfig getCluster1() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("localhost:5701");
        clientConfig.getGroupConfig().setName("cluster1").setPassword("cluster1-pass");
        UpdateClient.localClusterName = "cluster1";
        return clientConfig;
    }

    protected static Config getHCCluster1() {
        XmlConfigBuilder builder = new XmlConfigBuilder(ClusterServer.class.getResourceAsStream("/hazelcast-cluster-1.xml"));
        return builder.build();
    }

    protected static ClientConfig getCluster2() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("localhost:5801");
        clientConfig.getGroupConfig().setName("cluster2").setPassword("cluster2-pass");
        UpdateClient.remoteClusterName = "cluster2";
        return clientConfig;
    }

    private static ClientConfig getEUClientConfig() {
        // EU

        ClientConfig EUConfig = new ClientConfig();
        EUConfig.getNetworkConfig().addAddress("ec2-54-75-226-206.eu-west-1.compute.amazonaws.com");
        EUConfig.getGroupConfig().setName("eu-west-1").setPassword("shamrock");
        UpdateClient.localClusterName = "eu-west-1";
        return EUConfig;
    }

    private static ClientConfig getUSClientConfig() {
        // US
        ClientConfig USConfig = new ClientConfig();
        USConfig.getNetworkConfig().addAddress("ec2-54-225-95-142.compute-1.amazonaws.com");
        USConfig.getGroupConfig().setName("us-east-1").setPassword("badabing");
        UpdateClient.remoteClusterName = "us-east-1";
        return USConfig;
    }
}
