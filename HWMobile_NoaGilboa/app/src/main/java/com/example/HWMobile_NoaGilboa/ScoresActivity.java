package com.example.HWMobile_NoaGilboa;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScoresActivity extends AppCompatActivity implements ScoreAdapter.ScoreClickListener {

    private MapsFragment mapsFragment;
    private ScoresFragment scoresFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        mapsFragment = new MapsFragment();
        scoresFragment = new ScoresFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapContainer, mapsFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.scoresFragment, scoresFragment)
                .commit();
    }


    @Override
    public void onScoreClicked(Score score) {
        mapsFragment.zoom(score);
    }
}
