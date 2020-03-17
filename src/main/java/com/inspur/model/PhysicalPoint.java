package com.inspur.model;

import java.util.ArrayList;
import java.util.Date;

public class PhysicalPoint {
    String oid;//物理点OID
    String province_id;
    String citycode;
    String type;//物理点类型
    String type_num;//该物理点包含该网元的个数
    String type_oid_list;//该网元的OID列表
    String type_oid_base;//第一次基于哪个网元进行的判断
    String vendor_id;
    String start_time;//处理时间
    String longitude;//经度
    String latitude;//纬度
    Date insert_time;//插入时间
    int scan_seq;//物理点的扫描顺序

    @Override
    public String toString() {
        return "PhysicalPoint{" +
                "oid='" + oid + '\'' +
                ", province_id='" + province_id + '\'' +
                ", citycode='" + citycode + '\'' +
                ", type='" + type + '\'' +
                ", type_num='" + type_num + '\'' +
                ", type_oid_list='" + type_oid_list + '\'' +
                ", type_oid_base='" + type_oid_base + '\'' +
                ", vendor_id='" + vendor_id + '\'' +
                ", start_time='" + start_time + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", insert_time=" + insert_time +
                ", scan_seq=" + scan_seq +
                '}';
    }

    public String getType_oid_base() {
        return type_oid_base;
    }

    public void setType_oid_base(String type_oid_base) {
        this.type_oid_base = type_oid_base;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_num() {
        return type_num;
    }

    public void setType_num(String type_num) {
        this.type_num = type_num;
    }

    public String getType_oid_list() {
        return type_oid_list;
    }

    public void setType_oid_list(String type_oid_list) {
        this.type_oid_list = type_oid_list;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Date insert_time) {
        this.insert_time = insert_time;
    }

    public int getScan_seq() {
        return scan_seq;
    }

    public void setScan_seq(int scan_seq) {
        this.scan_seq = scan_seq;
    }
}
