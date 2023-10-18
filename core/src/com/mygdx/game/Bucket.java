package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Bucket {
    private final Rectangle rectangle;         // A rectangle representing the bucket's position and size
    private final Texture texture;             // The texture (image) used for the bucket
    private float moveSpeed;                   // Player's current movement speed
    private float speedBoostDuration;           // Duration of the speed boost in seconds
    private float speedBoostTimer;              // Timer to track the remaining duration of speed boost
    private boolean isSpeedBoostActive;        // Indicates if speed boost is currently active
    boolean hasSpeedItem;                      // Indicates if the bucket has a speed-boost item

    // Constructor for the Bucket class
    public Bucket(int x, int y, int width, int height, String path) {
        this.rectangle = new Rectangle();
        this.rectangle.x = x;
        this.rectangle.y = y;
        this.rectangle.width = width;
        this.rectangle.height = height;
        this.texture = new Texture(Gdx.files.internal(path));

        moveSpeed = 500;                        // Initialize the default movement speed (adjust as needed)
        speedBoostDuration = 3.0f;              // Set the duration of the speed boost to 3 seconds
        isSpeedBoostActive = false;             // Speed boost is initially inactive
    }

    // Update method to handle speed boost and timer
    public void update(float deltaTime) {
        // Update the speed boost timer
        if (isSpeedBoostActive) {
            speedBoostTimer -= deltaTime;
            if (speedBoostTimer <= 0) {
                // Speed boost has expired, reset the speed to default
                moveSpeed = 500; // Reset to the default speed
                isSpeedBoostActive = false;

                // Reset the hasSpeedItem variable
                resetSpeedItem();
            }
        }
    }

    // Method to increase the player's movement speed
    public void increaseSpeed() {
        // Increase the player's movement speed here
        // For example:
        moveSpeed += 300; // Adjust the speed increase value as needed

        // Activate the speed boost and set the timer
        isSpeedBoostActive = true;
        speedBoostTimer = speedBoostDuration;

        hasSpeedItem = true; // Set the hasSpeedItem variable to true
    }

    // Method to reset the hasSpeedItem variable
    public void resetSpeedItem() {
        hasSpeedItem = false;
    }

    // Getter method for retrieving the current movement speed
    public float getMoveSpeed() {
        return moveSpeed;
    }

    // Getter method for retrieving the rectangle representing the bucket
    public Rectangle getRectangle() {
        return this.rectangle;
    }

    // Getter method for retrieving the texture of the bucket
    public Texture getTexture() {
        return this.texture;
    }
}
