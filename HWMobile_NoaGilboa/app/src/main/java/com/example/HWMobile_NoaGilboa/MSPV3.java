package com.example.HWMobile_NoaGilboa;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MSPV3 {

    private final String SP_FILE = "SP_FILE";


    private Gson g = new Gson();

    private static MSPV3 me;
    private SharedPreferences sharedPreferences;

    public static MSPV3 getMe(Context context) {
        if(me == null)
            initHelper(context);
        return me;
    }

    private MSPV3(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
    }

    public List<Score> getScores() {
        Set<String> score_strings = sharedPreferences.getStringSet("scores", new HashSet<>());

        return score_strings
                .stream()
                .map(s -> g.fromJson(s, Score.class))
                .sorted()
                .collect(Collectors.toList());
    }

    public void saveScores(List<Score> scores) {
        HashSet<String> set = new HashSet<>();
        for (Score score : scores) {
            set.add(g.toJson(score));
        }

        sharedPreferences.edit()
                .putStringSet("scores", set)
                .apply();
    }

    public boolean addScore(Score score) {
        List<Score> scores = getScores();
        for (Score s : scores) {
            if (s.getName().equals(score.getName())) {
                if (s.getScore() < score.getScore()) {
                    s.setScore(score.getScore());
                    s.setLat(score.getLat());
                    s.setLon(score.getLon());
                    saveScores(scores);
                    return true;
                } else {
                    return false;
                }
            }
        }
        scores.add(score);
        saveScores(scores);
        return true;
    }

    public static MSPV3 initHelper(Context context) {
        if (me == null) {
            me = new MSPV3(context);
        }
        return me;
    }

    public void putDouble(String KEY, double defValue) {
        putString(KEY, String.valueOf(defValue));
    }

    public double getDouble(String KEY, double defValue) {
        return Double.parseDouble(getString(KEY, String.valueOf(defValue)));
    }

    public int getInt(String KEY, int defValue) {
        return sharedPreferences.getInt(KEY, defValue);
    }

    public void putInt(String KEY, int value) {
        sharedPreferences.edit().putInt(KEY, value).apply();
    }

    public String getString(String KEY, String defValue) {
        return sharedPreferences.getString(KEY, defValue);
    }

    public void putString(String KEY, String value) {
        sharedPreferences.edit().putString(KEY, value).apply();
    }
}
