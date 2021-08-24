import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Author jinhong.liu
 * @Description
 * @Date 2021/8/13
 */
public class niubi {
    public static void main(String[] args) {
        String windowEnd = new Timestamp(1628839289000l).toString();
        windowEnd="2021-08-16 17:48:00";

        //String windowEnd="2017-01-01 10:20:11.0";
        String timestr =windowEnd;
        String datastr = windowEnd.substring(0, windowEnd.length() - 9);
        String hourstr =windowEnd.substring(11, windowEnd.length() - 6);
        String minstr=windowEnd.substring(14,windowEnd.length()-3);
        System.out.println(timestr);
        System.out.println(datastr);
        System.out.println(hourstr);
        System.out.println(minstr);

        System.out.println("***************");
        System.out.println(Calendar.getInstance().getTimeInMillis());
        SimpleDateFormat DATE_HH =new
                SimpleDateFormat("yyyy-MM-dd HH");
        String format = DATE_HH.format(Calendar.getInstance().getTimeInMillis());
        System.out.println(format);
    }
}
