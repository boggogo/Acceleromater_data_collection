package xdesign.com.accelerometer_test.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * @author Georgi Koemdzhiev created on 19/08/16.
 */
public class DataPointUtils {

    public static ArrayList<Attribute> constructAttributeNames() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("StandardDeviationX"));
        attributes.add(new Attribute("StandardDeviationY"));
        attributes.add(new Attribute("StandardDeviationZ"));
        FastVector fvClassVal = new FastVector(2);
        fvClassVal.addElement("0");
        fvClassVal.addElement("1");
        Attribute ClassAttribute = new Attribute("ActivityTypeClass", fvClassVal);
        attributes.add(ClassAttribute);

        return attributes;
    }

    public static void saveCurrentDataToArffFile(Instances instances) throws IOException {

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, "/" + "activity_recognition" + System.currentTimeMillis() + ".arff");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(instances.toString());
        writer.flush();
        writer.close();

        Log.d(DataPointUtils.class.getSimpleName(), "DATA SAVED TO A ARFF file");
    }
}
