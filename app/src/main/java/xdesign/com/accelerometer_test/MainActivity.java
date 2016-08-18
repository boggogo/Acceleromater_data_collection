package xdesign.com.accelerometer_test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    @BindView(R.id.x_value)
    TextView mX;
    @BindView(R.id.y_value)
    TextView mY;
    @BindView(R.id.z_value)
    TextView mZ;
    @BindView(R.id.total_acc)
    TextView mTotalAcc;
    @BindView(R.id.listView)
    ListView mList;
    private SensorManager mSensorManager;
    private Sensor mAcceleromaterSensor;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private DecimalFormat decimalFormat;
    private ArrayAdapter<DataPoint> adapter;
    private ArrayList<DataPoint> dataPoints = new ArrayList<>();
    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
    private DescriptiveStatistics dsx = new DescriptiveStatistics();
    private DescriptiveStatistics dsy = new DescriptiveStatistics();
    private DescriptiveStatistics dsz = new DescriptiveStatistics();
    private Runnable intervalRunnable = new Runnable() {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, dsx.getStandardDeviation() + "\n" +
                            dsy.getStandardDeviation() + "\n" +
                            dsz.getStandardDeviation(), Toast.LENGTH_LONG).show();
                    dsx.clear();dsy.clear();dsz.clear();
                }
            });
        }
    };

    private int TIME_WINDOW = 2;
    private int TIME_INTERVAL = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcceleromaterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAcceleromaterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        decimalFormat = new DecimalFormat("0.000");

        adapter = new ArrayAdapter<DataPoint>(this, android.R.layout.simple_list_item_1, dataPoints);
        mList.setAdapter(adapter);


        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(intervalRunnable, TIME_WINDOW, TIME_INTERVAL, TimeUnit.SECONDS);
    }


    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcceleromaterSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        this.mX.setText(decimalFormat.format(linear_acceleration[0]));
        this.mY.setText(decimalFormat.format(linear_acceleration[1]));
        this.mZ.setText(decimalFormat.format(linear_acceleration[2]));
        this.mTotalAcc.setText(decimalFormat.format(
                calculateTotalAcceleration(linear_acceleration[0], linear_acceleration[1],
                        linear_acceleration[2])));

        DataPoint dataPoint = new DataPoint(Math.round(linear_acceleration[0]),
                Math.round(linear_acceleration[1]), Math.round(linear_acceleration[2]),
                Math.round(calculateTotalAcceleration(linear_acceleration[0],
                        linear_acceleration[1], linear_acceleration[2])));

        dsx.addValue(dataPoint.getX());
        dsy.addValue(dataPoint.getY());
        dsz.addValue(dataPoint.getZ());

        dataPoints.add(dataPoint);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public double calculateTotalAcceleration(double x, float y, float z) {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
