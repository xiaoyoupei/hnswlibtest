package com.github.jelmerk.knn.hnsw;

import com.github.jelmerk.knn.Item;

import java.util.Arrays;

public class TestItem implements Item<String, float[]> {

    private final String id;
    private final float[] vector;
    private final long version;

    public TestItem(String id, float[] vector) {
        this(id, vector, 0);
    }

    public TestItem(String id, float[] vector, long version) {
        this.id = id;
        this.vector = vector;
        this.version = version;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public float[] vector() {
        return vector;
    }

    @Override
    public long version() {
        return version;
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "id='" + id + '\'' +
                ", vector=" + Arrays.toString(vector) +
                ", version=" + version +
                '}';
    }
}