package com.craftedbytes.hazelcast.wan.demo;

import com.craftedbytes.hazelcast.wan.demo.domain.TestUserKey;
import com.craftedbytes.hazelcast.wan.demo.domain.TestUserValue;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class UpdateClient {

    private static HazelcastInstance hazelcastInstance;

    public static void main(String args[]){

        ClientConfig clientConfig1 = new ClientConfig();
        clientConfig1.getNetworkConfig().addAddress("127.0.0.1:5801");
        clientConfig1.getGroupConfig().setName("cluster2").setPassword("cluster2-pass");
        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig1);

        //Config config = new ClasspathXmlConfig("hazelcast-cluster-2.xml");
        //hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        populateData();
    }

    private static void populateData() {

        IMap<Object, Object> users = hazelcastInstance.getMap("users");

        String origin = hazelcastInstance.getConfig().getGroupConfig().toString();

        int start = new Integer(1000);
        int maxPopulation = start + 100;

        for(int i=start;i<maxPopulation;i++) {
            users.put(new TestUserKey(origin, new Integer(i).toString()), new TestUserValue(new Integer(i).toString()));
        }

    }

}
