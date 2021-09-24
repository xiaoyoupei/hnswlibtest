package com.github.jelmerk.knn.hnsw;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;

/**
 * @author xiaoyoupei
 * @date 2021-09-17 09:01
 * @Description：
 */
public class KryoStringNew implements ObjectSerializerNew<String> {

    private static final long serialVersionUID = 1L;

    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();

            kryo.setReferences(false); ////支持循环引用，默认值就是 true

            kryo.setRegistrationRequired(true); //关闭注册行为，默认值就是 false
            kryo.register(DataItem.class);
            kryo.register(byte[].class);

            //Fix the NPE bug when deserializing Collections.
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            //kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };


    public void write(String item, OutputStream out) throws IOException {
        Kryo kryo = kryos.get();
        Output output = new Output(out);
        kryo.writeObject(output, item);

        output.flush();
        output.close();
        out.close();

    }


    public String read(InputStream in) throws IOException {
        Kryo kryo = kryos.get();
        Input input = new Input(in);
        return  kryo.readObject(input,String.class);
    }
}
