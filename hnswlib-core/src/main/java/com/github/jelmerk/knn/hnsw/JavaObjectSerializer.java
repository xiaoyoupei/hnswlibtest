package com.github.jelmerk.knn.hnsw;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 使用java序列化来写值的实现
 * Implementation of {@link ObjectSerializer} that uses java serialization to write the value.
 *
 * @param <T> type of object to serialize
 */
public class JavaObjectSerializer<T> implements ObjectSerializer<T> {

    private static final long serialVersionUID = 1L; //序列化的版本号

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(T item, ObjectOutput out) throws IOException {
        out.writeObject(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T read(ObjectInput in) throws IOException, ClassNotFoundException {
        return (T) in.readObject();
    }

}
