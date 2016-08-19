package xdesign.com.accelerometer_test.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import xdesign.com.accelerometer_test.R;
import xdesign.com.accelerometer_test.data.DataPoint;

public class GraphActivity extends AppCompatActivity {
    @BindView(R.id.chartView)
    LineChartView mChart;
    private ArrayList<DataPoint> dataPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ButterKnife.bind(this);
        dataPoints = (ArrayList<DataPoint>) getIntent().getExtras().getSerializable(MainActivity.KEY_DATA_POINTS);

        List<PointValue> xValues = new ArrayList<>();
        List<PointValue> yValues = new ArrayList<>();
        List<PointValue> zValues = new ArrayList<>();

        for (int i = 0; i < dataPoints.size(); i++) {
            xValues.add(new PointValue(i, (float) dataPoints.get(i).getX()));
            yValues.add(new PointValue(i, (float) dataPoints.get(i).getY()));
            zValues.add(new PointValue(i, (float) dataPoints.get(i).getZ()));
        }

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line xRedLine = new Line(xValues).setColor(Color.RED).setCubic(true);
        Line yGreenLine = new Line(yValues).setColor(Color.GREEN).setCubic(true);
        Line zGlueLine = new Line(zValues).setColor(Color.BLUE).setCubic(true);

        List<Line> lines = new ArrayList<>();
        lines.add(xRedLine);
        lines.add(yGreenLine);
        lines.add(zGlueLine);

        LineChartData data = new LineChartData();
        data.setLines(lines);


        mChart.setLineChartData(data);
    }

    public ArrayList<AxisValue> getTimeStamps(ArrayList<DataPoint> dps) {
        ArrayList<AxisValue> timeStamps = new ArrayList<>();

        for (DataPoint dp : dps) {
            timeStamps.add(new AxisValue(dp.getTimeStamp()));
        }

        return timeStamps;
    }
}
