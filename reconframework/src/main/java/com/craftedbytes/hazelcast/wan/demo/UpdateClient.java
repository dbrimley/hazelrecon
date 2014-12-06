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

import java.util.Map;

public class UpdateClient
        extends ClientHelper {


    public static void main(String args[]){
        //ClientConfig remoteConfig = getUSClientConfig();
        ClientConfig remoteConfig = getCluster2();

        remoteClient = HazelcastClient.newHazelcastClient(remoteConfig);

        Map usCarMap = remoteClient.getMap("cars");

        System.out.println(usCarMap.size());

        //ClientConfig localConfig = getEUClientConfig();
        ClientConfig localConfig = getCluster1();

        localClient = HazelcastClient.newHazelcastClient(localConfig);

        Map euCarMap = localClient.getMap("cars");

        System.out.println(euCarMap.size());

        populateData();

        System.exit(0);
    }

    private static void populateData() {

        IMap<Object, Object> cars = localClient.getMap("cars");

        int start = new Integer(2000);
        int maxPopulation = start + 1000;

        for(int i=start;i<maxPopulation;i++) {
            cars.put(new TestUserKey(localClusterName, new Integer(i).toString()), new TestUserValue(new Integer(i).toString()));
            System.out.println(cars.size());
        }


    }

}
