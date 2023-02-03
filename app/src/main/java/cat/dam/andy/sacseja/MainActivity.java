package cat.dam.andy.sacseja;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    // Members
    private final int IDLE_TIME = 200; //temps mínim entre sacsejades
    private SensorManager sensorManager; //gestor de sensors
    private boolean colorFlag = false; //per canviar color
    private long lastUpdate; //per control de temps mínim entre sacsejades
    private TextView tv_main; //per instruccions i canviar color de fons

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initSensor();
    }

    private void initViews() {
        tv_main = findViewById(R.id.tv_main);
        tv_main.setBackgroundColor(Color.RED);
    }

    private void initSensor() {
        //obtenim el gestor de sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //obtenim l'hora actual en milisegons per controlar un temps mínim entre sacsejades
        lastUpdate = System.currentTimeMillis();
    }


    //sobreescriu els mètodes del SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    //Si hi ha algun canvi en els sensors registrats, es cridarà aquest mètode
    private void getAccelerometer(SensorEvent event) {
        //obtenim els valors del sensor
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        //Per testejar valors
        //Toast.makeText(getApplicationContext(),String.valueOf(accelationSquareRoot)+" "+ SensorManager.GRAVITY_EARTH,Toast.LENGTH_SHORT).show();
        if (accelationSquareRoot >= 2) //s'esecutarà si es sacseja
        {
            //Si fa poc temps dels darrer canvi no fem res
            if (actualTime - lastUpdate < IDLE_TIME) {
                return;
            }
            //quan ha passat el mínim temps entre sacsejades
            lastUpdate = actualTime;// actualitzem el temps
            //canviem el color de fons
            changeBackgroundColor(colorFlag);
            colorFlag = !colorFlag;
        }
    }

    private void changeBackgroundColor(Boolean isColor) {
        if (isColor) {
            tv_main.setBackgroundColor(Color.GREEN);
        } else {
            tv_main.setBackgroundColor(Color.RED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registra aquesta classe com a listener per l'orientació i
        // els sensors d'accelereració
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // desregistra el listener quan passa a segon pla
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}