package com.craftedbytes.hazelcast.wan.recon;

import com.craftedbytes.hazelcast.wan.utils.RepKey;
import com.craftedbytes.hazelcast.wan.utils.RepManager;
import com.craftedbytes.hazelcast.wan.utils.TestUserKey;
import com.craftedbytes.hazelcast.wan.utils.TestUserValue;
import com.google.common.collect.Multimap;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.DataSerializable;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ClusterReconTaskTest {

    @Test
    public void willShowMissingKeysInRemoteCluster(){


        HazelcastInstance hazelcastInstance1 = getRealHazelcastInstance("hazelcast-cluster-1.xml");
        IMap replicationMap1 = hazelcastInstance1.getMap("replication");

        HazelcastInstance hazelcastInstance2 = getRealHazelcastInstance("hazelcast-cluster-2.xml");
        IMap replicationMap2 = hazelcastInstance2.getMap("replication");

        RepManager cluster1RepManager = new RepManager(hazelcastInstance1,replicationMap1);
        IMap<TestUserKey, TestUserValue> userMap1 = hazelcastInstance1.getMap("users");
        cluster1RepManager.start();

        RepManager cluster2RepManager = new RepManager(hazelcastInstance2,replicationMap2);
        IMap<TestUserKey, TestUserValue> userMap2 = hazelcastInstance2.getMap("users");
        cluster2RepManager.start();

        // Wait for the second entry to hit the remote cluster
        CountDownLatch countDownLatch = new CountDownLatch(2);
        userMap1.addEntryListener(new CountdownEntryListener(countDownLatch,"localCluster"),true);
        userMap2.addEntryListener(new CountdownEntryListener(countDownLatch, "remoteCluster"), true);

        String origin = hazelcastInstance1.getConfig().getGroupConfig().toString();

        // Now run another put so this one makes it across
        TestUserKey key1 = new TestUserKey(origin,"key1");
        TestUserValue value1 = new TestUserValue("value1");
        userMap1.put(key1, value1);

        boolean recordArrived = false;
        try {
            recordArrived = countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue("Second record did not make it to remote cluster",recordArrived);

        // Now stop the RepManager
        cluster1RepManager.stop();

        // Now put another record into cluster 1 but it shouldn't make cluster 2
        TestUserKey key2 = new TestUserKey(origin,"key2");
        TestUserValue value2 = new TestUserValue("value2");
        userMap1.put(key2, value2);

        // Now run the ClusterReconTask

        ClusterReconTask clusterReconTask = new ClusterReconTask(hazelcastInstance1,hazelcastInstance2);
        ClusterReconResults clusterReconResults = null;
        try {
            clusterReconResults = clusterReconTask.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Multimap<String, DataSerializable> missingKeysByMap = clusterReconResults.getMissingKeysByMap();

        assertTrue(missingKeysByMap.containsKey("users"));

        Collection<DataSerializable> users = missingKeysByMap.get("users");

        assertTrue(users.contains(key2));

        System.out.println(clusterReconResults);

        // Now start up the RepManager and play over the missing entries.
        cluster1RepManager.start();

        // Wait for the repair entry to hit the remote cluster
        CountDownLatch repairDownLatch = new CountDownLatch(2);
        userMap1.addEntryListener(new CountdownEntryListener(repairDownLatch,"localCluster"),true);
        userMap2.addEntryListener(new CountdownEntryListener(repairDownLatch, "remoteCluster"), true);

        // Send the Repair
        System.out.println("Sending Repair Event");
        DataSerializable dataSerializableValue = userMap1.get(key2);
        replicationMap1.set(new RepKey(key2, "users"), dataSerializableValue);

        System.out.println("finished");

    }

    private HazelcastInstance getRealHazelcastInstance(String configXML){
        Config config = new ClasspathXmlConfig(configXML);
        return Hazelcast.newHazelcastInstance(config);
    }

    private class CountdownEntryListener<K,V> extends EntryAdapter<K,V> {

        private CountDownLatch countDownLatch;
        private String cluster;

        public CountdownEntryListener(CountDownLatch countDownLatch, String cluster){

            this.countDownLatch = countDownLatch;
            this.cluster = cluster;
        }

        @Override
        public void entryAdded(EntryEvent<K, V> event) {
            System.out.println("Added " + cluster + ":" + event.getKey());
            countDownLatch.countDown();
            System.out.println("Latch is now " + countDownLatch.getCount());
        }

        @Override
        public void entryMerged(EntryEvent<K, V> event) {
            System.out.println("Merged " + cluster + ":" + event.getKey());
            countDownLatch.countDown();
            System.out.println("Latch is now " + countDownLatch.getCount());
        }
    }

}