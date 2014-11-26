package com.craftedbytes.hazelcast.wan.demo;

import com.craftedbytes.hazelcast.wan.demo.domain.TestUserKey;
import com.craftedbytes.hazelcast.wan.demo.domain.TestUserValue;
import com.craftedbytes.hazelcast.wan.utils.RepManager;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Starts a Hazelcast Cluster node that contains a Replication Manager
 */
public class ClusterMember {

    private static HazelcastInstance hazelcastInstance;
    private static String keyStart;

    public static void main(String args[]){

        keyStart = args[1];

        hazelcastInstance = getRealHazelcastInstance(args[0]);

        initReplication();

        populateData();

    }

    private static void populateData() {

        IMap<Object, Object> users = hazelcastInstance.getMap("users");

        String origin = hazelcastInstance.getConfig().getGroupConfig().toString();

        int start = new Integer(keyStart);
        int maxPopulation = start + 100;

        for(int i=new Integer(keyStart);i<maxPopulation;i++) {
            users.put(new TestUserKey(origin, new Integer(i).toString()), new TestUserValue(new Integer(i).toString()));
        }

    }

    private static void initReplication() {

        IMap replicationMap = hazelcastInstance.getMap("replication");

        RepManager cluster1RepManager = new RepManager(hazelcastInstance,replicationMap);
        IMap<TestUserKey, TestUserValue> userMap1 = hazelcastInstance.getMap("users");
        cluster1RepManager.start();

    }

    private static HazelcastInstance getRealHazelcastInstance(String configXML){
        Config config = new ClasspathXmlConfig(configXML);
        return Hazelcast.newHazelcastInstance(config);
    }

}
