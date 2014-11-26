package com.craftedbytes.hazelcast.wan.utils;

import com.craftedbytes.hazelcast.MockHazelcastInstance;
import com.craftedbytes.hazelcast.MockIMap;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.DataSerializable;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for Replication Manager
 */
public class RepManagerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionOnNullHazelcastInstance () {
        new RepManager(null,"repMapName","repSchemeRef");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionOnNullRepMapName () {
        new RepManager(new MockHazelcastInstance(), null, "repSchemeRef");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionOnNullRepSchemeRef () {
        new RepManager(new MockHazelcastInstance(), "repMapName", null);
    }

    @Test
    public void willPassMapPutIntoRepMap(){


        HazelcastInstance hcInstance = getRealHazelcastInstance();
        String origin = hcInstance.getConfig().getGroupConfig().toString();

        RepManager classUnderTest = new RepManager(hcInstance,"repMapName","repSchemeRef");

        classUnderTest.start();

        IMap<RepKey, DataSerializable> repMap = classUnderTest.getRepMap();

        IMap<TestUserKey, TestUserValue> someUserMap = hcInstance.getMap("someUserMap");
        someUserMap.put(new TestUserKey(origin, "key"), new TestUserValue("value"));

        assertTrue(repMap.size() == 1);

    }

    @Test
    public void willReplicateToOtherClustersUserMap()
            throws InterruptedException {

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

        CountDownLatch countDownLatch1 = new CountDownLatch(2);
        CountDownLatch countDownLatch2 = new CountDownLatch(2);
        CountdownEntryListener countdownEntryListener1 = new CountdownEntryListener(countDownLatch1, hazelcastInstance1.getConfig().getGroupConfig().getName());
        CountdownEntryListener countdownEntryListener2 = new CountdownEntryListener(countDownLatch2, hazelcastInstance2.getConfig().getGroupConfig().getName());


        replicationMap1.addEntryListener(countdownEntryListener1,true);
        userMap1.addEntryListener(countdownEntryListener1,true);

        replicationMap2.addEntryListener(countdownEntryListener2,true);
        userMap2.addEntryListener(countdownEntryListener2,true);

        String origin = hazelcastInstance1.getConfig().getGroupConfig().toString();

        TestUserKey key = new TestUserKey(origin,"key");
        TestUserValue value = new TestUserValue("value");

        userMap1.put(key, value);

        boolean tripped1 = countDownLatch1.await(10, TimeUnit.MINUTES);
        assertTrue(tripped1);


        boolean tripped2 = countDownLatch2.await(10, TimeUnit.MINUTES);
        assertTrue(tripped2);

    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private HazelcastInstance getHazelcastInstanceWithThreeMaps() {

        Map<String,IMap> maps = new HashMap<String,IMap>();

        IMap testIMap1 = new MockIMap("testImap1");
        IMap testIMap2 = new MockIMap("testImap2");
        IMap testIMap3 = new MockIMap("testImap3");

        maps.put(testIMap1.getName(),testIMap1);
        maps.put(testIMap2.getName(),testIMap2);
        maps.put(testIMap3.getName(),testIMap3);

        return new MockHazelcastInstance(maps);
    }

    private HazelcastInstance getRealHazelcastInstance(){
        return Hazelcast.newHazelcastInstance();
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
            System.out.println(cluster + ":" + event.getKey());
            countDownLatch.countDown();
        }

        @Override
        public void entryMerged(EntryEvent<K, V> event) {
            System.out.println(cluster + ":" + event.getKey());
            countDownLatch.countDown();
        }
    }
}
