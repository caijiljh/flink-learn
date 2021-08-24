package com.atguigu.apitest.partitioner;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import redis.clients.jedis.Tuple;

/**
 * @Author jinhong.liu
 * @Description
 * @Date 2021/7/6
 */
public class PartitionerTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> data = env.socketTextStream("192.168.226.104", 6666);
        //随机
        //data.shuffle().print().setParallelism(4);
        //轮询的方式
        //data.rebalance().print().setParallelism(4);
        data.rescale().print().setParallelism(4);
        //data.print().setParallelism(4);
        env.execute("MyPartitioner");


    }
    class MyFlatMapFunction extends RichFlatMapFunction<Tuple2<Long,Long>,Tuple2<Long,Long>>{
            ValueState<Tuple2<Long,Long>> state;
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<>("state", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
            }));
            state = getRuntimeContext().getState(descriptor);
        }



        @Override
        public void close() throws Exception {
            super.close();
        }

        @Override
        public void flatMap(Tuple2<Long, Long> value, Collector<Tuple2<Long, Long>> out) throws Exception {

        }
    }
}
