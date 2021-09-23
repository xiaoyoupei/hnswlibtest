package com.github.jelmerk.knn.serializabletest;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.jelmerk.knn.hnsw.DataItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyoupei
 * @date 2021-09-13 15:41
 * @Description：
 */
public class kryoSerializable {

    static Kryo kryo = new Kryo();
    static DataItem dataItem = null;

    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        setSerializableObject();
        System.out.println("Kryo 序列化时间:" + (System.currentTimeMillis() - start) + " ms");
        start = System.currentTimeMillis();
        getSerializableObject();
        System.out.println("Kryo 反序列化时间:" + (System.currentTimeMillis() - start) + " ms");

    }

    public static void setSerializableObject() throws FileNotFoundException {


        kryo.setReferences(false); ////支持循环引用，默认值就是 true
        kryo.setRegistrationRequired(true); //关闭注册行为，默认值就是 false
        kryo.register(com.github.jelmerk.knn.hnsw.DataItem.class);
        kryo.register(byte[].class);


        File file = new File("F:/ZNV/test.bin");
        if (file.exists()) {
            file.delete();
        }
        Output output = new Output(new FileOutputStream(file),1000000000);
        for (int i = 0; i < 10000; i++) {
            kryo.writeObject(output, new DataItem(String.valueOf(i),intToByteArray(i), 10));
        }
        output.flush();
        output.close();
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static void getSerializableObject() {

        Input input;
        try {
            input = new Input(new FileInputStream("F:/ZNV/test.bin"));

            while ((dataItem = kryo.readObject(input, DataItem.class)) != null) {
                //System.out.println(dataItem.toString());
                System.out.print(1);
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (KryoException e) {
            e.printStackTrace();
        }
    }

}
