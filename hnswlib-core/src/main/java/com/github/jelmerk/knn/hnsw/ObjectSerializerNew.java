package com.github.jelmerk.knn.hnsw;

import java.io.*;

/**
 * Implementations of this interface are used to customize how objects will be stored when the index is persisted
 *
 * @param <T> type of object to serialize.
 */
public interface ObjectSerializerNew<T> extends Serializable {

    /**
     * Writes the item to an ObjectOutput implementation.
     *
     * @param item the item to write
     * @param out the ObjectOutput implementation to write to
     * @throws IOException in case of an I/O exception
     */
    void write(T item, OutputStream out) throws IOException;

    /**
     * Reads an item from an ObjectOutput implementation.
     *
     * @param in the ObjectInput implementation to read from
     * @return the read item
     * @throws IOException in case of an I/O exception
     * @throws ClassNotFoundException in case the value read does not match the type of item
     */
    T read(InputStream in) throws IOException, ClassNotFoundException;

}
