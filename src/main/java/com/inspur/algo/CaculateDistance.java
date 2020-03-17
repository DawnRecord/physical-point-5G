package com.inspur.algo;

import com.inspur.model.PhysicalPoint;
import com.inspur.model.PhysicalPointRru;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

public class CaculateDistance{


    public static double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid){

        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

        return geoCurve.getEllipsoidalDistance();
    }

    public static String isPhysicalPoint(PhysicalPoint physicalPoint, PhysicalPointRru physicalPointRru){

        //中物理点的经纬度
        String markLongitudeStr = physicalPoint.getLongitude();
        String markLatitudeStr = physicalPoint.getLatitude();
        //其余各点的经纬度
        String tempLongitudeStr = physicalPointRru.getLongitude();
        String tempLatitudeStr = physicalPointRru.getLatitude();

        double markLongitude,markLatitude,tempLongitude,tempLatitude;
        double distance = 0;

        try {
            markLongitude = Double.parseDouble(markLongitudeStr);
            markLatitude = Double.parseDouble(markLatitudeStr);
        } catch (NumberFormatException e) {
            return "markNotString";
        }

        try {
            tempLongitude = Double.parseDouble(tempLongitudeStr);
            tempLatitude = Double.parseDouble(tempLatitudeStr);
        } catch (NumberFormatException e) {
            return "tempNotString";
        }

        GlobalCoordinates markGlobal = new GlobalCoordinates(markLatitude,markLongitude);
        GlobalCoordinates tempGlobal = new GlobalCoordinates(tempLatitude,tempLongitude);

        distance = getDistanceMeter(markGlobal, tempGlobal, Ellipsoid.Sphere);

//        System.out.println(markGlobal.toString() + "和" + tempGlobal.toString() + "距离为：" + distance);

        if(distance <= 50){
            return "yes";
        }

        return "not";

    }
    public static String isPhysicalPoint(PhysicalPointRru infoRruMark, PhysicalPointRru tempInfoRru){
        //中心标志的经纬度
        String markLongitudeStr = infoRruMark.getLongitude();
        String markLatitudeStr = infoRruMark.getLatitude();
        //其余各点的经纬度
        String tempLongitudeStr = tempInfoRru.getLongitude();
        String tempLatitudeStr = tempInfoRru.getLatitude();

        double markLongitude,markLatitude,tempLongitude,tempLatitude;
        double distance = 0;

        try {
            markLongitude = Double.parseDouble(markLongitudeStr);
            markLatitude = Double.parseDouble(markLatitudeStr);
        } catch (Exception e) {
            return "markNotString";
        }

        try {
            tempLongitude = Double.parseDouble(tempLongitudeStr);
            tempLatitude = Double.parseDouble(tempLatitudeStr);
        } catch (Exception e) {
            return "tempNotString";
        }

        GlobalCoordinates markGlobal = new GlobalCoordinates(markLatitude,markLongitude);
        GlobalCoordinates tempGlobal = new GlobalCoordinates(tempLatitude,tempLongitude);

        distance = getDistanceMeter(markGlobal, tempGlobal, Ellipsoid.Sphere);

//        System.out.println(markGlobal.toString() + "和" + tempGlobal.toString() + "距离为：" + distance);

        if(distance <= 50){
            return "yes";
        }

        return "not";
    }

    public static void main(String[] args){

//        GlobalCoordinates source = new GlobalCoordinates(29.490295, 106.486654);
//        GlobalCoordinates target = new GlobalCoordinates(29.615467, 106.581515);
//
//        double meter1 = getDistanceMeter(source, target, Ellipsoid.Sphere);
//        double meter2 = getDistanceMeter(source, target, Ellipsoid.WGS84);
//
//        System.out.println("Sphere坐标系计算结果："+meter1 + "米");
        CaculateDistance caculateDistance = new CaculateDistance();
        PhysicalPointRru physicalPointRru1 = new PhysicalPointRru();
        PhysicalPointRru physicalPointRru2 = new PhysicalPointRru();

        //test
        physicalPointRru1.setLatitude("40.174223");
        physicalPointRru1.setLongitude("116.381093");
        physicalPointRru2.setLatitude("40.174223");
        physicalPointRru2.setLongitude("116.3815");
//        physicalPointRru2.setLatitude("40.174223");
//        physicalPointRru2.setLongitude("116.381093");

        String result = caculateDistance.isPhysicalPoint(physicalPointRru1,physicalPointRru2);

        System.out.println(result);

    }
}