package com.example.testMaps;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2022/7/18 17:09
 */
public class TestMapChange {
    private static final double BAIDU_FACTOR = (Math.PI * 3000.0) / 180.0;

    public static void main(String[] args) {
//        double lon = 113.938679;
//        double lat = 22.579607;
        double lat = 118.164167;
        double lon = 24.529768;
        GPS origin = new GPS(lon, lat);
//        GPS target = BD09ToGCJ02(origin);
//        System.out.println(target);

        GPS gps1 = new GPS(113.938679, 22.579607);
        GPS gps2 = new GPS(113.939419, 22.579747);
//        GPS gps1 = new GPS(22.579607, 113.938679);
//        GPS gps2 = new GPS(22.579747, 113.939419);
//        GPS gps1 = new GPS(lon, lat);
//        GPS gps2 = new GPS(lon, lat);
        System.out.println(gps1 + " : " + gps2 + " = " + distance(gps1, gps2));
    }

    public static Integer distance(GPS origin, GPS target) {
        int distance = 0;
        double dDistance = Math.acos(Math.sin((Math.PI / 180) * origin.lat) * Math.sin((Math.PI / 180) * target.lat)
                + (Math.cos((Math.PI / 180) * origin.lat) * Math.cos((Math.PI / 180) * target.lat)
                * Math.cos((Math.PI / 180) * origin.lon - (Math.PI / 180) * target.lon))
        ) * 6371 * 1000;
        distance = (int) dDistance;
        return distance;
    }

    public static GPS BD09ToGCJ02(GPS origin){

        double x = origin.lon - 0.0065;
        double y = origin.lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * BAIDU_FACTOR);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * BAIDU_FACTOR);
        return new GPS(z * Math.cos(theta), z * Math.sin(theta));
    }

    public static class GPS {
        public GPS (double lon, double lat) {
            this.lat = lat;
            this.lon = lon;
        }
        double lat;
        double lon;

        @Override
        public String toString() {
            return "GPS{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
    }
}
