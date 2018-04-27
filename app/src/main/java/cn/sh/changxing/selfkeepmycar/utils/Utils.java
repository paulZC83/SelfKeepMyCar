package cn.sh.changxing.selfkeepmycar.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sh.changxing.selfkeepmycar.db.model.Record;
import cn.sh.changxing.selfkeepmycar.db.model.RecordBmob;

/**
 * Created by ZengChao on 2017/11/16.
 */

public class Utils {
    public static double parseDouble(double d){
        BigDecimal b = new BigDecimal(d);
        return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static String getMonday(Date time) {



        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式

        Calendar cal = Calendar.getInstance();

        cal.setTime(time);

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了

        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天

        if(1 == dayWeek) {

            cal.add(Calendar.DAY_OF_MONTH, -1);

        }

        System.out.println("要计算日期为:"+sdf.format(cal.getTime())); //输出要计算日期

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        Log.d("TAG", "当前日期是一个星期的第几天："+day);

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        String imptimeBegin = sdf.format(cal.getTime()); //周一时间

        Log.d("TAG", "周一是："+imptimeBegin);
        getFirstDayOfMonth(time);
        return imptimeBegin;


    }

    /**
     * 返回指定日期的月的第一天
     *
     * @param date
     * @return
     */
    public static String getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), 1);
        String firstDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        Log.d("TAG", "本月1号是："+firstDayOfMonth);
        return firstDayOfMonth;
    }

    /**
     * 返回指定日期的月的前一月的第一天
     *
     * @param date
     * @return
     */
    public static String getFirstDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)-1, 1);
        String firstDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        Log.d("TAG", "上月1号是："+firstDayOfMonth);
        return firstDayOfMonth;
    }

    /**
     * 返回指定日期的月的前一月的第一天
     *
     * @param date
     * @return
     */
    public static String getFirstDayOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1, 1);
        String firstDayOfMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        Log.d("TAG", "上月1号是："+firstDayOfMonth);
        return firstDayOfMonth;
    }

    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,1);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getDayOfWeek(String strDate){
        StringBuffer buffer = new StringBuffer();
        buffer.append("星期");
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;
        try {
            date = formatter.parse(strDate);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            String zhDate="";
            switch (calendar.get(Calendar.DAY_OF_WEEK)-1){
                case 0:
                    zhDate = "日";
                    break;
                case 1:
                    zhDate = "一";
                    break;
                case 2:
                    zhDate = "二";
                    break;
                case 3:
                    zhDate = "三";
                    break;
                case 4:
                    zhDate = "四";
                    break;
                case 5:
                    zhDate = "五";
                    break;
                case 6:
                    zhDate = "六";
                    break;
            }
            return buffer.append(zhDate).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return buffer.toString();
        }
    }


    public static List<String> getDayListOfMonth(int year, int month) {
        List<String> list = new ArrayList();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.set(Calendar.YEAR, year);
        aCalendar.set(Calendar.MONTH, month - 1);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        for (int i = 1; i <= day; i++) {
            String aDate = String.valueOf(year)+"-"+frontCompWithZore(month,2)+"-"+frontCompWithZore(i,2);

            Log.d("dayList...", aDate);
            list.add(aDate);
        }
        return list;
    }
    /*
    　　* 0 指前面补充零

    　　* formatLength 字符总长度为 formatLength

    　　* d 代表为正数。

    　　*/
    private static String frontCompWithZore(int sourceDate,int formatLength){
        String newString = String.format("%0"+formatLength+"d", sourceDate);
        return newString;
    }

    public static List<List<String>> getWeekListOfMonth(List<String> days){
        List<List<String>> weeks = new ArrayList<>();
        List<String> week = new ArrayList<>();
        for (int i = 0; i < days.size(); i++) {
            week.add(days.get(i));
            if (getDayOfWeek(days.get(i)).equals("星期日")) {
                weeks.add(week);
                week = new ArrayList<>();
            }
            if (i == days.size() - 1) {
                if (!getDayOfWeek(days.get(i)).equals("星期日")) {
                    weeks.add(week);
                }
            }
        }

        for (int i = 0; i < weeks.size(); i++) {
            Log.d("weekList...", "第"+(i+1)+"周");
            for (int j = 0; j < weeks.get(i).size(); j++) {
                Log.d("weekList...", weeks.get(i).get(j));
            }
        }
        return weeks;
    }

    public static RecordBmob castToBmobFromRecord(Record record){
        RecordBmob bmob = new RecordBmob();
        bmob.setDateYmd(record.getDateYmd());
        bmob.setDistance(record.getDistance());
        bmob.setDriveMoney(record.getDriveMoney());
        bmob.setExAndReType(record.getExAndReType());
        bmob.setExpensesMoney(record.getExpensesMoney());
        bmob.setExpensesType(record.getExpensesType());
        bmob.setId(record.getId());
        bmob.setOtherExpenses(record.getOtherExpenses());
        bmob.setOtherTarget(record.getOtherTarget());
        bmob.setReceiptsMoney(record.getReceiptsMoney());
        bmob.setReceiptsType(record.getReceiptsType());
        bmob.setTarget(record.getTarget());
        bmob.setTotal(record.getTotal());
        bmob.setUnitPrice(record.getUnitPrice());
        bmob.setFuelConsumption(record.getFuelConsumption());
        return bmob;
    }

    public static Record castToRecordFromBmob(RecordBmob bmob){
        Record record = new Record();
        record.setDateYmd(bmob.getDateYmd());
        record.setDistance(bmob.getDistance());
        record.setDriveMoney(bmob.getDriveMoney());
        record.setExAndReType(bmob.getExAndReType());
        record.setExpensesMoney(bmob.getExpensesMoney());
        record.setExpensesType(bmob.getExpensesType());
        record.setId(bmob.getId());
        record.setOtherExpenses(bmob.getOtherExpenses());
        record.setOtherTarget(bmob.getOtherTarget());
        record.setReceiptsMoney(bmob.getReceiptsMoney());
        record.setReceiptsType(bmob.getReceiptsType());
        record.setTarget(bmob.getTarget());
        record.setTotal(bmob.getTotal());
        record.setUnitPrice(bmob.getUnitPrice());
        record.setFuelConsumption(bmob.getFuelConsumption());
        return record;
    }


}
