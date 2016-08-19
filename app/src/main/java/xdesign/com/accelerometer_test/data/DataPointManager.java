package xdesign.com.accelerometer_test.data;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;

/**
 * @author Georgi Koemdzhiev created on 19/08/16.
 */
public class DataPointManager {
    private ArrayList<DataPoint> dataPoints;
    private DescriptiveStatistics dsX;
    private DescriptiveStatistics dsY;
    private DescriptiveStatistics dsZ;
    private int ACTIVITY_TYPE = 0;

    public DataPointManager(int activityType) {
        dataPoints = new ArrayList<>();
        dsX = new DescriptiveStatistics();
        dsY = new DescriptiveStatistics();
        dsZ = new DescriptiveStatistics();
        this.ACTIVITY_TYPE = activityType;
    }


    public void add(DataPoint dp) {
        this.dataPoints.add(dp);
    }

    public void addDSX(double dsx) {
        this.dsX.addValue(dsx);
    }

    public void addDSY(double dsy) {
        this.dsY.addValue(dsy);
    }

    public void addDSZ(double dsz) {
        this.dsZ.addValue(dsz);
    }


    public double getCurrentDSForCurrentXvalues() {
        double xds = dsX.getStandardDeviation();
        dsX.clear();
        return xds;
    }

    public double getCurrentDSForCurrentYvalues() {
        double yds = dsY.getStandardDeviation();
        dsY.clear();
        return yds;
    }

    public double getCurrentDSForCurrentZvalues() {
        double xds = dsZ.getStandardDeviation();
        dsZ.clear();
        return xds;
    }

    public double[] getDSXYZ_ActivityType() {
        double[] ds = new double[4];
        ds[0] = getCurrentDSForCurrentXvalues();
        ds[1] = getCurrentDSForCurrentYvalues();
        ds[2] = getCurrentDSForCurrentZvalues();
        // walking is 0 running is 1
        ds[3] = ACTIVITY_TYPE;

        return ds;
    }


    public void clear() {
        this.dataPoints.clear();
    }

    public void clearDSs() {
        this.dsX.clear();
        this.dsY.clear();
        this.dsZ.clear();
    }

    public int size() {
        return this.dataPoints.size();
    }

    public ArrayList<DataPoint> getDataPoints() {
        return dataPoints;
    }


}
