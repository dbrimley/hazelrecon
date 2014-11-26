package com.craftedbytes.hazelcast.wan.recon;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.hazelcast.nio.serialization.DataSerializable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by dbrimley on 20/10/2014.
 */
public class ClusterReconResults {

    private Multimap<String,DataSerializable> missingKeysByMap = ArrayListMultimap.create();
    private Multimap<String,DataSerializable> differentValuesByMap = ArrayListMultimap.create();
    private String localGroupConfig;
    private String remoteGroupConfig;

    public ClusterReconResults(String localGroupConfig, String remoteGroupConfig) {
        this.localGroupConfig = localGroupConfig;
        this.remoteGroupConfig = remoteGroupConfig;
    }

    public Multimap<String,DataSerializable> getMissingKeysByMap() {
        return missingKeysByMap;
    }

    public Multimap<String,DataSerializable> getDifferentValuesByMap() {
        return differentValuesByMap;
    }

    public String getLocalGroupConfig() {
        return localGroupConfig;
    }

    public String getRemoteGroupConfig() {
        return remoteGroupConfig;
    }

    @Override
    public String toString() {
        return "ClusterReconResults{" +
                "missingKeysByMap=" + missingKeysByMap +
                ", differentValuesByMap=" + differentValuesByMap +
                ", localGroupConfig='" + localGroupConfig + '\'' +
                ", remoteGroupConfig='" + remoteGroupConfig + '\'' +
                '}';
    }
}
