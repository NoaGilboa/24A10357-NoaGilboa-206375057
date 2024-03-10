package com.example.HWMobile_NoaGilboa;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity

        implements OnMain, SensorEventListener,
        Game.IGame {
    private Game game;
    public static final String MODE_ARG = "mode";
    public static final String NAME_ARG = "name";

    public static final String MODE_NORMAL = "mode";
    public static final String MODE_SENSOR_NORMAL = "mode_sensor_normal";

    public static final String MODE_HARD = "mode_hard";
    public static final String MODE_SENSOR_HARD = "mode_sensor_hard";

    private Sensor accelerometer;

    @Override
    public void runOnMainThread(Runnable r) {
        runOnUiThread(r);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            game.simpleLocation.beginUpdates();
        }
    }

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = new Game(this);

        FloatingActionButton moveLeftBtn = findViewById(R.id.moveLeftBtn);
        FloatingActionButton moveRightBtn = findViewById(R.id.moveRightBtn);

        moveLeftBtn.setOnClickListener((v) -> {
            game.movePlayerLeft();
        });

        moveRightBtn.setOnClickListener((v) -> {
            game.movePlayerRight();
        });


        SensorManager manager = getSystemService(SensorManager.class);
        String mode = getIntent().getStringExtra(MODE_ARG);
        name = getIntent().getStringExtra(NAME_ARG);


        if (MODE_SENSOR_HARD.equals(mode)
                || MODE_SENSOR_NORMAL.equals(mode)) {
            accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        if (MODE_SENSOR_HARD.equals(mode)
                || MODE_HARD.equals(mode)) {
            game.setSpawnSpeed(10.0f);
        }
    }


    long prevUpdateTime = 0;
    float xLast = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float xNew = sensorEvent.values[0];
        long now = System.currentTimeMillis();
        if (Math.abs(xNew - xLast) > 0.5f) {
            if (now - prevUpdateTime > 400) { // 200 ms break
                prevUpdateTime = now;
                if (xNew - xLast > 0) { // right??
                    game.movePlayerRight();
                } else { // Left ??
                    game.movePlayerLeft();
                }
                xLast = xNew;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.setGameOver(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        game.setGameOver(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.startGame();
    }

    @Override
    public void gameOver() {
        LatLng location = game.getLocation();
        int score = game.getScore();
        Score scoreObject = new Score();
        scoreObject.setName(name);
        scoreObject.setLat(location.latitude);
        scoreObject.setLon(location.longitude);
        scoreObject.setDate(System.currentTimeMillis());
        scoreObject.setScore(score);
        if (MSPV3.getMe(this).addScore(scoreObject)) {
            Toast.makeText(this, "You have New best score! " + score, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}