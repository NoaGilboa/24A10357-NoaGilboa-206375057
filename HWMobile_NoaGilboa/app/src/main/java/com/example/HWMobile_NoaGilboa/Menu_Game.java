package com.example.HWMobile_NoaGilboa;


import static com.example.HWMobile_NoaGilboa.MainActivity.MODE_HARD;
import static com.example.HWMobile_NoaGilboa.MainActivity.MODE_NORMAL;
import static com.example.HWMobile_NoaGilboa.MainActivity.MODE_SENSOR_HARD;
import static com.example.HWMobile_NoaGilboa.MainActivity.MODE_SENSOR_NORMAL;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;

public class Menu_Game extends AppCompatActivity {
    private MaterialButton menu_BTN_button;
    private MaterialButton menu_BTN_sensor;

    private MaterialButton menu_BTN_button_hard;
    private MaterialButton menu_BTN_sensor_hard;
    private MaterialButton menu_BTN_highScores;
    private AppCompatImageView menu_IMG_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_game);
        GameUtils.hideSystemUI(this);
        findView();

        clicked();
    }

    private void clicked() {
        menu_BTN_button.setOnClickListener(view -> moveToGame(MODE_NORMAL));
        menu_BTN_sensor.setOnClickListener(view -> moveToGame(MODE_SENSOR_NORMAL));
        menu_BTN_button_hard.setOnClickListener(view -> moveToGame(MODE_HARD));
        menu_BTN_sensor_hard.setOnClickListener(view -> moveToGame(MODE_SENSOR_HARD));
        menu_BTN_highScores.setOnClickListener(view -> moveToScores());
    }

    private void findView() {
        menu_BTN_button = findViewById(R.id.menu_BTN_button);
        menu_BTN_sensor = findViewById(R.id.menu_BTN_sensor);
        menu_BTN_button_hard = findViewById(R.id.menu_BTN_button_hard);
        menu_BTN_sensor_hard = findViewById(R.id.menu_BTN_sensor_hard);
        menu_BTN_highScores = findViewById(R.id.menu_BTN_highScores);
        menu_IMG_background = findViewById(R.id.menu_IMG_background);

    }

    public void moveToGame(String mode) {

        View v = getLayoutInflater().inflate(R.layout.name_layout, null, false);
        EditText nameEt = v.findViewById(R.id.nameEt);

        new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Invaders")
                .setPositiveButton("Start", (dialogInterface, i) -> {
                    String name = nameEt.getText().toString();
                    if (name.isEmpty()) {
                        Toast.makeText(Menu_Game.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent gameIntent = new Intent(this, MainActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString(MainActivity.NAME_ARG, name);
                    bundle.putString(MainActivity.MODE_ARG, mode);

                    gameIntent.putExtras(bundle);
                    startActivity(gameIntent);

                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    public void moveToScores() {
        Intent ScoreIntent = new Intent(this, ScoresActivity.class);
        startActivity(ScoreIntent);
    }
}
