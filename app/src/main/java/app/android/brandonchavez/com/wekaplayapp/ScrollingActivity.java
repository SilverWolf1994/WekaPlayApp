package app.android.brandonchavez.com.wekaplayapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class ScrollingActivity extends AppCompatActivity {

    private Spinner spAttributeSkyA;
    private EditText etAttributeTemperatureA, etAttributeHumidityA;
    private CheckBox cbAttributeWindyA;
    private TextView tvPredictionA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spAttributeSkyA = (Spinner) findViewById(R.id.spAttributeSky);
        etAttributeTemperatureA = (EditText) findViewById(R.id.etAttributeTemperature);
        etAttributeHumidityA = (EditText) findViewById(R.id.etAttributeHumidity);
        cbAttributeWindyA = (CheckBox) findViewById(R.id.cbAttributeWindy);
        tvPredictionA = (TextView) findViewById(R.id.tvPrediction);
        Button bPredictionA = (Button) findViewById(R.id.bPrediction);

        bPredictionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String outlook = spAttributeSkyA.getSelectedItem().toString();
                Double temperature = Double.parseDouble(etAttributeTemperatureA.getText().toString());
                Double humidity = Double.parseDouble(etAttributeHumidityA.getText().toString());
                String windy = windyTime();
                wekaPrediction(outlook, temperature, humidity, windy);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private Instances wekaLoadData()
    {
        Instances data = null;
        try {
            InputStream inputStream = getAssets().open("weather.arff");
            BufferedReader dataset = new BufferedReader(new InputStreamReader(inputStream));
            data = new Instances(dataset);
            dataset.close();
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (Exception exception) {
            Log.e("EXCEPTION:\n", exception.getMessage());
        }

        return data;
    }

    private void wekaPrediction(String outlook, Double temperature, Double humidity, String windy)
    {
        try {

            Instances data = wekaLoadData();

            J48 classifier = new J48();
            classifier.setUnpruned(true);
            FilteredClassifier filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifier);
            filteredClassifier.buildClassifier(data);

            Evaluation evaluation = new Evaluation(data);
            evaluation.evaluateModel(filteredClassifier, data);
            Toast.makeText(getApplication(), "Correct: " + evaluation.correct(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplication(), "Incorrect: " + evaluation.incorrect(), Toast.LENGTH_LONG).show();

            Instance instance = data.instance(data.numAttributes());
            instance.setDataset(data);
            instance.setValue(0, outlook);
            instance.setValue(1, temperature);
            instance.setValue(2, humidity);
            instance.setValue(3, windy);

            double prediction = filteredClassifier.classifyInstance(instance);
            tvPredictionA.setText(instance.classAttribute().value((int) prediction));

            Log.i("fclassifier:\n", filteredClassifier.toString());

        } catch (Exception exception) {
            Log.e("EXCEPTION:\n", exception.getMessage());
        }
    }

    public String windyTime()
    {
        if (cbAttributeWindyA.isChecked())
        {
            return "TRUE";
        }
        else
        {
            return "FALSE";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
