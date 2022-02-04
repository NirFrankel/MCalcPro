package com.example.mcalcpro;

import ca.roumani.i2c.MPro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mcalcpro.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    private TextToSpeech tts;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        this.tts = new TextToSpeech(this, this);

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onInit(int initStatus){
        this.tts.setLanguage(Locale.US);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1){
    }

    public void onSensorChanged(SensorEvent event){
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax*ax + ay*ay + az*az);
        if (a > 10) {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }
    public void buttonClicked(View v) {
       try {
            MPro mp = new MPro();
            mp.setPrinciple(((EditText) findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText) findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText) findViewById(R.id.iBox)).getText().toString());
            String s = "Monthly Payment = " + mp.computePayment("%,.2f");
            String n = "n";
            String balance = "Balance";
            s += "\n\n";
            s += "By making this payments monthly for 20 years, the mortgage will be paid in full. But if you terminate the mortgage on its nth anniversary, the balance still owing depends on n as shown below:";
            s += "\n\n";
            s += String.format("%8s", n) + String.format("%16s", balance);

            int i = 0;
            while (i <= 5) {
                s += "\n\n";
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                i++;
            }

            int j = 10;
            while (j <= 20) {
                s += "\n\n";
                s += String.format("%8d", j) + mp.outstandingAfter(j, "%,16.0f");
                j += 5;
            }

            ((TextView) findViewById(R.id.output)).setText(s);
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
            catch(Exception e){
                Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                label.show();
            }
    }


}

