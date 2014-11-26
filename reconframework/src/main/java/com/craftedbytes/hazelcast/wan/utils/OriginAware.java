package com.craftedbytes.hazelcast.wan.utils;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

/**
 * Created by dbrimley on 20/10/2014.
 */
public class OriginAware implements DataSerializable{

    private String origin;

    public OriginAware(){}

    public OriginAware(String origin) {
        this.origin = origin;
    }

    public String getOriginGroupConfig(){
        return origin;
    }

    @Override
    public void writeData(ObjectDataOutput out)
            throws IOException {
        out.writeUTF(origin);
    }

    @Override
    public void readData(ObjectDataInput in)
            throws IOException {
        this.origin = in.readUTF();
    }
}
