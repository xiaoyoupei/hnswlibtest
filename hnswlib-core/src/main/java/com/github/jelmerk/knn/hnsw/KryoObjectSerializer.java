package com.github.jelmerk.knn.hnsw;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.KryoObjectOutput;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;

/**
 * @author xiaoyoupei
 * @date 2021-09-17 09:01
 * @Description：
 */
public class KryoObjectSerializer<T> implements ObjectSerializer<T> {

    private static final long serialVersionUID = 1L;

    static Kryo kryo = new Kryo();
    static {
        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置

        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
        kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置

        //Fix the NPE bug when deserializing Collections.
        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

//    private final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
//        @Override
//        protected Kryo initialValue() {
//            Kryo kryo = new Kryo();
//            kryo.register(aClass, new BeanSerializer<>(kryo, aClass));
//            //引用，对A对象序列化时，默认情况下kryo会在每个成员对象第一次序列化时写入一个数字，
//            // 该数字逻辑上就代表了对该成员对象的引用，如果后续有引用指向该成员对象，
//            // 则直接序列化之前存入的数字即可，而不需要再次序列化对象本身。
//            // 这种默认策略对于成员存在互相引用的情况较有利，否则就会造成空间浪费
//            // （因为没序列化一个成员对象，都多序列化一个数字），
//            // 通常情况下可以将该策略关闭，kryo.setReferences(false);
//            kryo.setReferences(false);
//            //设置是否注册全限定名，
//            kryo.setRegistrationRequired(false);
//            //设置初始化策略，如果没有默认无参构造器，那么就需要设置此项,使用此策略构造一个无参构造器
//            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
//            return kryo;
//        }
//    };

    @Override
    public void write(T item, ObjectOutput out) throws IOException {
        //Kryo kryo = kryoThreadLocal.get();
//        Kryo kryo = new Kryo();
//        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
//        kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //Fix the NPE bug when deserializing Collections.
//        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
//                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
//        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }
        });
        Output output = new Output(objectOutputStream);
        kryo.writeObject(output, item);
        output.flush();

    }

    @Override
    public T read(T item,ObjectInput in) throws IOException, ClassNotFoundException {
        //Kryo kryo = kryoThreadLocal.get();
//        Kryo kryo = new Kryo();
//        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
//        kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
//
//        //Fix the NPE bug when deserializing Collections.
//        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
//                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
//        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        ObjectInputStream objectInputStream = new ObjectInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return in.read();
            }
        });
        Input input = new Input(objectInputStream);
        return (T) kryo.readObject(input,item.getClass());
    }

}
