package com.github.jelmerk.knn.util;

import com.github.jelmerk.knn.hnsw.DataItem;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import javax.xml.crypto.Data;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaoyoupei
 * @date 2021-09-23 13:43
 * @Description：
 */
public class ProtostuffUtils {
    /**
     * 避免每次序列化都重新申请Buffer空间
     */
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    /**
     * 缓存Schema
     *
     */
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    /**
     * 序列化方法，把指定对象序列化成字节数组
     *
     * @param obj
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }

        return data;
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

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

    public static void main(String[] args){
        DataItem dataItem =new DataItem("1", new byte[] { 11,22 }, 10);
        byte[] serialize = serialize(dataItem);
        DataItem deserialize = deserialize(serialize, DataItem.class);
        System.out.println(deserialize.toString());

    }
}
