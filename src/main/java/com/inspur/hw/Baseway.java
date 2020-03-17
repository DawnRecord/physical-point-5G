package com.inspur.hw;

import com.inspur.model.PhysicalPoint;
import com.inspur.model.PhysicalPointRru;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.inspur.algo.CaculateDistance.isPhysicalPoint;

public class Baseway {

    private static Logger logger = Logger.getLogger(Baseway.class);

    protected Connection connection = null;
    private Map<String, PhysicalPointRru> mapInfoRru= new HashMap<>();//rru的所有信息
    private Map<String, PhysicalPointRru> tempmapInfoRru= new HashMap<>();//进行物理点遍历
    private Map<PhysicalPointRru, List<PhysicalPointRru>> physicalPointRruListMap = new HashMap<>();//rru物理点
    private ArrayList<String> cityList = new ArrayList<>();//有几个地级市
    private ArrayList<PhysicalPointRru> noLoLaList = new ArrayList<>();//没有经纬度信息的rru列表
    private ArrayList<PhysicalPoint> PhysicalPointList = new ArrayList<>();//物理点列表
    private ArrayList<PhysicalPoint> PhysicalPointLastList = new ArrayList<>();//昨天的物理点列表
    private Map<String,PhysicalPoint> PhysicalPointMapLast = new HashMap<>();//昨天的物理点Map
    private ArrayList<String> physicalPointSeqList = new ArrayList<>();
    private Map<String,Integer> physicalPointSeqMap = new HashMap<>();//存储物理点顺序的表
    private Map<String, Integer> seqLastMap = new HashMap<>();//存储上次物理点顺序的表
    private int province_id;
    private int vendor_id;
    private String start_time;
    private String start_time_last;
    private String table_name;
    private String ne_name;

    public String getNe_name() {
        return ne_name;
    }

    public void setNe_name(String ne_name) {
        this.ne_name = ne_name;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStart_time_last() {
        return start_time_last;
    }

    public void setStart_time_last(String start_time_last) {
        this.start_time_last = start_time_last;
    }

    boolean existPhyPoint() throws SQLException{
        String phyPointSqlCount = "SELECT count(*)" +
                "  FROM PARA_PHYSICALPOINT_NG_NEWEST" +
                " WHERE PROVINCE_ID = " +province_id+
                "   AND VENDOR_ID = " + vendor_id +
                "   AND TYPE = '" + ne_name + "'";
        logger.info("判断该省份，该厂家，该网元是否存在物理点数据，SQL为：" + phyPointSqlCount);
        PreparedStatement ps = connection.prepareStatement(phyPointSqlCount);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()){
            count = rs.getInt(1);
        }
        ps.close();
        rs.close();
        if(count == 0){
            return false;//不存在物理点，进行更新操作
        }

        return true;
    }



    boolean acquireRruData() throws SQLException {
        //NE 数据获取与存储
        String neSql = "SELECT A.OID, A.LONGITUDE, A.LATITUDE, A.GNBID, B.CITYCODE" +
                "  FROM " + table_name + " A," +
                "       (SELECT GNBID, CITYCODE" +
                "          FROM PARA_GNB_NG_ALL" +
                "         WHERE START_TIME = "+"TO_DATE('" +start_time +"', 'yyyy-mm-dd')"+
                "           AND PROVINCE_ID = " +province_id+
                "           AND VENDOR_ID = "+ vendor_id +") B" +
                " WHERE A.PROVINCE_ID = "+ province_id +
                "   AND A.VENDOR_ID = "+ vendor_id +
                "   AND A.START_TIME = "+ "TO_DATE('" +start_time +"', 'yyyy-mm-dd')" +
                "   AND A.GNBID = B.GNBID";
        logger.info("执行查询" + ne_name + "信息的SQL为：" + neSql);
        PreparedStatement ps = connection.prepareStatement(neSql);
        ResultSet rs = ps.executeQuery();
        logger.info("查询完毕，正在对数据进行处理：");
        while (rs.next()){
            PhysicalPointRru physicalPointRru = new PhysicalPointRru();

            physicalPointRru.setOid(rs.getString("OID"));
            //将非数字转化成-1
            String citycode = rs.getString("CITYCODE");
            try{
                int citycodeInt = Integer.parseInt(citycode);
                physicalPointRru.setCityCode(citycode);
            }catch (Exception e){
                physicalPointRru.setCityCode("-1");
            }
            physicalPointRru.setCityCode(rs.getString("CITYCODE"));
            physicalPointRru.setGnbId(rs.getString("GNBID"));
            physicalPointRru.setLongitude(rs.getString("LONGITUDE"));
            physicalPointRru.setLatitude(rs.getString("LATITUDE"));
            physicalPointRru.setVendorId("1");

            mapInfoRru.put(physicalPointRru.getOid(),physicalPointRru);

        }
        rs.close();
        ps.close();
        tempmapInfoRru.putAll(mapInfoRru);

        logger.info("获取的"+ne_name+"数据条数有：" + tempmapInfoRru.size());
        if(tempmapInfoRru.size() == 0){
            logger.info("该省份：" + province_id + " 该厂家：" + vendor_id +" 该类型" + ne_name +"不包含数据。");
            return false;
        }

        //地级市获取
        String cityCountSql = "SELECT CITYCODE,COUNT(*) FROM (" + neSql + ") GROUP BY citycode";
        logger.info("执行查询地级市个数的SQL为：" + cityCountSql);
        PreparedStatement ps1 = connection.prepareStatement(cityCountSql);
        ResultSet rs1 = ps1.executeQuery();

        int flag = 0;
        while (rs1.next()){
            String citycode = rs1.getString("CITYCODE");
            try{
                int citycodeInt = Integer.parseInt(citycode);
                cityList.add(rs1.getString("CITYCODE"));
            }catch (Exception e){
                logger.info("地市为：" + citycode + " 赋值为-1");
                flag = -1;
            }
//            if(rs1.getString("CITYCODE") == null){
//                logger.info("citycode为空，赋值为-1");
//                cityList.add("-1");
//            }else{
//                cityList.add(rs1.getString("CITYCODE"));
//            }
        }
        ps1.close();
        rs1.close();
        //一个地市一个地市循环查询
        if(flag == -1){
            cityList.add("-1");
        }
        Collections.sort(cityList);//排序
        logger.info("地市个数为：" + cityList.size());
        logger.info("地市列表为：" + cityList);

        return true;
    }

    /**
     * 物理点更新处理
     * @throws SQLException
     */
    void acquirePhyPointData() throws SQLException {
        String phyPointSql = "SELECT *" +
                "  FROM PARA_PHYSICALPOINT_NG_NEWEST" +
                " WHERE PROVINCE_ID = " +province_id+
                "   AND VENDOR_ID = " + vendor_id +
                "   AND TYPE = '" + ne_name + "'";
        logger.info("获取最新的物理点数据，SQL为：" + phyPointSql);
        PreparedStatement ps = connection.prepareStatement(phyPointSql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            PhysicalPoint physicalPoint = new PhysicalPoint();

            physicalPoint.setOid(rs.getString(1));
            physicalPoint.setProvince_id(rs.getString(2));
            physicalPoint.setCitycode(rs.getString(3));
            physicalPoint.setType(rs.getString(4));
            physicalPoint.setType_num(rs.getString(5));
            physicalPoint.setType_oid_list(rs.getString(6));
            physicalPoint.setType_oid_base(rs.getString(7));
            physicalPoint.setVendor_id(rs.getString(8));
            physicalPoint.setStart_time(rs.getString(9));
            physicalPoint.setLongitude(rs.getString(10));
            physicalPoint.setLatitude(rs.getString(11));
            physicalPoint.setScan_seq(rs.getInt(13));

            PhysicalPointLastList.add(physicalPoint);
            PhysicalPointMapLast.put(physicalPoint.getOid(), physicalPoint);

        }

        String phyPoint = "SELECT citycode,COUNT(*) FROM PARA_PHYSICALPOINT_NG_NEWEST" +
                " WHERE PROVINCE_ID = " +province_id+
                "   AND VENDOR_ID = " + vendor_id +
                "   AND TYPE = '" + ne_name + "'" +
                "   GROUP BY citycode";
        logger.info("获取最新每个地市包含的物理点个数，SQL为：" + phyPoint);
        PreparedStatement ps1 = connection.prepareStatement(phyPoint);
        ResultSet rs1 = ps1.executeQuery();
        while (rs1.next()){
            if(rs1.getString(1).equals("0")){
                seqLastMap.put("00000", rs1.getInt(2));
            }else {
                seqLastMap.put(rs1.getString(1), rs1.getInt(2));
            }
        }
        for(Map.Entry<String,Integer> map : seqLastMap.entrySet()){
            logger.info(map.getKey() +"地市最新的物理点个数为：" + map.getValue());
        }
        rs.close();
        rs1.close();
        ps.close();
        ps1.close();

    }


    void ProcessRru() {
        logger.info("第一次按照地市列表进行物理点进行判定处理：");
        for (int i = 0; i < cityList.size(); i++) {
            String cityCode = cityList.get(i);
            logger.info("第" + (i+1) + "次开始执行，正在处理" + cityCode + "地市");

            int num = jdugephysicalPointFirst(cityCode);//物理点判断
            logger.info(cityCode + "包含的物理点个数为：" + num);

        }

        RruToPhysicalPoint(1);

    }

    void ProcessUpdateRru(){
        logger.info("按照地市列表进行物理点进行更新处理：");
        for (int i = 0; i < cityList.size(); i++) {
            String cityCode = cityList.get(i);
            logger.info("第" + (i+1) + "次开始执行，正在处理" + cityCode + "地市");
            int num = jdugephysicalPoint(cityCode);//物理点判断
//            logger.info(cityCode + "地市原物理点更新，处理完毕。");
        }
        logger.info("本日新增的不属于任何物理点的"+ne_name+"个数为：" + tempmapInfoRru.size());
        if(tempmapInfoRru.size() > 0){
            logger.info("对新增的网元进行增加物理点处理：" );
            for (int i = 0; i < cityList.size(); i++) {
                String cityCode = cityList.get(i);
                int num = jdugephysicalPointFirst(cityCode);//物理点判断
                if(num > 0){
                    logger.info("地市" + cityCode + "新增的物理点个数为：" + num);
                }

            }
        }

        RruToPhysicalPoint(2);
    }

    private void RruToPhysicalPoint(int way) {
        for (Map.Entry<PhysicalPointRru, List<PhysicalPointRru>> map : physicalPointRruListMap.entrySet()){
            PhysicalPointRru phyPointRru = map.getKey();
            List<PhysicalPointRru> tempPhyPointRruList = map.getValue();
            StringBuffer typeOidList = new StringBuffer();
            String cityCode = "";
            String noTypeOid = "";
            String oid = "";
            if(phyPointRru.getCityCode() == null){
                cityCode = "-1";
            }else{
                cityCode = phyPointRru.getCityCode();
            }
            noTypeOid = cityCode +"_" + phyPointRru.getLongitude() + "_" + phyPointRru.getLatitude();
            oid = ne_name + "_" + noTypeOid;
            for(PhysicalPointRru physicalPointRru : tempPhyPointRruList){//该物理点所有的网元
                typeOidList.append(physicalPointRru.getOid() + "|");
            }
            String typeOidListStr = typeOidList.toString();

            //oid province_id citycode type type_num type_oid vendor_id start_time longitude latitude time_stamp scan_seq;
            PhysicalPoint physicalPoint = new PhysicalPoint();
            physicalPoint.setOid(oid);
            physicalPoint.setProvince_id(String.valueOf(province_id));
            physicalPoint.setCitycode(phyPointRru.getCityCode());
            physicalPoint.setType(ne_name);
            physicalPoint.setType_num(String.valueOf(tempPhyPointRruList.size()));
            if(typeOidListStr.endsWith("|")){
                typeOidListStr = typeOidListStr.substring(0,typeOidListStr.length()-1);
            }
            physicalPoint.setType_oid_list(typeOidListStr);
            physicalPoint.setVendor_id(String.valueOf(vendor_id));
            physicalPoint.setStart_time(start_time);
            physicalPoint.setLongitude(phyPointRru.getLongitude());
            physicalPoint.setLatitude(phyPointRru.getLatitude());
            physicalPoint.setInsert_time(new Date());
            if(way == 1 && physicalPointSeqMap.get(noTypeOid) != null) {//第一次物理点顺序
                physicalPoint.setScan_seq(physicalPointSeqMap.get(noTypeOid));
            }else if(way == 2 && physicalPointSeqMap.get(noTypeOid) != null ){//第二次物理点顺序向后排
                try {
                    physicalPoint.setScan_seq(physicalPointSeqMap.get(noTypeOid) + seqLastMap.get(phyPointRru.getCityCode()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            physicalPoint.setType_oid_base(phyPointRru.getOid());

            PhysicalPointList.add(physicalPoint);
        }
    }


    int jdugephysicalPoint(String cityCode){
        int count = 0;//该地市新增的网元个数


        for(PhysicalPoint physicalPoint : PhysicalPointLastList){
            if(!cityCode.equalsIgnoreCase(physicalPoint.getCitycode())){
                continue;
            }

            StringBuilder oidListStr = new StringBuilder();

            Iterator<Map.Entry<String,PhysicalPointRru>> iteratorMark = tempmapInfoRru.entrySet().iterator();
            int listCount = 0;
            while (iteratorMark.hasNext()){
                Map.Entry<String,PhysicalPointRru> map = iteratorMark.next();
                String infoRruOid = map.getKey();
                PhysicalPointRru infoRru = map.getValue();
                if ("0.0".equals(infoRru.getLatitude()) && "0.0".equals(infoRru.getLongitude()) ||
                        infoRru.getLatitude()==null || infoRru.getLongitude()==null) {
                    iteratorMark.remove();//去除该RRU
                    continue;
                }
                if ((infoRru.getCityCode() == null && cityCode == "-1") ||
                        infoRru.getCityCode() != null && infoRru.getCityCode().equalsIgnoreCase(cityCode)){
                    String result = isPhysicalPoint(physicalPoint, infoRru);

                    if ("markNotString".equalsIgnoreCase(result)) {
//                        logger.info(physicalPoint.getOid() + "物理点经纬度信息异常，经度：" + physicalPoint.getLongitude() + " 纬度：" + physicalPoint.getLatitude());
                    } else if ("tempNotString".equalsIgnoreCase(result)) {
//                        logger.info(infoRruOid + "经纬度信息异常，经度：" + infoRru.getLongitude() + " 纬度：" + infoRru.getLatitude());
                    } else if ("not".equalsIgnoreCase(result)) {
//                            logger.info("周围点不属于该物理点");
                    } else if ("yes".equalsIgnoreCase(result)) {
                        oidListStr.append(infoRru.getOid()).append("|");
                        listCount++;
                        iteratorMark.remove();//已获取，去除该RRU
                    }
                }
            }

            String type_oid_list = oidListStr.toString();
            if(type_oid_list.length() > 0){//该地是物理点包含网元类型时才进行处理
                type_oid_list = type_oid_list.substring(0, type_oid_list.length()-1);
            }
            physicalPoint.setType_num(String.valueOf(listCount));
            physicalPoint.setType_oid_list(type_oid_list);
            physicalPoint.setStart_time(start_time);
            PhysicalPointList.add(physicalPoint);

        }

        count = tempmapInfoRru.size();



        return count;
    }

    int jdugephysicalPointFirst(String cityCode){
        int count = 0;//该地市包含的物理点个数
        Iterator<Map.Entry<String,PhysicalPointRru>> iteratorMark = tempmapInfoRru.entrySet().iterator();
        Iterator<Map.Entry<String,PhysicalPointRru>> iteratorTemp = tempmapInfoRru.entrySet().iterator();

        while (iteratorMark.hasNext()){
            Map.Entry<String,PhysicalPointRru> map = iteratorMark.next();
            String infoRruOid = map.getKey();
            PhysicalPointRru infoRruMark = map.getValue();
            //空地市 或者 指定地市
            if ((infoRruMark.getCityCode() == null && cityCode == "-1") ||
                    infoRruMark.getCityCode() != null && infoRruMark.getCityCode().equalsIgnoreCase(cityCode)) {//只处理指定的地市
                ArrayList<PhysicalPointRru> physicalPointRrus = new ArrayList<>();//一个物理点包含的rru列表
                if ("0.0".equals(infoRruMark.getLatitude()) && "0.0".equals(infoRruMark.getLongitude()) ||
                        infoRruMark.getLatitude() == null || infoRruMark.getLongitude() == null) {
                    continue;
                }

                iteratorTemp = tempmapInfoRru.entrySet().iterator();
                while (iteratorTemp.hasNext()){
                    Map.Entry<String,PhysicalPointRru> tempmap = iteratorTemp.next();
                    String tempInfoRruOid = tempmap.getKey();
                    PhysicalPointRru tempInfoRru = tempmap.getValue();
                    //空地市 或者 指定地市
                    if (tempInfoRru.getCityCode() == null && cityCode == "-1" ||
                            tempInfoRru.getCityCode() != null && tempInfoRru.getCityCode().equalsIgnoreCase(cityCode)) {
//                    if (tempInfoRru.getCityCode() == null ||
//                            tempInfoRru.getCityCode() != null && tempInfoRru.getCityCode().equalsIgnoreCase(cityCode)) {//指定地市 或者 为空的数据
                        if ("0.0".equals(tempInfoRru.getLatitude()) && "0.0".equals(tempInfoRru.getLongitude()) ||
                                tempInfoRru.getLatitude() == null || tempInfoRru.getLongitude() == null) {
//                            logger.info(tempInfoRruOid + "经纬度信息为 0.0");
//                            tempmapInfoRru.remove(tempInfoRruOid);//去除纬度为0的rru
                            iteratorTemp.remove();//去除纬度为0或空的rru
                            iteratorMark = tempmapInfoRru.entrySet().iterator();
                            noLoLaList.add(tempInfoRru);//存储经纬度为0的rru
                            continue;
                        }

                        String result = isPhysicalPoint(infoRruMark, tempInfoRru);
                        if ("markNotString".equalsIgnoreCase(result)) {
//                            logger.info(infoRruOid + "经纬度信息异常，经度：" + infoRruMark.getLongitude() + " 纬度：" + infoRruMark.getLatitude());
                        } else if ("tempNotString".equalsIgnoreCase(result)) {
//                            logger.info(tempInfoRruOid + "经纬度信息异常，经度：" + infoRruMark.getLongitude() + " 纬度：" + infoRruMark.getLatitude());
                        } else if ("not".equalsIgnoreCase(result)) {
//                            logger.info("周围点不属于该物理点");
                        } else if ("yes".equalsIgnoreCase(result)) {
//                            logger.info("周围点" + tempInfoRruOid + "属于该物理点" + infoRruOid);
                            physicalPointRrus.add(tempInfoRru);//当前物理点所包含的所有网元（包含自身）
//                            tempmapInfoRru.remove(tempInfoRruOid);//去除已包含的网元（包含自身）
                            iteratorTemp.remove();//去除rru
                            iteratorMark = tempmapInfoRru.entrySet().iterator();
                        }
                    }else {
                    }

                }
                count++;
                physicalPointRruListMap.put(infoRruMark, physicalPointRrus);
                physicalPointSeqMap.put(cityCode + "_" + infoRruMark.getLongitude() + "_" +infoRruMark.getLatitude(), count);
//                logger.info(cityCode + "该地市，第 " + count +"个物理点找到");
//                logger.info("RRU: "+infoRruMark.getOid() + "物理点为"+physicalPointRrus);

            } else{
            }
        }



//        for(Map.Entry<String,PhysicalPointRru> map : tempmapInfoRru.entrySet()) {
//            String infoRruOid = map.getKey();
//            PhysicalPointRru infoRruMark = map.getValue();
//            logger.info(cityCode);
//            logger.info(infoRruMark.getCityCode());
//            if (infoRruMark.getCityCode() == null && cityCode == null || infoRruMark.getCityCode() != null && infoRruMark.getCityCode().equalsIgnoreCase(cityCode)) {//只处理指定的地市
//                ArrayList<PhysicalPointRru> physicalPointRrus = new ArrayList<>();//一个物理点包含的rru列表
//                if ("0.0".equals(infoRruMark.getLatitude()) && "0.0".equals(infoRruMark.getLongitude())) {
//                    continue;
//                }
//                for (Map.Entry<String, PhysicalPointRru> tempmap : tempmapInfoRru.entrySet()) {
//                    String tempInfoRruOid = tempmap.getKey();
//                    PhysicalPointRru tempInfoRru = tempmap.getValue();
//                    if (tempInfoRru.getCityCode() == null && cityCode == null || tempInfoRru.getCityCode() != null && tempInfoRru.getCityCode().equalsIgnoreCase(cityCode)) {
//                        if ("0.0".equals(infoRruMark.getLatitude()) && "0.0".equals(infoRruMark.getLongitude())) {
//                            logger.info(tempInfoRruOid + "经纬度信息为 0.0");
//                            tempmapInfoRru.remove(tempInfoRruOid);//去除纬度为0的rru
//                            noLoLaList.add(tempInfoRru);//存储经纬度为0的rru
//                            continue;
//                        }
//
//                        String result = CaculateDistance.isPhysicalPoint(infoRruMark, tempInfoRru);
//                        if ("markNotString".equalsIgnoreCase(result)) {
//                            logger.info(infoRruOid + "经纬度信息异常，经度：" + infoRruMark.getLongitude() + " 纬度：" + infoRruMark.getLatitude());
//                        } else if ("tempNotString".equalsIgnoreCase(result)) {
//                            logger.info(tempInfoRruOid + "经纬度信息异常，经度：" + infoRruMark.getLongitude() + " 纬度：" + infoRruMark.getLatitude());
//                        } else if ("not".equalsIgnoreCase(result)) {
//                            logger.info("周围点不属于该物理点");
//                        } else if ("yes".equalsIgnoreCase(result)) {
//                            logger.info("周围点" + tempInfoRruOid + "属于该物理点" + infoRruOid);
//                            physicalPointRrus.add(tempInfoRru);//当前物理点所包含的所有网元（包含自身）
//                            tempmapInfoRru.remove(tempInfoRruOid);//去除已包含的网元（包含自身）
//                        }
//                    }else {
//                    }
//                }
//                physicalPointRruListMap.put(infoRruMark, physicalPointRrus);
//                count++;
//            } else{
//            }
//        }
        return count;
    }

    void outputPhyPoint() throws SQLException {

        //输入前先删除
        String deleteSql = "DELETE FROM PARA_PHYSICALPOINT_NG" +
                "  WHERE PROVINCE_ID = "+ province_id +
                "    AND VENDOR_ID = "+ vendor_id +
                "    AND START_TIME = TO_DATE('" + start_time + "', 'yyyy-mm-dd')" +
                "    AND TYPE = '" + ne_name + "'";
        logger.info("插入前先删除，SQL为：" + deleteSql);
        PreparedStatement ps1 = connection.prepareStatement(deleteSql);
        int count = ps1.executeUpdate();
        logger.info("删除的数据条数为：" + count);
        ps1.close();

        //PhysicalPointList 物理点输出测试

        count = 0;
        String insertSql = "INSERT INTO para_physicalpoint_ng(oid,province_id,citycode,type,type_num,type_oid_list,type_oid_base,vendor_id,start_time,longitude,latitude,scan_seq)\n" +
                "VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'yyyy-mm-dd'),?,?,?)";
        logger.info("插入数据的SQL为：" + insertSql);
        logger.info("地市为空，为字符串的情况，统一为-1地市");
        PreparedStatement ps = connection.prepareStatement(insertSql);
        for(PhysicalPoint physicalPoint : PhysicalPointList){
            ps.setString(1,physicalPoint.getOid());
            ps.setString(2,physicalPoint.getProvince_id());
            if(physicalPoint.getCitycode() == null){
                ps.setInt(3,-1);
            }else{
                int cityCodeTemp = -1;
                try{
                    cityCodeTemp = Integer.parseInt(physicalPoint.getCitycode());
                    ps.setInt(3,cityCodeTemp);
                }catch (NumberFormatException e){//地市为字符串的情况
                    ps.setInt(3,cityCodeTemp);
                }
            }
            ps.setString(4,physicalPoint.getType());
            ps.setString(5,physicalPoint.getType_num());
            ps.setString(6,physicalPoint.getType_oid_list());
            ps.setString(7,physicalPoint.getType_oid_base());
            ps.setInt(8,Integer.parseInt(physicalPoint.getVendor_id()));
            ps.setString(9,physicalPoint.getStart_time());
            ps.setString(10,physicalPoint.getLongitude());
            ps.setString(11,physicalPoint.getLatitude());
            ps.setString(12,String.valueOf(physicalPoint.getScan_seq()));
            try {
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.info("异常物理点的信息为：" + physicalPoint.toString());
            }
            count++;
        }
        ps.close();
        logger.info("数据插入完毕，插入" + count + "条。");

        connection.commit();

        //插入一张完整表中，内为最新数据
        //输入前先删除
        logger.info("维护最新物理表数据。");
        String deleteSqlNewest = "DELETE FROM PARA_PHYSICALPOINT_NG_NEWEST" +
                "  WHERE PROVINCE_ID = "+ province_id +
                "    AND VENDOR_ID = "+ vendor_id  +
                "    AND TYPE = '" + ne_name + "'";
        logger.info("插入前先删除，SQL为：" + deleteSqlNewest);
        PreparedStatement ps2 = connection.prepareStatement(deleteSqlNewest);
        count = ps2.executeUpdate();
        ps2.close();
        logger.info("删除的数据条数为：" + count);
        String insertSqlNew = "INSERT INTO PARA_PHYSICALPOINT_NG_NEWEST" +
                "     SELECT *" +
                "       FROM PARA_PHYSICALPOINT_NG" +
                "  WHERE PROVINCE_ID = "+ province_id +
                "    AND VENDOR_ID = "+ vendor_id +
                "    AND START_TIME = TO_DATE('" + start_time + "', 'yyyy-mm-dd')" +
                "    AND TYPE = '" + ne_name + "'";
        PreparedStatement ps3 = connection.prepareStatement(insertSqlNew);
        logger.info("插入数据的SQL为：" + insertSqlNew);
        count = ps3.executeUpdate();
        logger.info("插入的数据条数为：" + count);
        ps3.close();


    }



    public static void main(String[] args) throws Exception {

//        Class.forName("oracle.jdbc.OracleDriver");
//        ObtainRru rru = new ObtainRru();
//        rru.province_id = 101;
//        rru.vendor_id = 1;
//        rru.start_time = "2020-02-26";
//        rru.process();
    }
}
