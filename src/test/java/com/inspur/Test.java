package com.inspur;

import com.inspur.model.PhysicalPointRru;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Test {
    public static void main(String[] args) throws ParseException {
        Logger logger = Logger.getLogger(Test.class);
        logger.info("test");


        String srcFileName = "GX_GX_MOBILE_OMC1_ZTE_CM_eNodeB5_1440_20200314020000_15841251292525333188_0_0.tar.gz";
        String srcFile = "E:\\Company\\后台相关\\中兴华为文件解压测试\\test\\GX_GX_MOBILE_OMC1_ZTE_CM_eNodeB5_1440_20200314020000_15841251292525333188_0_0.tar.gz";
        File oldFile = new File(srcFile);
        File zteDir = new File(srcFile.replace(".", "_"));
        zteDir.mkdir();
        File newFile = new File(srcFile.replace(".", "_") + File.separator + srcFileName);
        System.out.println("oldFile:" + oldFile);
        System.out.println("newFile:" + newFile);
        if(oldFile.renameTo(newFile)){
            System.out.println("移动成功");
        }else{
            System.out.println("移动失败");
        }
//        File newFile1 = new File(srcFile.replace(".", "_") + File.separator + oldFile.getName());
//        if(oldFile.renameTo(newFile1)){
//            System.out.println("1移动成功");
//        }else{
//            System.out.println("1移动失败");
//        }
        //srcFile = oldFile.getAbsolutePath();

//        String str = ".a";
//
//        try {
//            System.out.println(Integer.valueOf(str));
//        } catch (Exception e) {
//            try {
//                System.out.println(Integer.parseInt(str,16));
//            } catch (Exception ex) {
//                System.out.println(str + "错误");
//            }
//
//        }
//        try {
//            System.out.println("j");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            System.out.println(Integer.parseInt(str));
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        try {
//            System.out.println(Integer.parseInt(str,10));
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        str = ".a";
//        try {
//            System.out.println(Integer.parseInt(str,16));
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }

//        String str = "";
//        str = "null";
//        System.out.println(StringUtils.isBlank(str) || "null".equalsIgnoreCase(str) ? "为空" : "不为空" + str);
//        System.out.println(StringUtils.isBlank(str) ? "为空" : "不为空" + str);



//        //SubNetwork=630101,MEID=578134,TransportNetwork=1,IpLayerConfig=1
//
//        String refIpLayerConfig = "SubNetwork=630101,MEID=578134,TransportNetwork=1,IpLayerConfig=1";
//        int index = refIpLayerConfig.lastIndexOf("=");
//        String str = refIpLayerConfig.substring(index + 1,refIpLayerConfig.length());
//        System.out.println(str);
////        String updatesql = "insert into necur_nodeb_w select /*+remote_mapping(WONOP_208)*/* from necur_nodeb_w@WONOP_208 where province_id=";
////
////        System.out.println(updatesql);
////        String str = "lxx";
//////        str = str.substring(0,);
////        System.out.println(str.substring(0,3));

//        Map<String,User> map = new HashMap<>();
//        User user1 = new User();
//        user1.setName("1");
//        user1.setNum("11111");
//        User user2 = new User();
//        user2.setName("2");
//        user2.setNum("22222");
//        map.put("1",user1);
//        map.put("2",user2);
//        System.out.println(map.toString());
//        if(true) {
//            User user = map.get("1");
//            user.setNum("00000");
////            map.put("1",user);
//        }
//        System.out.println(map.toString());



//        System.out.println(args.length);
//
//
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date=new Date();
//        String time = "2020-03-01 16:08:11";
//        Calendar calendar = Calendar.getInstance();
////        System.out.println(calendar.toString());
//        calendar.setTime(date);
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        date = calendar.getTime();
//        System.out.println(sdf.format(date));


//        int i = 127;
//        System.out.println(String.valueOf(i));
//
//        String str = "123|";
//        System.out.println(str.length());
//        if(str.endsWith("|")){
//            System.out.println(str.substring(1,str.length()-1));
//        }
//
//        Map<String, String> maptest = new HashMap<>();
//        maptest.put("1","a");
//        maptest.put("2","b");
//        maptest.put("3","c");
//
//        Map<String, String> maptestb = new HashMap<>();
//        maptestb.putAll(maptest);
//
////        for(Map.Entry<String,String> map: maptest.entrySet()){
////            System.out.println("一级：" + map.getKey() + map.getValue());
////            for(Map.Entry<String,String> map1: maptest.entrySet()){
//////                System.out.println("二级：" +map1.getKey() + map1.getValue());
////                if(map1.getKey().equals("2")){
////                    System.out.println("二级：" +map1.getKey() + map1.getValue());
////                    maptest.remove(map1.getKey());
////                }
////            }
////        }
//
////        for(Map.Entry<String,String> map: maptest.entrySet()){
////            System.out.println("一级：" + map.getKey() + map.getValue());
////            for(Map.Entry<String,String> map1: maptestb.entrySet()){
//////                System.out.println("二级：" +map1.getKey() + map1.getValue());
////                if(map1.getKey().equals("2")){
////                    System.out.println("二级：" +map1.getKey() + map1.getValue());
////                    maptest.remove(map1.getKey());
////                }
////            }
////        }
//
//
//
//
//
////        Iterator<Map.Entry<String, String>> iterator = maptest.entrySet().iterator();
////        int num = 1;
////        while (iterator.hasNext()){
////
////            Map.Entry<String, String> map = iterator.next();
////            System.out.println("一级：" + map.getKey() + map.getValue());
////
////            Iterator<Map.Entry<String, String>> iterator1 = maptestb.entrySet().iterator();
////            while (iterator1.hasNext()){
////                Map.Entry<String, String> map1 = iterator1.next();
////                System.out.println("二级：" +map1.getKey() + map1.getValue());
////                if(map1.getKey().equals(String.valueOf(num))){
//////                    System.out.println("二级：" +map1.getKey() + map1.getValue());
////                        iterator1.remove();
//////                    System.out.println(maptestb);
////                    maptest.clear();
////                    maptest.putAll(maptestb);
//////                    System.out.println(maptest);
////                    iterator = maptest.entrySet().iterator();
////                    }
////                }
////            System.out.println("----↓");
////            System.out.println(maptest);
////            System.out.println(maptestb);
////            System.out.println("----↑");
////
////            num++;
////            System.out.println("num" + num);
////            }
//
//        Iterator<Map.Entry<String, String>> iterator = maptest.entrySet().iterator();
//        Iterator<Map.Entry<String, String>> iterator1 = maptest.entrySet().iterator();
//        int num = 1;
//        while (iterator.hasNext()){
//
//            Map.Entry<String, String> map = iterator.next();
//            System.out.println("一级：" + map.getKey() + map.getValue());
//
//            iterator1 = maptest.entrySet().iterator();
//            while (iterator1.hasNext()){
//                Map.Entry<String, String> map1 = iterator1.next();
//                System.out.println("二级：" +map1.getKey() + map1.getValue());
//                if(map1.getKey().equals(String.valueOf(num))){
////                    System.out.println("二级：" +map1.getKey() + map1.getValue());
//                    iterator1.remove();
////                    System.out.println(maptestb);
////                    System.out.println(maptest);
//                    iterator = maptest.entrySet().iterator();
//                }
//            }
//            System.out.println("----↓");
//            System.out.println(maptest);
//            System.out.println("----↑");
//
//            num++;
//            System.out.println("num" + num);
//        }

        }

}
