# Android game

## Overview

This project is an Android game that utilizes sensors and GPS location. It features multiple game modes, real-time score tracking, and high-score management with location-based data. The game supports both button controls and accelerometer-based controls for movement.

## Features

- **Multiple Game Modes**: Normal and hard difficulty, with or without sensor-based controls.
- **Player Movement**: Control the player using buttons or the device's accelerometer.
- **Real-Time Location**: Track the player's location using GPS.
- **High Scores**: Maintain a list of high scores with corresponding locations.
- **Interactive Map**: View high scores on a map with zoom functionality.

## Project Structure

- **MainActivity**: Handles the main game logic and sensor events.
- **Game**: Manages the game state, player movement, obstacles, and gifts.
- **Menu_Game**: Main menu activity for selecting game modes and viewing high scores.
- **ScoresActivity**: Activity for displaying high scores and integrating with the map.
- **MapsFragment**: Fragment for displaying high scores on a map.
- **MyDB**: Manages the list of high scores.
- **Layouts**: XML files for defining the user interface.

## Installation

1. Clone the repository:

```bash
git clone https://github.com/NoaGilboa/24A10357-NoaGilboa-206375057.git
```

2. Open the project in Android Studio.

3. Build and run the project on an Android device or emulator.

## Usage

### Main Menu

1. **Select Game Mode**: Choose between normal, hard, and sensor-based modes.
2. **Enter Name**: Provide a player name to track scores.
3. **Start Game**: Begin the game with the selected mode.

### In-Game

- **Button Controls**: Use the on-screen buttons to move the player left or right.
- **Sensor Controls**: Tilt the device to move the player if using a sensor-based mode.
- **Score**: Collect gifts to increase your score. Avoid obstacles to prevent losing lives.
- **Game Over**: When the game ends, your score and location are saved. If it's a new high score, it will be displayed on the map.

### High Scores

1. **View Scores**: Navigate to the high scores screen from the main menu.
2. **Map Integration**: High scores are displayed on a map with markers indicating the location where the score was achieved.

## Code Snippets

### MainActivity

```java
public class MainActivity extends AppCompatActivity implements OnMain, SensorEventListener, Game.IGame {
    private Game game;
    // ... Other variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        game = new Game(this);

        FloatingActionButton moveLeftBtn = findViewById(R.id.moveLeftBtn);
        FloatingActionButton moveRightBtn = findViewById(R.id.moveRightBtn);
        
        moveLeftBtn.setOnClickListener((v) -> game.movePlayerLeft());
        moveRightBtn.setOnClickListener((v) -> game.movePlayerRight());

        SensorManager manager = getSystemService(SensorManager.class);
        String mode = getIntent().getStringExtra(MODE_ARG);

        if (MODE_SENSOR_HARD.equals(mode) || MODE_SENSOR_NORMAL.equals(mode)) {
            accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        if (MODE_SENSOR_HARD.equals(mode) || MODE_HARD.equals(mode)) {
            game.setSpawnSpeed(10.0f);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float xNew = sensorEvent.values[0];
        long now = System.currentTimeMillis();
        if (Math.abs(xNew - xLast) > 0.5f && now - prevUpdateTime > 400) {
            prevUpdateTime = now;
            if (xNew - xLast > 0) game.movePlayerRight();
            else game.movePlayerLeft();
            xLast = xNew;
        }
    }

    // Other overridden methods...
}
```

### Game

```java
public class Game {
    // Game-related variables and constants

    public Game(MainActivity activity) {
        simpleLocation = new SimpleLocation(activity);
        requestLocationPermissions(activity);
        iGame = activity;
        // Initialize game layout, player, obstacles, etc.
    }

    public void movePlayerLeft() {
        int prevLane = player.getLane();
        if (player.moveLeft()) {
            checkPlayerInvokedCollision();
            swap(ROWS - 1, prevLane, ROWS - 1, player.getLane());
        }
    }

    public void movePlayerRight() {
        int prevLane = player.getLane();
        if (player.moveRight()) {
            checkPlayerInvokedCollision();
            swap(ROWS - 1, prevLane, ROWS - 1, player.getLane());
        }
    }

    private void checkPlayerInvokedCollision() {
        int lane = player.getLane();
        if (matrix[ROWS - 1][lane] instanceof Obstacle) {
            player.damage();
            drawLives();
            if (player.isDead()) gameOver();
            resetObstacle(lane, obstacle_lanes[lane]);
        } else if (matrix[ROWS - 1][lane] instanceof Gift) {
            player.incrementScore();
            resetGift(lane, gift_lanes[lane]);
        }
    }

    // Other game-related methods...
}
```

