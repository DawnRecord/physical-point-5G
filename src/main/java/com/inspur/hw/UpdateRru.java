package com.inspur.hw;

import org.apache.log4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateRru extends Baseway{

    private static Logger logger = Logger.getLogger(UpdateRru.class);

    public void process() throws SQLException {
        connection = DriverManager.getConnection("jdbc:oracle:thin:@10.162.65.192:10110:wonop1", "lternop", "P0ibojja");
        if(connection == null){
            logger.info("数据库连接失败");
        }else{
            logger.info("数据库连接成功");
        }
        /**
         * 方法一
         */
        boolean flag = acquireRruData();//RRU数据获取，RRU地市获取
        if(!flag){
            return;
        }
        acquirePhyPointData();
        ProcessUpdateRru();
        outputPhyPoint();//物理点输出到para_physicalpoint_ng表中
        /**
         * 可以增加一个方法，比较今天和昨天的RRU数据，将变动的RRU进行处理，不再处理未变动的RRU,这种情况是向物理点的type列表向后拼接
         */


        connection.close();


    }


    public static void main(String[] args) throws Exception {

        //start_time=2020-02-26 province_id=127
        if(args.length != 2){
            return;
        }
        Map<String,String> argsMap = new HashMap<>();
        try {
            for (int i = 0; i < args.length; i++) {
                String str1 = args[i].split("=")[0];
                String str2 = args[i].split("=")[1];
                argsMap.put(str1, str2);
            }
        }catch (Exception e){
            logger.info("参数异常");
            return;
        }
        logger.info("输入的参数为:" +argsMap);

        Class.forName("oracle.jdbc.OracleDriver");

        Map<String, String> neTypeMap = new HashMap<>();
        neTypeMap.put("RRU", "PARA_RRU_NG_ALL");
        neTypeMap.put("CU", "PARA_CU_NG_ALL");
        neTypeMap.put("DU", "PARA_DU_NG_ALL");
        Map<Integer, String> vendorMap = new HashMap<>();
        if ("0".equalsIgnoreCase(argsMap.get("province_id"))) {
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
                    for (Map.Entry<String, String> typeMap : neTypeMap.entrySet()) {
                        logger.info("-------处理" + i + "省份，" + vendor_id + "厂家" + typeMap.getKey() + "数据中---------");

                        UpdateRru updateRru = new UpdateRru();
                        updateRru.setProvince_id(i);
                        updateRru.setStart_time(argsMap.get("start_time"));
                        updateRru.setVendor_id(vendor_id);
                        updateRru.setTable_name(typeMap.getValue());
                        updateRru.setNe_name(typeMap.getKey());

                        updateRru.process();
                        logger.info("---------" + i + "省份，" + vendor_id + "厂家" + typeMap.getKey() + "处理完毕-------");
                    }
                }
            }
        } else {
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
                for (Map.Entry<String, String> typeMap : neTypeMap.entrySet()) {
                    logger.info("-------处理" + argsMap.get("province_id") + "省份，" + vendor_id + "厂家" + typeMap.getKey() + "数据中---------");

                    UpdateRru updateRru = new UpdateRru();
                    updateRru.setProvince_id(Integer.parseInt(argsMap.get("province_id")));
                    updateRru.setStart_time(argsMap.get("start_time"));
                    updateRru.setVendor_id(vendor_id);
                    updateRru.setTable_name(typeMap.getValue());
                    updateRru.setNe_name(typeMap.getKey());

                    updateRru.process();
                    logger.info("---------" + argsMap.get("province_id") + "省份，" + vendor_id + "厂家" + typeMap.getKey() + "处理完毕-------");
                }

            }
        }
    }
}
