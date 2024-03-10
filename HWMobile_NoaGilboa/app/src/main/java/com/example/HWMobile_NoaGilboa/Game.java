package com.example.HWMobile_NoaGilboa;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

import im.delight.android.location.SimpleLocation;

public class Game {


    public interface IGame {
        void gameOver();
    }

    public static final int COLUMNS = 5;
    public static final int ROWS = 12;

    private float SPAWN_SPEED = 5;
    private final GridLayout gameLayout;
    private final LinearLayout lifeLayout;
    private final TextView scoresTv;
    private final Player player;
    private final int[] obstacle_lanes;
    private final int[] active_lanes;
    private final int[] gift_lanes;
    private final int[] active_gift_lane;
    private final Random r = new Random();
    private final Handler handler;
    private GameObject[][] matrix;

    boolean isGameOver = false;

    public SimpleLocation simpleLocation;

    private IGame iGame;

    public Game(MainActivity activity) {
        simpleLocation = new SimpleLocation(activity);
        requestLocationPermissions(activity);
        iGame = activity;
        gameLayout = activity.findViewById(R.id.game_layout);
        lifeLayout = activity.findViewById(R.id.life_layout);
        scoresTv = activity.findViewById(R.id.scoreTv);
        scoresTv.setText("Score: " + 0);
        obstacle_lanes = new int[COLUMNS];
        gift_lanes = new int[COLUMNS];
        active_lanes = new int[COLUMNS];
        active_gift_lane = new int[COLUMNS];
        gameLayout.setColumnCount(COLUMNS);
        gameLayout.setRowCount(ROWS);
        handler = new Handler();
        matrix = new GameObject[ROWS][COLUMNS];
        for (int col = 0; col < COLUMNS; col++) {
            set(0, col, new Obstacle(activity));
        }
        for (int col = 0; col < COLUMNS; col++) {
            gift_lanes[col] = 1;
        }
        player = new Player(activity);
        set(ROWS - 1, COLUMNS / 2, player);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (matrix[row][col] == null) {
                    set(row, col, new DummyObject(activity));
                }
                gameLayout.addView(matrix[row][col].getView());
            }
        }
    }


    private void resetObstacle(int lane, int row) {
        // reset obstacle
        obstacle_lanes[lane] = 0;
        swap(row, lane, 0, lane);
    }

    private void resetGift(int lane, int row) {
        // reset obstacle
        gift_lanes[lane] = 1;
        swap(row, lane, 1, lane);
    }

    private boolean moveObstacle(int lane) {
        int current_row = obstacle_lanes[lane];
        int new_row = current_row + 1;
        if (new_row >= ROWS) {
            // reset obstacle
            resetObstacle(lane, current_row);
            return false;
        } else {
            obstacle_lanes[lane]++;
            if (matrix[new_row][lane] instanceof Player) {
                // reset obstacle
                if (player.getLife() == 1)
                    Toast.makeText(gameLayout.getContext(), "You died. reviving", Toast.LENGTH_SHORT).show();
                player.damage();
                drawLives();
                if (player.isDead()) {
                    gameOver();
                }
                resetObstacle(lane, current_row);
                return false;
            } else if (matrix[new_row][lane] instanceof Gift) {
                resetObstacle(lane, current_row);
                return false;
            }
            checkObstacleInvokedCollision(lane, new_row);
            swap(current_row, lane, new_row, lane);
            return true;
        }
    }

    private void requestLocationPermissions(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 101);
        } else {
            simpleLocation.beginUpdates();
        }
    }

    private void gameOver() {
        isGameOver = true;
        iGame.gameOver();
    }

    private boolean moveGift(int lane) {
        int current_row = gift_lanes[lane];
        int new_row = current_row + 1;

        if (new_row >= ROWS || matrix[new_row][lane] instanceof Obstacle) {
            // reset obstacle
            if (new_row < ROWS)
                resetObstacle(lane, new_row);
            else
                resetGift(lane, current_row);
            return true;
        } else if (matrix[new_row][lane] instanceof Gift) {
            return true;
        } else {
            gift_lanes[lane]++;
            if (matrix[new_row][lane] instanceof Player) {
                // reset obstacle
                Toast.makeText(gameLayout.getContext(), "You got a score!", Toast.LENGTH_SHORT).show();
                player.incrementScore();
                scoresTv.setText("Score: " + player.getScore());
                resetGift(lane, current_row);
                return false;
            }
            checkGiftInvokedCollision(lane, new_row);
            swap(current_row, lane, new_row, lane);
            return true;
        }
    }

    public void movePlayerLeft() {
        int prevLane = player.getLane();
        if (player.moveLeft()) {
            checkPlayerInvokedCollision();
            swap(ROWS - 1, prevLane, ROWS - 1, player.getLane());
        }
    }

    public LatLng getLocation() {
        return new LatLng(simpleLocation.getLatitude(), simpleLocation.getLongitude());
    }

    private void checkPlayerInvokedCollision() {
        int lane = player.getLane();
        if (matrix[ROWS - 1][lane] instanceof Obstacle) {
            player.damage();
            drawLives();
            if (player.isDead()) {
                gameOver();
            }
            Toast.makeText(gameLayout.getContext(), "Direct hit!", Toast.LENGTH_SHORT).show();
            resetObstacle(lane, obstacle_lanes[lane]);
        } else if (matrix[ROWS - 1][lane] instanceof Gift) {
            // reset obstacle
            Toast.makeText(gameLayout.getContext(), "You got a score!", Toast.LENGTH_SHORT).show();
            player.incrementScore();
            resetGift(lane, gift_lanes[lane]);
        }
    }

    private void checkObstacleInvokedCollision(int lane, int newRow) {

        if (matrix[newRow][lane] instanceof Player) {
            // reset obstacle
            if (player.getLife() == 1)
                Toast.makeText(gameLayout.getContext(), "You died. reviving", Toast.LENGTH_SHORT).show();
            player.damage();
            drawLives();
            resetObstacle(lane, newRow - 1);
        }

    }

    private void checkGiftInvokedCollision(int lane, int newRow) {
        if (matrix[newRow][lane] instanceof Player) {
            // reset obstacle
            Toast.makeText(gameLayout.getContext(), "You got a score!", Toast.LENGTH_SHORT).show();
            player.incrementScore();
            resetGift(lane, newRow - 1);
        }
    }

    public void movePlayerRight() {
        int prevLane = player.getLane();
        if (player.moveRight()) {
            checkPlayerInvokedCollision();
            swap(ROWS - 1, prevLane, ROWS - 1, player.getLane());
        }
    }

    private void set(int row, int col, GameObject object) {
        matrix[row][col] = object;
        object.setPos(row, col);
    }

    private void swap(int r1, int c1, int r2, int c2) {
        GameObject object = matrix[r1][c1];
        set(r1, c1, matrix[r2][c2]);
        set(r2, c2, object);
    }

    public void drawLives() {
        for (int i = 0; i < 3; i++) {
            lifeLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < player.getLife(); i++) {
            lifeLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    class ObstacleMovement implements Runnable {
        private final int lane;

        public ObstacleMovement(int lane) {
            this.lane = lane;
        }

        @Override
        public void run() {
            if (isGameOver) return;
            if (!moveObstacle(lane)) {
                active_lanes[lane] = 0;
            } else {
                handler.postDelayed(new ObstacleMovement(lane), (long)(5000 / SPAWN_SPEED));
            }
        }
    }

    class GiftMovement implements Runnable {
        private final int lane;
        private GameObject gift;

        public GiftMovement(int lane, GameObject gift) {
            this.lane = lane;
            this.gift = gift;
        }


        @Override
        public void run() {
            if (isGameOver) return;
            if (!moveGift(lane)) {
                active_gift_lane[lane] = 0;
                gift_lanes[lane] = 1;
                gameLayout.removeView(gift.getView());
            } else {
                handler.postDelayed(new GiftMovement(lane, gift),  (long)(5000 / SPAWN_SPEED));
            }
        }
    }

    class ObstacleTask implements Runnable {

        @Override
        public void run() {
            if (isGameOver) return;
            int random_lane = r.nextInt(COLUMNS);
            handler.postDelayed(new ObstacleTask(), (long)(5000 / SPAWN_SPEED));
            if (active_lanes[random_lane] != 0) {
                return;
            }
            active_lanes[random_lane] = 1;
            handler.postDelayed(new ObstacleMovement(random_lane), (long)(5000 / SPAWN_SPEED));
        }
    }

    class GiftTask implements Runnable {

        @Override
        public void run() {
            if (isGameOver) return;
            int random_lane = r.nextInt(COLUMNS);
            handler.postDelayed(new GiftTask(), (long)(15000 / SPAWN_SPEED));
            if (active_gift_lane[random_lane] != 0) {
                return;
            }
            if (matrix[1][random_lane] instanceof Obstacle) {
                return;
            }
            GameObject gift = new Gift(gameLayout.getContext());
            gameLayout.addView(gift.getView());
            set(1, random_lane, gift);
            active_gift_lane[random_lane] = 1;
            handler.postDelayed(new GiftMovement(random_lane, gift), (long)(15000 / SPAWN_SPEED));
        }
    }


    public int getScore() {
        return player.getScore();
    }

    public void setGameOver(boolean b) {
        this.isGameOver = b;
    }

    public void startGame() {
        this.isGameOver = false;
        handler.postDelayed(new GiftTask(), (long)(15000 / SPAWN_SPEED));
        handler.postDelayed(new ObstacleTask(), (long)(5000 / SPAWN_SPEED));
    }
    public void setSpawnSpeed(float spawnSpeed) {
        this.SPAWN_SPEED = spawnSpeed;
    }
}

