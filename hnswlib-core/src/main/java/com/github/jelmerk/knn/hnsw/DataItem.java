package com.github.jelmerk.knn.hnsw;

/**
 * Created by ZNV on 2019/10/11.
 */

import com.github.jelmerk.knn.Item;

import java.util.Arrays;

public class DataItem implements Item<String, byte[]> {

    private final String id;
    private final byte[] vector;
    private final long version;

    public DataItem(String id, byte[] vector) {
        this(id, vector, 0);
    }

    public DataItem(String id, byte[] vector, long version) {
        this.id = id;
        this.vector = vector;
        this.version = version;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public byte[] vector() {
        return vector;
    }

    @Override
    public long version() {
        return version;
    }

    @Override
    public String toString() {
        return "TestItem{" + "id='" + id + '\'' + ", vector=" + Arrays.toString(vector) + ", version=" + version + '}';
    }
}