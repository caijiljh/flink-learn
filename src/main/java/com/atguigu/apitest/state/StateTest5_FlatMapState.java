package com.atguigu.apitest.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * @Author jinhong.liu
 * @Description
 * @Date 2021/7/6
 */
public class StateTest5_FlatMapState {
    public static void main(String[] args) {
        

    }
    class MyFlatMapFunction extends RichFlatMapFunction<Tuple2<Long,Long>,Tuple2<Long,Long>> {
        ValueState<Tuple2<Long,Long>> state;
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<>("state", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
            }), new Tuple2(0l,0l));
            state = getRuntimeContext().getState(descriptor);
        }



        @Override
        public void close() throws Exception {
            super.close();
        }

        @Override
        public void flatMap(Tuple2<Long, Long> value, Collector<Tuple2<Long, Long>> out) throws Exception {
            Tuple2<Long, Long> value1 = state.value();
           Long count =value1.f0 + 1 ;
           Long sum= value1.f1+value.f1;
           state.update(new Tuple2<>(count,sum));
            out.collect(new Tuple2<>(value.f0,sum));
        }
    }
}
