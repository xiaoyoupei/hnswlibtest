package com.github.jelmerk.knn.hnsw;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaoyoupei
 * @date 2021-09-17 09:01
 * @Description：kryo序列化DataItem
 */
public class ProtostuffDataItem implements ObjectSerializer<DataItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 避免每次序列化都重新申请Buffer空间
     */
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    /**
     * 缓存Schema
     */
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            //这个schema通过RuntimeSchema进行懒创建并缓存
            //所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

    @Override
    public void write(DataItem item, ObjectOutput out) throws IOException {
        Class<DataItem> clazz = (Class<DataItem>) item.getClass();
        Schema<DataItem> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(item, schema, buffer);
        } finally {
            buffer.clear();
        }
        out.writeObject(data);

    }

    @Override
    public DataItem read(ObjectInput in) throws IOException, ClassNotFoundException {
        byte[] data;
        data = (byte[]) in.readObject();
        Schema<DataItem> schema = getSchema(DataItem.class);
        DataItem obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}
