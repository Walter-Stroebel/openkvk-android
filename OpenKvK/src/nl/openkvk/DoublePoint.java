package nl.openkvk;

public class DoublePoint {
    /**
     * Google coordinates, WGS84 in radians.
     */
    public final double x, y, lon, lat;

    public DoublePoint(double x, double y, double lon, double lat) {
        this.x = x;
        this.y = y;
        this.lon = lon;
        this.lat = lat;
    }

    public static DoublePoint mercator(double lon, double lat) {
        double[] xy = MercatorTransform.forward(lon, lat);
        return new DoublePoint(xy[0], xy[1], Math.toRadians(lon), Math.toRadians(lat));
    }

    public static DoublePoint xy(double x, double y) {
        double[] ll = MercatorTransform.inverse(x, y);
        return new DoublePoint(x, y, Math.toRadians(ll[0]), Math.toRadians(ll[1]));
    }

    public static double distance(DoublePoint p1, DoublePoint p2) {
        double R = 6371000; // m
        return Math.acos(Math.sin(p1.lat) * Math.sin(p1.lat) + Math.cos(p1.lat) * Math.cos(p2.lat) * Math.cos(p2.lon - p1.lon)) * R;
    }
}
