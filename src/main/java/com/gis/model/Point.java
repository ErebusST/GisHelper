/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.gis.model;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 司徒彬
 * @date 2019-12-09 15:16
 */
@Getter
@Setter
public class Point {

    public Point() {

    }

    public Point(BigDecimal x, BigDecimal y) {
        this.lng = x;
        this.lat = y;
    }

    public Point(Double x, Double y) {
        this.lng = BigDecimal.valueOf(x);
        this.lat = BigDecimal.valueOf(y);
    }

    public Point(String x, String y) {
        this.lng = new BigDecimal(x);
        this.lat = new BigDecimal(y);
    }


    /**
     * 经度 X
     */
    private BigDecimal lng;
    /**
     * 纬度 Y
     */
    private BigDecimal lat;

    List<Point> points;


    String id;

    String type;
    private boolean exist = true;


    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        object.addProperty("lng", lng);
        object.addProperty("lat", lat);
        return object.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Point that = (Point) obj;
        return (this.lng.floatValue() + " " + this.lat.floatValue())
                .equalsIgnoreCase(that.getLng().floatValue() + " " + that.getLat().floatValue());
    }


}
