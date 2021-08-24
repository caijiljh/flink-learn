package com.atguigu.apitest.window;/**
 * Copyright (c) 2018-2028 尚硅谷 All Rights Reserved
 * <p>
 * Project: FlinkTutorial
 * Package: com.atguigu.apitest.window
 * Version: 1.0
 * <p>
 * Created by wushengran on 2020/11/9 16:19
 */

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: WindowTest2_CountWindow
 * @Description:
 * @Author: wushengran on 2020/11/9 16:19
 * @Version: 1.0
 */
public class WindowTest2_CountWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

//        // 从文件读取数据
//        DataStream<String> inputStream = env.readTextFile("D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\sensor.txt");

        // socket文本流
        DataStream<String> inputStream = env.socketTextStream("localhost", 7777);

        // 转换成SensorReading类型
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        // 开计数窗口测试
        SingleOutputStreamOperator<Double> avgTempResultStream = dataStream.keyBy("id")
                .countWindow(10, 2)
                .aggregate(new MyAvgTemp());

        avgTempResultStream.print();

        env.execute();
    }

    public static class MyAvgTemp implements AggregateFunction<SensorReading, Tuple2<Double, Integer>, Double>{
        @Override
        public Tuple2<Double, Integer> createAccumulator() {
            return new Tuple2<>(0.0, 0);
        }

        @Override
        public Tuple2<Double, Integer> add(SensorReading value, Tuple2<Double, Integer> accumulator) {

            return new Tuple2<>(accumulator.f0 + value.getTemperature(), accumulator.f1 + 1);
        }

        @Override
        public Double getResult(Tuple2<Double, Integer> accumulator) {
            return accumulator.f0 / accumulator.f1;
        }

        @Override
        public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> a, Tuple2<Double, Integer> b) {
            return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
        }
    }


    /**
     * 带超时的计数窗口触发器
     */
    public static class CountTriggerWithTimeout<T> extends Trigger<T, TimeWindow> {

        /**
         * 窗口大小
         */
        private int maxSize;
        /**
         * 时间类型
         */
        private TimeCharacteristic timeType;

        private ReducingStateDescriptor<Long> countStateDescriptor =
                new ReducingStateDescriptor("counter", new Sum(), LongSerializer.INSTANCE);


        public CountTriggerWithTimeout(int maxSize, TimeCharacteristic timeType) {
            this.maxSize = maxSize;
            this.timeType = timeType;
        }

        private TriggerResult fireAndPurge(TimeWindow window, TriggerContext ctx) throws Exception {
            clear(window, ctx);
            return TriggerResult.FIRE_AND_PURGE;
        }


        /**
         * 当窗口每读取一个元素就会调用此方法
         */
        @Override
        public TriggerResult onElement(T element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
            ReducingState<Long> countState = ctx.getPartitionedState(countStateDescriptor);
            countState.add(1L);//计数器+1

            if (countState.get() >= maxSize) { //当计数次数满足条件时候 先计算，然后输出结果，最后将窗口中的数据和窗口清除
                return fireAndPurge(window, ctx);
            }
            if (timestamp >= window.getEnd()) { //当超时时 先计算，然后输出结果，最后将窗口中的数据和窗口清除
                return fireAndPurge(window, ctx);
            } else {
                return TriggerResult.CONTINUE;
            }
        }

        /**
         * 当ProcessingTime定时器触发的时候就会调用此方法
         */
        @Override
        public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {

            if (timeType != TimeCharacteristic.ProcessingTime) {
                return TriggerResult.CONTINUE;
            }
            if (time >= window.getEnd()) {
                return TriggerResult.CONTINUE;
            } else {
                return fireAndPurge(window, ctx);
            }
        }

        /**
         * 当EventTime定时器被触发的时候就会调用此方法
         */
        @Override
        public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            if (timeType != TimeCharacteristic.EventTime) {
                return TriggerResult.CONTINUE;
            }

            if (time >= window.getEnd()) {
                return TriggerResult.CONTINUE;
            } else {
                return fireAndPurge(window, ctx);
            }
        }

        /**
         * 窗口清理的时候调用
         */
        @Override
        public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
            ReducingState<Long> countState = ctx.getPartitionedState(countStateDescriptor);
            countState.clear();
        }


        class Sum implements ReduceFunction<Long> {
            @Override
            public Long reduce(Long value1, Long value2) throws Exception {
                return value1 + value2;
            }
        }

    }


}
