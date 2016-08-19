package xdesign.com.accelerometer_test;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String KEY_DATA_POINTS = "data_points";
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
    private int fileCounter = 0;
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
                    try {
                        saveCurrentDataToArffFile(dataPoints);
                        Log.d(TAG, "DATA SAVED TO A ARFF file");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dataPoints.clear();
                    adapter.notifyDataSetChanged();
                    dsx.clear();
                    dsy.clear();
                    dsz.clear();
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

        DataPoint dataPoint = new DataPoint(Math.round(linear_acceleration[0]),
                Math.round(linear_acceleration[1]), Math.round(linear_acceleration[2]),
                System.currentTimeMillis());

        mTotalAcc.setText(decimalFormat.format(dataPoint.getTotalA()));

        dsx.addValue(dataPoint.getX());
        dsy.addValue(dataPoint.getY());
        dsz.addValue(dataPoint.getZ());

        dataPoints.add(dataPoint);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handle tool bar menu item clicks...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_graph) {
            Intent intent = new Intent(MainActivity.this, GraphActivity.class);
            intent.putExtra(KEY_DATA_POINTS, dataPoints);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_delete_current_data) {
            this.dataPoints.clear();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }


    public void saveCurrentDataToArffFile(ArrayList<DataPoint> dataPoints) throws IOException {
        ArrayList<Attribute> attributes = new ArrayList<>(dataPoints.size());
        attributes.add(new Attribute("X"));
        attributes.add(new Attribute("Y"));
        attributes.add(new Attribute("Z"));
//        for (DataPoint dp : dataPoints) {
//            attributes.add(new Attribute("x"));
//            attributes.add(new Attribute("StandardDeviation y"));
//            attributes.add(new Attribute("StandardDeviation z"));
//        }

        Instances dataSet = new Instances("ActivityRecognition", attributes, dataPoints.size());
        for (DataPoint dp : dataPoints) {
            dataSet.add(new DenseInstance(1.0, dp.getXYZ()));
        }

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, "/" + "activity_recognition" + fileCounter++ + ".arff");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(dataSet.toString());
        writer.flush();
        writer.close();
    }


}
