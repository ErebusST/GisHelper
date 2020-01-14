/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.house.util;

import com.base.tools.ListUtils;
import com.base.tools.ObjectUtils;
import com.base.tools.StringUtils;
import com.google.common.collect.Lists;
import com.house.model.business.Point;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 获取某个点的六边形各个点的坐标
 *
 * @author 司徒彬
 * @date 2020 -01-08 12:12
 */
public class GisUtils {
    //常量 二分之一
    private static final BigDecimal ERFENZHIYI = BigDecimal.valueOf(0.5);

    private static final Integer SCALE = 10;

    private static final BigDecimal ERFENZHISAN = BigDecimal.valueOf(3).multiply(ERFENZHIYI);

    private static final BigDecimal GENHAOSAN = BigDecimal.valueOf(Math.sqrt(3));
    //常量 二分之根号三
    private static final BigDecimal ERFENZHIGENHAOSAN = GENHAOSAN.multiply(ERFENZHIYI);

    /**
     * Generate data list.
     *
     * @param center       the center
     * @param centerRadius the center radius
     * @param hiveRadius   the hive radius
     * @return the list
     */
    public static List<Point> generateData(Point center, BigDecimal centerRadius, BigDecimal hiveRadius) {
        try {


            List<Point> result = new ArrayList<>();

            int count = centerRadius.divide(hiveRadius, 0, BigDecimal.ROUND_UP).intValue() + 1;

            Point start = center;// BD092Mercator(center);

            List<Point> borderPoints = getSixPoint(start, hiveRadius);
            start.setPoints(borderPoints);
            result.add(start);
            result.addAll(getCurrentRowPointsInfo(start, hiveRadius, count));
            //向上/下求点
            List<Point> upPoints = new ArrayList<>();
            List<Point> downPoints = new ArrayList<>();
            List<Point> details = new ArrayList<>();
            IntStream.range(0, count).forEach(index -> {
                Point lastUpPoint;
                if (upPoints.size() == 0) {
                    lastUpPoint = start;
                } else {
                    lastUpPoint = upPoints.get(upPoints.size() - 1);
                }
                Point upPoint = GisUtils.getNextCenterPoint(lastUpPoint, hiveRadius, HexagonType.UP);
                List<Point> upSixPoints = GisUtils.getSixPoint(upPoint, hiveRadius);
                upPoint.setPoints(upSixPoints);
                upPoints.add(upPoint);
                //再获取当前行所有的点信息
                details.addAll(GisUtils.getCurrentRowPointsInfo(upPoint, hiveRadius, count));


                //向下
                Point lastDownPoint;
                if (downPoints.size() == 0) {
                    lastDownPoint = start;
                } else {
                    lastDownPoint = downPoints.get(downPoints.size() - 1);
                }
                Point downPoint = GisUtils.getNextCenterPoint(lastDownPoint, hiveRadius, HexagonType.DOWN);
                List<Point> downSixPoints = GisUtils.getSixPoint(downPoint, hiveRadius);
                downPoint.setPoints(downSixPoints);
                downPoints.add(downPoint);
                //再获取当前行所有的点信息
                details.addAll(GisUtils.getCurrentRowPointsInfo(downPoint, hiveRadius, count));
            });

            result = ListUtils.unionAll(result, upPoints, downPoints, details);

            result = result.stream().map(object -> {
                long inCircle = object.getPoints().stream().filter(child -> GisUtils.checkPointInCircle(center, child, centerRadius)).count();
                if (inCircle > 0) {
                    return object;
                } else {
                    return null;
                }
            }).filter(ObjectUtils::isNotNull).collect(Collectors.toList());

            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets current row points info.
     *
     * @param point  the point
     * @param radius the radius
     * @param count  the count
     * @return the current row points info
     */
    public static List<Point> getCurrentRowPointsInfo(Point point, BigDecimal radius, Integer count) {
        List<Point> leftPoints = new ArrayList<>(count);
        List<Point> rightPoints = new ArrayList<>(count);
        IntStream.range(0, count).forEach(index -> {
            Point lastLeftPoint;
            if (leftPoints.size() == 0) {
                lastLeftPoint = point;
            } else {
                lastLeftPoint = leftPoints.get(leftPoints.size() - 1);
            }

            Point nextLeftPoint = GisUtils.getNextCenterPoint(lastLeftPoint, radius, HexagonType.LEFT);
            List<Point> leftChildPoints = GisUtils.getSixPoint(nextLeftPoint, radius);
            nextLeftPoint.setPoints(leftChildPoints);
            leftPoints.add(nextLeftPoint);

            Point lastRightPoint;
            if (rightPoints.size() == 0) {
                lastRightPoint = point;
            } else {
                lastRightPoint = rightPoints.get(rightPoints.size() - 1);
            }

            Point nextRightPoint = GisUtils.getNextCenterPoint(lastRightPoint, radius, HexagonType.RIGHT);
            List<Point> rightChildPoints = GisUtils.getSixPoint(nextRightPoint, radius);
            nextRightPoint.setPoints(rightChildPoints);
            rightPoints.add(nextRightPoint);
        });

        return ListUtils.unionAll(leftPoints, rightPoints);
    }

    /**
     * 获取六边形六个点的坐标，顺序为从最高点（顶点）开始顺时针共计六个点
     *
     * @param center the center
     * @param radius the radius
     * @return six point
     */
    public static List<Point> getSixPoint(Point center, BigDecimal radius) {
        List<Point> points = new ArrayList<>(6);
        // Point point1 = new Point(center.getLng(), center.getLat().add(radius));
        Point point1 = getPointByRadius(center, 0, radius.doubleValue());
        points.add(point1);
        Point point2 = getPointByRadius(center, 60, radius.doubleValue());
        points.add(point2);
        Point point3 = getPointByRadius(center, 120, radius.doubleValue());
        points.add(point3);
        Point point4 = getPointByRadius(center, 180, radius.doubleValue());
        points.add(point4);
        Point point5 = getPointByRadius(center, 240, radius.doubleValue());
        points.add(point5);
        Point point6 = getPointByRadius(center, 300, radius.doubleValue());
        points.add(point6);
        return points;
    }


    /**
     * Gets next center point.
     *
     * @param current the current
     * @param radius  the radius
     * @param type    the type
     * @return the next center point
     */
    public static Point getNextCenterPoint(Point current, BigDecimal radius, HexagonType type) {
        switch (type) {
            case LEFT:
                return getNextRowCenterPoint(current, radius, HexagonType.LEFT);
            case RIGHT:
                return getNextRowCenterPoint(current, radius, HexagonType.RIGHT);
            case UP:
                return getNextColCenterPoint(current, radius, HexagonType.UP);
            case DOWN:
                return getNextColCenterPoint(current, radius, HexagonType.DOWN);
            default:
                return current;
        }

    }


    /**
     * 获取下一个六边形中心点
     *
     * @param center 上一个中心店
     * @param radius
     * @param type   left or right
     * @return
     */
    private static Point getNextRowCenterPoint(Point center, BigDecimal radius, HexagonType type) {
        //两点距离为 根号3R
        //间距
        if (type.equals(HexagonType.RIGHT)) {
            return getPointByRadius(center, 90, GENHAOSAN.multiply(radius).doubleValue());
        } else {
            return getPointByRadius(center, 270, GENHAOSAN.multiply(radius).doubleValue());
        }
    }

    /**
     * Gets next col center point.
     *
     * @param center the center
     * @param radius the radius
     * @param type   the type
     * @return the next col center point
     */
    public static Point getNextColCenterPoint(Point center, BigDecimal radius, HexagonType type) {
        //横坐标为 x + 根号三/2*R 纵坐标为 y + 二分之三*R
        if (type.equals(HexagonType.UP)) {
            return getPointByRadius(center, 330, GENHAOSAN.multiply(radius).doubleValue());
        } else {
            return getPointByRadius(center, 210, GENHAOSAN.multiply(radius).doubleValue());
        }
    }

    /**
     * Check point in circle boolean.
     * <p>
     * 需要经纬度坐标系
     *
     * @param center the center
     * @param point  the point
     * @param radius the radius
     * @return the boolean
     */
    public static boolean checkPointInCircle(Point center, Point point, BigDecimal radius) {
        double distance = distance(center, point);
        return radius.doubleValue() >= distance;
    }


    /**
     * 判断是否在多边形区域内
     *
     * @param point   the point 要判断的点
     * @param polygon the polygon 要按顺序依次排列 顺时针或者逆时针
     * @return boolean boolean
     */
    public static boolean checkPointInPolygon(Point point, List<Point> polygon) {
        if (polygon.size() == 0 || polygon.size() == 1) {
            return false;
        }
        double pointLon = point.getLng().doubleValue();
        double pointLat = point.getLat().doubleValue();
        // 将要判断的横纵坐标组成一个点
        Point2D.Double point2D = new Point2D.Double(pointLon, pointLat);
        GeneralPath border = pointsToPolygon(polygon);
        return border.contains(point2D);
    }

    private static GeneralPath pointsToPolygon(List<Point> polygon) {
        GeneralPath border = new GeneralPath();
        Point first = polygon.get(0);
        //通过移动到指定坐标（以双精度指定），将一个点添加到路径中
        border.moveTo(first.getLng().doubleValue(), first.getLat().doubleValue());
        polygon.stream().forEach(p -> {
            //通过绘制一条从当前坐标到新指定坐标（以双精度指定）的直线，将一个点添加到路径中。
            border.lineTo(p.getLng().doubleValue(), p.getLat().doubleValue());
        });

        // 将几何多边形封闭
        border.lineTo(first.getLng().doubleValue(), first.getLat().doubleValue());
        border.closePath();
        return border;
    }

    /**
     * Check polygons intersect boolean.
     *
     * @param polygon1 the polygon 1
     * @param polygon2 the polygon 2
     * @return the boolean
     */
    public static boolean checkPolygonsIntersect(List<Point> polygon1, List<Point> polygon2) {
        try {
            List<Line> polygonLines1 = getPolygonLines(polygon1);
            List<Line> polygonLines2 = getPolygonLines(polygon2);
            for (Line line1 : polygonLines1) {
                for (Line line2 : polygonLines2) {
                    boolean intersect = Line2D
                            .linesIntersect(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.y1, line2.x2, line2.y2);
                    if (intersect) {
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            for (Point point : polygon2) {
                boolean intersect = checkPointInPolygon(point, polygon1);
                if (intersect) {
                    return true;
                }
            }
            for (Point point : polygon1) {
                boolean intersect = checkPointInPolygon(point, polygon2);
                if (intersect) {
                    return true;
                }
            }
            return false;
//            List<Line> polygonLines1 = getPolygonLines(polygon1);
//            List<Line> polygonLines2 = getPolygonLines(polygon2);
//            boolean intersect = false;
//            for (Line line1 : polygonLines1) {
//                boolean isBreak = false;
//                for (Line line2 : polygonLines2) {
//                    intersect = Line2D
//                            .linesIntersect(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.y1, line2.x2, line2.y2);
//                    if (intersect) {
//                        isBreak = true;
//                        break;
//                    } else {
//                        isBreak = false;
//                        continue;
//                    }
//                }
//                if (isBreak) {
//                    break;
//                }
//            }
//            return intersect;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 获取多边形的各个边
     *
     * @param points
     * @return
     */
    private static List<Line> getPolygonLines(List<Point> points) {
        List<Line> lines = new ArrayList<>(points.size());
        int size = points.size() - 1;
        for (int i = 0; i < size; i++) {
            Point point = points.get(i);
            Point end = points.get(i + 1);
            Line line = new Line(point.getLng(), point.getLat(), end.getLng(), end.getLat());
            lines.add(line);
        }
        return lines;
    }

    @Getter
    @Setter
    private static class Line {
        double x1;
        double y1;
        double x2;
        double y2;

        Line(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
            this.x1 = x1.doubleValue();
            this.y1 = y1.doubleValue();
            this.x2 = x2.doubleValue();
            this.y2 = y2.doubleValue();
        }
    }


    /**
     * Distance double. 单位:米
     * <p>
     * 需要经纬度坐标系
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @return the double
     */
    public static double distance(Point point1, Point point2) {
        double lat1 = point1.getLat().doubleValue();
        double lng1 = point1.getLng().doubleValue();
        double lat2 = point2.getLat().doubleValue();
        double lng2 = point2.getLng().doubleValue();

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // 单位转换成米
        return distance;
    }


    /**
     * 根据一点的坐标与距离，以及方向，计算另外一点的位置
     * <p>
     * 需要经纬度坐标系
     * <p>
     * Point up = GisUtils.getPointByRadius(center, 0, distance);
     * Point right = GisUtils.getPointByRadius(center, 90, distance);
     * Point down = GisUtils.getPointByRadius(center, 180, distance);
     * Point left = GisUtils.getPointByRadius(center, 270, distance);
     *
     * @param point    the point
     * @param angle    角度，从正北顺时针方向开始计算
     * @param distance 距离，单位m
     * @return point point by radius
     */
    public static Point getPointByRadius(Point point, double angle, double distance) {
        double startLat = point.getLat().doubleValue();
        double startLng = point.getLng().doubleValue();
        //将距离转换成经度的计算公式
        double δ = distance / (EARTH_RADIUS * 1000);
        // 转换为radian，否则结果会不正确
        angle = Math.toRadians(angle);
        startLng = Math.toRadians(startLng);
        startLat = Math.toRadians(startLat);
        double lat = Math.asin(Math.sin(startLat) * Math.cos(δ) + Math.cos(startLat) * Math.sin(δ) * Math.cos(angle));
        double lng = startLng + Math.atan2(Math.sin(angle) * Math.sin(δ) * Math.cos(startLat), Math.cos(δ) - Math.sin(startLat) * Math.sin(lat));
        // 转为正常的10进制经纬度
        lng = Math.toDegrees(lng);
        lat = Math.toDegrees(lat);
        return new Point(lng, lat);
    }


    private static final Double EARTH_RADIUS = 6370996.81 / 1000;
    private static final List<Double> MERCATOR_BAND;
    private static final List<Double> LL_BAND;
    private static final Double[][] MC2LL = {
            {1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331, 200.9824383106796, -187.2403703815547,
                    91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 17337981.2},
            {-7.435856389565537e-9, 0.000008983055097726239, -0.78625201886289, 96.32687599759846, -1.85204757529826,
                    -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86},
            {-3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616, 59.74293618442277, 7.357984074871,
                    -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37},
            {-1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591, 40.31678527705744, 0.65659298677277,
                    -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06},
            {3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062, 23.10934304144901, -0.00023663490511,
                    -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4},
            {2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8, 7.47137025468032, -0.00000353937994,
                    -0.02145144861037, -0.00001234426596, 0.00010322952773, -0.00000323890364, 826088.5}
    };
    private static final Double[][] LL2MC = {
            {-0.0015702102444, 111320.7020616939, 1704480524535203d, -10338987376042340d, 26112667856603880d,
                    -35149669176653700d, 26595700718403920d, -10725012454188240d, 1800819912950474d, 82.5},
            {0.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142,
                    -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5},
            {0.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455,
                    -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5},
            {0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013,
                    -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5},
            {-0.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378,
                    54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5},
            {-0.0003218135878613132, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093,
                    2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}
    };
    private static final Integer MERCATOR_BAND_LENGTH;
    private static final Integer LL_BAND_LENGTH;

    static {
        MERCATOR_BAND = Lists.newArrayList(12890594.86, 8362377.87, 5591021d, 3481989.83, 1678043.12, 0d);
        MERCATOR_BAND_LENGTH = MERCATOR_BAND.size();
        LL_BAND = Lists.newArrayList(75d, 60d, 45d, 30d, 15d, 0d);
        LL_BAND_LENGTH = LL_BAND.size();
    }


    /**
     * 墨卡托坐标转经纬度坐标
     *
     * @param point the point
     * @return map point
     */
    public static Point Mercator2BD09(Point point) {
        double x = point.getLng().doubleValue();
        double y = point.getLat().doubleValue();

        Double[] cF = null;
        x = Math.abs(x);
        y = Math.abs(y);

        for (int cE = 0; cE < MERCATOR_BAND_LENGTH; cE++) {
            if (y >= MERCATOR_BAND.get(cE)) {
                cF = MC2LL[cE];
                break;
            }
        }
        Pair<Double, Double> location = converter(x, y, cF);
        Point result = new Point(location.getLeft(), location.getRight());
        return result;
    }

    /**
     * 经纬度坐标转墨卡托坐标
     *
     * @param x
     * @param y
     * @param cE
     * @return
     */
    private static Pair<Double, Double> converter(Double x, Double y, Double[] cE) {
        Double xTemp = cE[0] + cE[1] * Math.abs(x);
        Double cC = Math.abs(y) / cE[9];
        Double yTemp = cE[2] + cE[3] * cC + cE[4] * cC * cC + cE[5] * cC * cC * cC + cE[6] * cC * cC * cC * cC + cE[7] * cC * cC * cC * cC * cC + cE[8] * cC * cC * cC * cC * cC * cC;
        xTemp *= (x < 0 ? -1 : 1);
        yTemp *= (y < 0 ? -1 : 1);
        return Pair.of(xTemp, yTemp);
    }

    /**
     * Bd 092 mercator point.
     *
     * @param point the point
     * @return the point
     */
    public static Point BD092Mercator(Point point) {
        double lng = point.getLng().doubleValue();
        double lat = point.getLat().doubleValue();

        Double[] cE = null;
        lng = getLoop(lng, -180, 180);
        lat = getRange(lat, -74, 74);
        for (int i = 0; i < LL_BAND_LENGTH; i++) {
            if (lat >= LL_BAND.get(i)) {
                cE = LL2MC[i];
                break;
            }
        }
        if (cE != null) {
            for (int i = LL_BAND_LENGTH - 1; i >= 0; i--) {
                if (lat <= -LL_BAND.get(i)) {
                    cE = LL2MC[i];
                    break;
                }
            }
        }

        Pair<Double, Double> location = converter(lng, lat, cE);
        Point result = new Point(location.getLeft(), location.getRight());
        return result;
    }


    /**
     * @param lng
     * @param min
     * @param max
     * @return
     */
    private static Double getLoop(Double lng, Integer min, Integer max) {
        while (lng > max) {
            lng -= max - min;
        }
        while (lng < min) {
            lng += max - min;
        }
        return lng;
    }

    /**
     * @param lat
     * @param min
     * @param max
     * @return
     */
    private static Double getRange(Double lat, Integer min, Integer max) {
        if (min != null) {
            lat = Math.max(lat, min);
        }
        if (max != null) {
            lat = Math.min(lat, max);
        }
        return lat;
    }


    /**
     * Fix geo info list.
     *
     * @param baiGeo the bai geo
     * @return the list
     */
    public static List<Point> fixGeoInfo(String baiGeo) {
        try {
            return StringUtils.splitToList(baiGeo, ";").stream().map(pointStr -> {
                List<String> strings = StringUtils.splitToList(pointStr, ",");
                if (strings.size() != 2
                        || (strings.size() == 2 && (!StringUtils.isFloat(strings.get(0))) || !StringUtils.isFloat(strings.get(1)))) {
                    return null;
                } else {
                    return new Point(strings.get(0), strings.get(1));
                }

            }).filter(ObjectUtils::isNotNull).collect(Collectors.toList());
        } catch (Exception ex) {
            throw ex;
        }
    }
}
