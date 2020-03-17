package com.inspur.hw;

import org.apache.log4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ObtainRru extends Baseway{
    private static Logger logger = Logger.getLogger(ObtainRru.class);

    public void process() throws SQLException {
        connection = DriverManager.getConnection("jdbc:oracle:thin:@10.162.65.192:10110:wonop1", "lternop", "P0ibojja");
        if (connection == null) {
            logger.info("数据库连接失败");
        } else {
            logger.info("数据库连接成功");
        }

        boolean flag = acquireRruData();//RRU数据获取，RRU地市获取
        if(!flag){
            return;
        }

        if(existPhyPoint()){
            logger.info("存在物理点，进行更新操作！");
            acquirePhyPointData();
            ProcessUpdateRru();
        }else {
            logger.info("不存在物理点，进行第一次创建操作！");
            ProcessRru();//物理点判断处理
        }

        outputPhyPoint();//物理点输出到para_physicalpoint_ng表中

        connection.close();

    }


    public static boolean init(Map<String, String> argsMap) throws ClassNotFoundException, SQLException {

//        //java *.jar start_time=2020-02-26 province_id=127
//        if(args.length != 2){
//            return;
//        }
//        Map<String,String> argsMap = new HashMap<>();
//        try {
//            for (int i = 0; i < args.length; i++) {
//                String str1 = args[i].split("=")[0];
//                String str2 = args[i].split("=")[1];
//                argsMap.put(str1, str2);
//            }
//        }catch (Exception e){
//            logger.info("参数异常");
//            return;
//        }
        logger.info("输入的参数为:" +argsMap);

        Class.forName("oracle.jdbc.OracleDriver");

        Map<String,String> neTypeMap = new HashMap<>();
        neTypeMap.put("RRU","PARA_RRU_NG_ALL");
        neTypeMap.put("CU","PARA_CU_NG_ALL");
        neTypeMap.put("DU","PARA_DU_NG_ALL");
        Map<Integer,String> vendorMap = new HashMap<>();
        if("0".equalsIgnoreCase(argsMap.get("province_id"))){
            for (int i = 101; i < 131; i++) {
                for (int i1 = 0; i1 < 4; i1++) {
                    int vendor_id = -1;
                    if (i1 == 0) {
                        vendor_id = 1;
                    } else if (i1 == 1) {
                        vendor_id = 2;
                    } else if (i1 == 2) {
                        vendor_id = 5;
                    } else if (i1 == 3) {
                        vendor_id = 7;
                    }
                    for(Map.Entry<String, String> typeMap : neTypeMap.entrySet()) {
                        try {
                            logger.info("-----------------------------------------------------------------------------------");
                            logger.info("-------处理省份：" + i + "，厂家：" + vendor_id + "，网元类型：" + typeMap.getKey()+ "，处理数据的时间为：" +argsMap.get("start_time")+ " 数据中-----");

                            ObtainRru obtainRru = new ObtainRru();
                            obtainRru.setProvince_id(i);
                            obtainRru.setStart_time(argsMap.get("start_time"));
                            obtainRru.setVendor_id(vendor_id);
                            obtainRru.setTable_name(typeMap.getValue());
                            obtainRru.setNe_name(typeMap.getKey());

                            obtainRru.process();
                            logger.info("--------省份：" + i + "，厂家：" + vendor_id + "，网元类型："+typeMap.getKey()+ "，处理数据的时间为：" +argsMap.get("start_time")+ " 处理完毕-------");
                            logger.info("-----------------------------------------------------------------------------------");
                        } catch (SQLException e) {
                            logger.info("--------省份：" + i + "，厂家：" + vendor_id + "，网元类型："+typeMap.getKey()+ "，处理数据的时间为：" +argsMap.get("start_time")+ " 出现异常-------");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else {
            for (int i1 = 0; i1 < 4; i1++) {
                int vendor_id = -1;
                if (i1 == 0) {
                    vendor_id = 1;
                } else if (i1 == 1) {
                    vendor_id = 2;
                } else if (i1 == 2) {
                    vendor_id = 5;
                } else if (i1 == 3) {
                    vendor_id = 7;
                }
                //测试代码以下打包去除
                vendor_id = 2;
                neTypeMap.clear();
                neTypeMap.put("RRU","PARA_RRU_NG_ALL");
                //测试代码以上打包去除
                for(Map.Entry<String, String> typeMap : neTypeMap.entrySet()) {
                    logger.info("-----------------------------------------------------------------------------------");
                    logger.info("-------处理省份：" + argsMap.get("province_id") + "，厂家：" + vendor_id + "，网元类型：" + typeMap.getKey()+ "，处理数据的时间为：" +argsMap.get("start_time")+" 数据中-----");

                    ObtainRru obtainRru = new ObtainRru();
                    obtainRru.setProvince_id(Integer.parseInt(argsMap.get("province_id")));
                    obtainRru.setStart_time(argsMap.get("start_time"));
                    obtainRru.setVendor_id(vendor_id);
                    obtainRru.setTable_name(typeMap.getValue());
                    obtainRru.setNe_name(typeMap.getKey());

                    obtainRru.process();
                    logger.info("--------省份：" + argsMap.get("province_id") + "，厂家：" + vendor_id + "，网元类型："+typeMap.getKey()+ "，处理数据的时间为：" +argsMap.get("start_time")+" 处理完毕-------");
                    logger.info("-----------------------------------------------------------------------------------");
                }

                //测试代码以下打包去除
                    return true;
                //测试代码以上打包去除

            }
        }
        return true;
    }



}
