package com.craftedbytes.hazelcast.wan.utils;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

/**
 * Created by dbrimley on 03/10/2014.
 */
public class RepKey implements DataSerializable {

    private DataSerializable userKey;
    private String destinationMap;

    public RepKey(){}

    public RepKey(DataSerializable userKey, String destinationMap){
        this.userKey = userKey;
        this.destinationMap = destinationMap;
    }

    public DataSerializable getUserKey() {
        return userKey;
    }

    public String getDestinationMap() {
        return destinationMap;
    }

    @Override
    public void writeData(ObjectDataOutput out)
            throws IOException {
        out.writeObject(userKey);
        out.writeUTF(destinationMap);
    }

    @Override
    public void readData(ObjectDataInput in)
            throws IOException {
        this.userKey = in.readObject();
        this.destinationMap = in.readUTF();
    }

}
