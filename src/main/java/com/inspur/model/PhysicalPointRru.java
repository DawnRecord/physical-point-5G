package com.inspur.model;

public class PhysicalPointRru {
    String oid;
    String longitude;
    String latitude;
    String gnbId;
    String cityCode;
    String vendorId;


    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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

    public String getGnbId() {
        return gnbId;
    }

    public void setGnbId(String gnbId) {
        this.gnbId = gnbId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "PhysicalPointRru{" +
                "oid='" + oid + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", gnbId='" + gnbId + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", vendorId='" + vendorId + '\'' +
                '}';
    }
}
