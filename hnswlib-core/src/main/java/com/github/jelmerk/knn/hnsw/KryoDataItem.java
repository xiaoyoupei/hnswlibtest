package com.github.jelmerk.knn.hnsw;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
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
     * 由于kryo不是线程安全的，所以每个线程都使用独立的kryo
     */
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

    /**
     * kryo序列化
     *
     * @param item the item to write
     * @param out  the ObjectOutput implementation to write to
     * @throws IOException
     */
    public void write(DataItem item, ObjectOutput out) throws IOException {
        Kryo kryo = kryos.get();
        Output output = new Output((OutputStream) out);
        kryo.writeObject(output, item);
        output.flush();
        //Log.TRACE();

    }

    /**
     * kryo反序列化
     *
     * @param in the ObjectInput implementation to read from
     * @return 返回读的对象
     * @throws IOException
     */
    public DataItem read(ObjectInput in) throws IOException {
        Kryo kryo = kryos.get();
        Input input = new Input((InputStream) in);
        //Log.TRACE();
        return kryo.readObject(input, DataItem.class);
    }
}
