package com.inspur.hw;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InputClass {


    private static Logger logger = Logger.getLogger(InputClass.class);

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        int province_id;
        int vendor_id;
        String start_time;
        String table_name;
        String ne_name;
        //start_time=2020-02-26 province_id=127
        Map<String, String> argsMap = new HashMap<>();
        //增加输入参数设置，如果没有输入时间就是默认跑前一天的数据，如果没有输入省份就是跑全国的省份。
        if (args.length == 0){
            Date date = new Date();
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date = calendar.getTime();
            start_time = sdf.format(date);

            province_id = 0;
            argsMap.put("start_time",start_time);
            argsMap.put("province_id",String.valueOf(province_id));
        }else if(args.length == 2){
            try {
                for (int i = 0; i < args.length; i++) {
                    String str1 = args[i].split("=")[0];
                    String str2 = args[i].split("=")[1];
                    argsMap.put(str1, str2);
                }
            } catch (Exception e) {
                logger.info("参数异常退出1");
                return;
            }
        }else {
            logger.info("参数异常退出2");
            return;
        }


        logger.info("输入的参数为:" + argsMap);


        //最新物理点表没有数据 执行初次插入操作
        ObtainRru.init(argsMap);//

//        //最新物理点表没有数据 执行初次插入操作
//        UpdateRru updateRru = new UpdateRru();
//        if(!boolean){
//            updateRru.init(argsMap);
//        }
        logger.info("执行完毕");
    }

//    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        Map<String, String> argsMap = new HashMap<>();
//        argsMap.put("start_time","2020-03-05");
//        argsMap.put("province_id","130");
//        ObtainRru.init(argsMap);
//        logger.info("输入的参数为:" + argsMap);
//        logger.info("执行完毕");
//    }


}
