package xdesign.com.accelerometer_test;

/**
 * @author Georgi Koemdzhiev created on 18/08/16.
 */
public class DataPoint {
    private double x;
    private double y;
    private double z;
    private double totalA;

    public DataPoint(double x, double y, double z, double totalA) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.totalA = totalA;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getTotalA() {
        return totalA;
    }

    public void setTotalA(double totalA) {
        this.totalA = totalA;
    }

    @Override
    public String toString() {
        return "x=" + x +
                "y=" + y +
                "z=" + z +
                "totA=" + totalA;
    }
}
