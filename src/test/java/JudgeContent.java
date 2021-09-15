
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author jinhong.liu
 * @Description source_udf
 * @Date 2021/7/4
 */
public class JudgeContent {
    public static void main(String[] args) throws Exception {

        Connection co = null;
        PreparedStatement st = null;
        ResultSet re = null;
        File f = new File("D:\\MyCode\\learnspace\\FlinkTutorial\\FlinkTutorial\\src\\test\\java\\mycode.txt");

        List<String> lines = FileUtils.readLines(f, "UTF-8");
        List<String> ddd=new ArrayList<>();
        System.out.println(lines);
        //注册驱动
        Class.forName("com.mysql.jdbc.Driver");
        //获取连接
        co = DriverManager.getConnection("jdbc:mysql://192.168.10.7:3306/weatherdatadb", "root", "");

        String sql = "select\n" +
                "t1.Fid c1,pp.Fcityname_cn c2,t1.Fcityname_cn c3\n" +
                "from\n" +
                "(select\n" +
                "c.Fid,c.Fcityname_cn,c.Fparent\n" +
                "from city c join city_code cc on cc.Finternal= ? and c.Fid=cc.Fcity_id) as t1 join weatherdatadb.city pp on t1.Fparent=pp.Fid;";

        //获取传输
        st = co.prepareStatement(sql);
        for (String line : lines) {

            st.setInt(1, Integer.parseInt(line));
            re = st.executeQuery();
            //执行sql
            while (re.next()) {
                        String stu = line+"   " + re.getInt("c1") + "   " + re.getString("c2") + "   " + re.getString("c3");
                        ddd.add(stu);
                    }

        }


        System.out.println(ddd);
    }
}




