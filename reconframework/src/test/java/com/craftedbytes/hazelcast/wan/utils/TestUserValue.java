package com.craftedbytes.hazelcast.wan.utils;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

/**
 * Created by dbrimley on 03/10/2014.
 */
public class TestUserValue implements DataSerializable {

    private String value;

    public TestUserValue(){}

    public TestUserValue(String value) {
        this.value = value;
    }

    @Override
    public void writeData(ObjectDataOutput out)
            throws IOException {
        out.writeUTF(value);
    }

    @Override
    public void readData(ObjectDataInput in)
            throws IOException {
        this.value = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestUserValue that = (TestUserValue) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
