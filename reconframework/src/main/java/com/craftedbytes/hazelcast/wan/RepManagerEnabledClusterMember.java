package com.craftedbytes.hazelcast.wan;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Starts a Hazelcast Cluster node that contains a Replication Manager
 */
public class RepManagerEnabledClusterMember {

    private static HazelcastInstance hazelcastInstance;

    public static void main(String args[]){
        hazelcastInstance = getLocationAwareHazelcastInstance();
        initReplication();
    }

    private static void initReplication() {

        IMap replicationMap = hazelcastInstance.getMap("replication");
        RepManager cluster1RepManager = new RepManager(hazelcastInstance,replicationMap);
        cluster1RepManager.start();

    }

    private static HazelcastInstance getLocationAwareHazelcastInstance() {
        String location = System.getProperty("LOCATION");
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(RepManagerEnabledClusterMember.class.getResourceAsStream(
                "/" + location + ".hazelcast.xml"));
        return Hazelcast.newHazelcastInstance(xmlConfigBuilder.build());
    }

}
