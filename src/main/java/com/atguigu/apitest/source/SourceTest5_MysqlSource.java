package com.atguigu.apitest.source;

import com.atguigu.apitest.beans.SensorReading;

import com.atguigu.apitest.beans.Stu;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import scala.beans.BeanProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @Author jinhong.liu
 * @Description source_udf
 * @Date 2021/7/4
 */
public class SourceTest5_MysqlSource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Stu> stuDataStreamSource = env.addSource(new MysqlSource());
        stuDataStreamSource.print().setParallelism(1);
        env.execute("SourceTest5_MysqlSource");
    }

    public static class MysqlSource extends RichParallelSourceFunction<Stu>{


       private PreparedStatement ps=null;

        private Connection connection=null;

        private ResultSet rs=null;
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://192.168.226.104:3306/flink";
            String username = "root";
            String password = "123456";
            Class.forName(driver);
            try {
               connection = DriverManager.getConnection(url, username, password);
                String sql="select * from stu";
                 ps = connection.prepareStatement(sql);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(SourceContext<Stu> ctx) throws Exception {
        try{
            rs = ps.executeQuery();
            while (rs.next()){
                Stu stu = new Stu(rs.getInt("id"), rs.getString("name"));
                ctx.collect(stu);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }

        @Override
        public void close() throws Exception {
            super.close();
            if(rs!=null)rs.close();
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }

        @Override
        public void cancel() {

        }
    }
}
