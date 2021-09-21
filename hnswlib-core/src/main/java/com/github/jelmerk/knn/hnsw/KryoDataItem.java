package com.github.jelmerk.knn.hnsw;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;

/**
 * @author xiaoyoupei
 * @date 2021-09-17 09:01
 * @Description：kryo序列化DataItem
 */
public class KryoDataItem implements ObjectSerializer<DataItem> {

    private static final long serialVersionUID = 1L;

    /**
     * ThreadLocal牺牲空间来换取并发线程安全
     */
    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();

            kryo.setReferences(true); ////支持循环引用，默认值就是 true

            kryo.setRegistrationRequired(false); //关闭注册行为，默认值就是 false

            //Fix the NPE bug when deserializing Collections.
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };


//    static Kryo kryo = new Kryo();
//
//    static {
//        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
//        kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //Fix the NPE bug when deserializing Collections.
//        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
//                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
//        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
//    }

    /**
     * kryo写序列化
     *
     * @param item the item to write
     * @param out  the ObjectOutput implementation to write to
     * @throws IOException
     */
    public void write(DataItem item, ObjectOutput out) throws IOException {
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

    /**
     * kryo读序列化
     *
     * @param in the ObjectInput implementation to read from
     * @return 返回读的对象
     * @throws IOException
     */
    public DataItem read(ObjectInput in) throws IOException {
        Kryo kryo = kryos.get();
        ObjectInputStream objectInputStream = new ObjectInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return in.read();
            }
        });
        Input input = new Input(objectInputStream);
        return kryo.readObject(input, DataItem.class);
    }
}
