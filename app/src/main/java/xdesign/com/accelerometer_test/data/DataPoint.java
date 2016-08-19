package xdesign.com.accelerometer_test.data;

import java.io.Serializable;

/**
 * @author Georgi Koemdzhiev created on 18/08/16.
 */
public class DataPoint implements Serializable {
    private double x;
    private double y;
    private double z;
    private long timeStamp;


    public DataPoint(double x, double y, double z, long timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = timeStamp;
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


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getTotalA() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double[] getXYZ() {
        return new double[]{
                x,
                y,
                z
        };
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z + "," + getTimeStamp();
    }

}
