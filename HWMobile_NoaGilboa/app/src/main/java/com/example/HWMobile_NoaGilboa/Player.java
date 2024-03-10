package com.example.HWMobile_NoaGilboa;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;

public class Player extends GameObject {

    public static final int MAX_LIFE = 3;
    private int life;
    private int lane;

    private int score;



    public Player(Context context) {
        super(new ImageView(context));
        this.life = MAX_LIFE;
        lane = Game.COLUMNS / 2;
        getView().setImageResource(R.drawable.player);
    }

    public void damage() {
        life--;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }


    public boolean moveLeft() {
        if (lane - 1 < 0) return false;
        lane--;
        return true;
    }

    public boolean moveRight() {
        if (lane + 1 >= Game.COLUMNS) return false;
        lane++;
        return true;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }
    public void incrementScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public boolean isDead() {
        return life <= 0;
    }
}
