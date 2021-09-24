package com.github.jelmerk.knn.hnsw;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyoupei
 * @date 2021-09-17 09:01
 * @Description：
 */
public class KryoString  implements ObjectSerializer<String> {

    private static final long serialVersionUID = 1L;

    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();

            kryo.setReferences(false); ////支持循环引用，默认值就是 true

            kryo.setRegistrationRequired(true); //关闭注册行为，默认值就是 false
            kryo.register(com.github.jelmerk.knn.hnsw.DataItem.class);
            kryo.register(byte[].class);

            //Fix the NPE bug when deserializing Collections.
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            //kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };


    public void write(String item, ObjectOutput out) throws IOException {
        Kryo kryo = kryos.get();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }
        });
        Output output = new Output(objectOutputStream);
        kryo.writeObject(output, item);
        output.close();
    }


    public String read(ObjectInput in) throws IOException {
        //Kryo kryo = kryoThreadLocal.get();
//        Kryo kryo = new Kryo();
//        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
//        kryo.setRegistrationRequired(true); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //Fix the NPE bug when deserializing Collections.
//        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
//                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
//        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Kryo kryo = kryos.get();
        ObjectInputStream objectInputStream = new ObjectInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return in.read();
            }
        });
        Input input = new Input(objectInputStream);
        return  kryo.readObject(input,String.class);
    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
//        /**
//         * java序列化String
//         */
////        String string = "znv666";
////        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("F:\\\\ZNV\\\\test.txt"));
////        oos.writeObject(string);
//
////        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("F:\\\\ZNV\\\\test.txt"));
////        String o = (String)ois.readObject();
////        System.out.println(o);
//
//        /**
//         * kryo序列化String
//         */
//        Kryo kryo = new Kryo();
//        kryo.setReferences(true);
//        kryo.setRegistrationRequired(false);
//        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
//                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
//        String string = "znv666";
//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("F:\\ZNV\\test.txt"));
//        Output output = new Output(oos);
//        kryo.writeObject(output,string);
//        output.close();
//
//
//        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("F:\\ZNV\\test.txt"));
//        Input input = new Input(ois);
//        String o = kryo.readObject(input, String.class);
//        System.out.println(o);
//
//    }
}
