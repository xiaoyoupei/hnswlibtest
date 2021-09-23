package com.github.jelmerk.knn.serializabletest;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.jelmerk.knn.hnsw.DataItem;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaoyoupei
 * @date 2021-09-13 15:41
 * @Description：
 */
public class protostuffSerializable {

    static DataItem dataItem = null;

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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        long start = System.currentTimeMillis();
        setSerializableObject();
        System.out.println("protostuff 序列化时间:" + (System.currentTimeMillis() - start) + " ms");
        start = System.currentTimeMillis();
        getSerializableObject();
        System.out.println("protostuff 反序列化时间:" + (System.currentTimeMillis() - start) + " ms");

    }

    public static void setSerializableObject() throws IOException {


        Schema<DataItem> schema = getSchema(DataItem.class);
        byte[] data = new byte[0];
        File file = new File("F:/ZNV/test.bin");
        if (file.exists()) {
            file.delete();
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
        for (int i = 0; i < 10000; i++) {
            data = ProtostuffIOUtil.toByteArray(new DataItem(String.valueOf(i), intToByteArray(i), 10), schema, buffer);
            buffer.clear();
        }
        objectOutputStream.writeObject(data);
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static void getSerializableObject() throws IOException, ClassNotFoundException {


        byte[] data;
        FileInputStream fileInputStream = new FileInputStream("F:/ZNV/test.bin");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        data = (byte[]) objectInputStream.readObject();
        Schema<DataItem> schema = getSchema(DataItem.class);
        DataItem obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);

        while (obj != null) {
            //System.out.println(dataItem.toString());
            System.out.print(1);
        }

    }

}
