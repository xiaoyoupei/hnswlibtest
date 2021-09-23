package com.github.jelmerk.knn.serializabletest;

import com.github.jelmerk.knn.hnsw.DataItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyoupei
 * @date 2021-09-13 15:11
 * @Description：
 */
public class javaSerializable {


    static DataItem dataItem = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        long start = System.currentTimeMillis();
        setSerializable();
        System.out.println("java原生序列化时间:" + (System.currentTimeMillis() - start) + " ms");
        start = System.currentTimeMillis();
        getSerializable();
        System.out.println("java原生反序列化时间:" + (System.currentTimeMillis() - start) + " ms");

    }

    public static void setSerializable() throws IOException {
        File file = new File("F:/ZNV/test.bin");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        for (int i = 0; i < 10000; i++) {
            objectOutputStream.writeObject(new DataItem(String.valueOf(i),intToByteArray(i), 10));
        }
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }


    public static void getSerializable() {
        try {
            FileInputStream fileInputStream = new FileInputStream("F:/ZNV/test.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            while ((dataItem = (DataItem) objectInputStream.readObject()) != null) {
                //System.out.println(dataItem.toString());
                System.out.print(1);
            }
            fileInputStream.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
