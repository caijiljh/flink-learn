import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author jinhong.liu
 * @Description
 * @Date 2021/8/23
 */
public class JudgeDateGap {
    public static void main(String[] args) {
        Calendar instance = Calendar.getInstance();
        long timeInMillis = instance.getTimeInMillis();
        Date nowTime = new Date(1629669599000l);
        long beginTime1=timeInMillis/(60*1000*60*24)*(60*1000*60*24)-(60*1000*60*8);
        long endTime1=timeInMillis/(60*1000*60*24)*(60*1000*60*24)-(60*1000*60*2);
        Date beginTime = new Date(beginTime1);
        Date endTime = new Date(endTime1);

//        System.out.println(nowTime);
//        System.out.println(beginTime);
//        System.out.println(endTime);
//        System.out.println(judgeTimeGap(nowTime,beginTime,endTime));


        System.out.println(getProportionNum("11","1"));
    }

    private static boolean judgeTimeGap(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }
    public static String getProportionNum( String adClickAll ,String unqadClick ) {
        BigDecimal BigDecimalCountAll = new BigDecimal(adClickAll);
        BigDecimal BigDecimalUnqulifiedCount = new BigDecimal(unqadClick);
        BigDecimal divide = BigDecimalUnqulifiedCount.divide(BigDecimalCountAll, 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal multiply = divide.multiply(BigDecimal.valueOf(100));
        String proportion = multiply.doubleValue()+"%";
        return  proportion;
    }
}

