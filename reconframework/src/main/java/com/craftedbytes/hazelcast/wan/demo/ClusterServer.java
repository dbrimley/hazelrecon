package com.craftedbytes.hazelcast.wan.demo;

import com.craftedbytes.hazelcast.wan.RepManager;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Created by dbrimley on 27/11/14.
 */
public class ClusterServer {
    public static void main (String args[ ]){
        XmlConfigBuilder builder = new XmlConfigBuilder(ClusterServer.class.getResourceAsStream("/hazelcast-cluster-" + args[0] + ".xml"));
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(builder.build());
        instance.getMap("cars");
        IMap replicationMap = instance.getMap("replication");
        RepManager cluster1RepManager = new RepManager(instance,replicationMap);
        cluster1RepManager.start();
    }
}
