package com.craftedbytes.hazelcast.wan;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.InputStream;

/**
 * Starts a Hazelcast Cluster node that contains a Replication Manager
 * Requires Environment variable LOCATION to be set and a replication map to be present in xml config
 */
public class RepManagerEnabledClusterMember {

    public static final String REPLICATION_MAP = "replication";
    public static final String LOCATION_SYSTEM_PROPERTY = "LOCATION";
    private static HazelcastInstance hazelcastInstance;

    public static void main(String args[]){
        try {
            hazelcastInstance = getLocationAwareHazelcastInstance();
            initReplication();
        } catch(RuntimeException e){
            System.out.println(e);
            System.exit(-1);
        }
    }

    private static void initReplication() {
        IMap replicationMap = hazelcastInstance.getMap(REPLICATION_MAP);
        RepManager cluster1RepManager = new RepManager(hazelcastInstance,replicationMap);
        cluster1RepManager.start();
    }

    private static HazelcastInstance getLocationAwareHazelcastInstance() {
        String location = getLocationEnvironmentVariable();
        InputStream inputStream = getLocationAwareXMLConfig(location);
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(inputStream);
        return Hazelcast.newHazelcastInstance(xmlConfigBuilder.build());
    }

    private static InputStream getLocationAwareXMLConfig(String location) {
        String xmlLocation = "/" + location + ".hazelcast.xml";
        InputStream inputStream = RepManagerEnabledClusterMember.class
                .getResourceAsStream(xmlLocation);
        if (inputStream == null){
            throw new RuntimeException("Could not find inputStream  " + xmlLocation);
        }
        return inputStream;
    }

    private static String getLocationEnvironmentVariable() {
        String location = System.getProperty(LOCATION_SYSTEM_PROPERTY,null);
        if (location == null){
            throw new RuntimeException("Could not find environment variable " + LOCATION_SYSTEM_PROPERTY);
        }
        return location;
    }

}
