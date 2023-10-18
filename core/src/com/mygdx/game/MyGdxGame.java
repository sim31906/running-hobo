package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {
	Texture img;
	private Texture dropImage;
	private Texture bucketImage;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Bucket bucket;
	private Bucket bucket2;

	private Array<Rectangle> raindrops;
	private long lastDropTime;


	private long diff = 750000000;

	private int player1HP = 3; // Initialize player 1 HP to 3
	private int player2HP = 3; // Initialize player 2 HP to 3
	private boolean gameOver = false;
	private String winner = "";

	private Texture hpFullTexture;
	private Texture hpEmptyTexture;
	private float hpBarWidth = 100; // Adjust as needed
	private float hpBarHeight = 20; // Adjust as needed
	private float hpBarX1 = 20; // Player 1 HP bar position
	private float hpBarX2 = 680; // Player 2 HP bar position
	private float hpBarY = 450; // HP bar height from the bottom of the screen

	private BitmapFont font;
	private GlyphLayout layout;

	private Texture backgroundTexture;
	private Array<HealItem> healItems;
	private Array<SpeedItem> speedItems;
	private float baseRaindropSpeed = 100; // Initial raindrop speed
	private float maxRaindropSpeed = 700; // Maximum raindrop speed
	private float raindropSpeedIncreaseRate = 20; // Rate of speed increase per second
	private long startTime;
	public class HealItem {
		private Texture texture;
		private Rectangle rectangle;

		public HealItem(float x, float y) {
			this.texture = new Texture("berger.png"); // Replace with the actual texture for the heal item
			this.rectangle = new Rectangle(x, y, 32, 32); // Adjust the size as needed
		}

		public Texture getTexture() {
			return texture;
		}

		public Rectangle getRectangle() {
			return rectangle;
		}
	}

	public class SpeedItem {
		private Texture texture;
		private Rectangle rectangle;

		public SpeedItem(float x, float y) {
			this.texture = new Texture("speed.png"); // Replace with the actual texture for the speed item
			this.rectangle = new Rectangle(x, y, 32, 32); // Adjust the size as needed
		}


		public Texture getTexture() {
			return texture;
		}

		public Rectangle getRectangle() {
			return rectangle;
		}
	}

	private Texture speedItemStatusTexture;

	private boolean gameStarted = false;




	private void drawBackground() {
		// Set the background color or texture
		// In this example, we'll use a solid color as the background
		// You can replace this with your preferred background image or texture
		batch.setColor(Color.SKY);

		// Draw the background
		batch.draw(backgroundTexture, 0, 0, 800, 480);

		// Reset the batch color to its default value
		batch.setColor(Color.WHITE);
	}



	@Override
	public void create () {
		batch = new SpriteBatch();

		bucket = new Bucket(32, 20, 80, 122, "playerred.png");
		bucket2 = new Bucket(698, 20, 80, 122, "playerblue.png");

		hpFullTexture = new Texture(Gdx.files.internal("FULL HP.png"));
		hpEmptyTexture = new Texture(Gdx.files.internal("HITHP.png"));

		dropImage = new Texture(Gdx.files.internal("image-removebg-preview.png"));

		backgroundTexture = new Texture(Gdx.files.internal("BG.jpg"));

		speedItemStatusTexture = new Texture(Gdx.files.internal("light.png")); // Replace with your actual image file

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		raindrops = new Array<Rectangle>();
		spawnRaindrop();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		layout = new GlyphLayout();

		healItems = new Array<HealItem>();
		speedItems = new Array<SpeedItem>();

		startTime = TimeUtils.nanoTime();



	}


	@Override
	public void render () {
		if (!gameStarted) {
			// Show the "Press any key to start" screen
			if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
				gameStarted = true;
				startTime = TimeUtils.nanoTime();
			} else {
				// Render the start screen
				renderStartScreen();
				return;
			}
		}

		float deltaTime = Gdx.graphics.getDeltaTime();
		float elapsedTime = (TimeUtils.nanoTime() - startTime) / 1000000000f;
		float raindropSpeed = baseRaindropSpeed + (raindropSpeedIncreaseRate * elapsedTime);
		raindropSpeed = Math.min(raindropSpeed, maxRaindropSpeed);


		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= raindropSpeed * deltaTime;
			raindrop.x += MathUtils.random(-100, 100) * Gdx.graphics.getDeltaTime();

			boolean player1Hit = raindrop.overlaps(bucket.getRectangle());
			boolean player2Hit = raindrop.overlaps(bucket2.getRectangle());

			if (player1Hit && player2Hit) {
				player1HP--;
				player2HP--;
				System.out.println("Both players got hit! Player 1 HP: " + player1HP + " Player 2 HP: " + player2HP);
				iter.remove();
			} else if (player1Hit) {
				player1HP--;
				System.out.println("Player 1 got hit! Player 1 HP: " + player1HP + " Player 2 HP: " + player2HP);
				iter.remove();
			} else if (player2Hit) {
				player2HP--;
				System.out.println("Player 2 got hit! Player 1 HP: " + player1HP + " Player 2 HP: " + player2HP);
				iter.remove();
			}
		}

		for (Iterator<HealItem> iter = healItems.iterator(); iter.hasNext(); ) {
			HealItem healItem = iter.next();
			healItem.getRectangle().y -= 500 * Gdx.graphics.getDeltaTime();

			if (healItem.getRectangle().overlaps(bucket.getRectangle()) && player1HP < 3) {
				player1HP++;
				System.out.println("Player 1 got heal! Player 1 HP: " + player1HP + " Player 2 HP: " + player2HP);
				iter.remove();
			}
			if (healItem.getRectangle().overlaps(bucket2.getRectangle()) && player2HP < 3) {
				player2HP++;
				System.out.println("Player 2 got heal! Player 1 HP: " + player1HP + " Player 2 HP: " + player2HP);
				iter.remove();
			}
		}



		bucket.update(deltaTime);
		bucket2.update(deltaTime);

		for (Iterator<SpeedItem> iter = speedItems.iterator(); iter.hasNext(); ) {
			SpeedItem speedItem = iter.next();
			speedItem.getRectangle().y -= 500 * Gdx.graphics.getDeltaTime();

			if (speedItem.getRectangle().overlaps(bucket.getRectangle())) {
				// Apply the speed item effect to player 1
				// For example, increase the player's movement speed
				bucket.increaseSpeed();
				iter.remove();
			}
			if (speedItem.getRectangle().overlaps(bucket2.getRectangle())) {
				// Apply the speed item effect to player 2
				// For example, increase the player's movement speed
				bucket2.increaseSpeed();
				iter.remove();
			}
		}




		// Check for game over conditions
		if (player1HP <= -1 || player2HP <= -1) {
			gameOver = true;
			if (player1HP <= -1 && player2HP <= -1) {
				winner = "It's a tie!";
			} else if (player1HP <= 0) {
				winner = "blue wins!";
			} else {
				winner = "red wins!";
			}
		}



		if (gameOver) {
			// Handle game over logic here
			font.getData().setScale(2); // Set the font scale as needed
			String message = "Game Over\n" + winner;
			layout.setText(font, message);
			float messageX = (800 - layout.width) / 2;
			float messageY = (480 + layout.height) / 2;
			batch.begin();
			font.draw(batch, layout, messageX, messageY);
			batch.end();
			return; // Skip the rest of the rendering if the game is over
		}



		if (Gdx.input.isKeyPressed(Input.Keys.A))
			bucket.getRectangle().x -= bucket.getMoveSpeed() * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			bucket.getRectangle().x += bucket.getMoveSpeed() * Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			bucket2.getRectangle().x -= bucket2.getMoveSpeed() * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			bucket2.getRectangle().x += bucket2.getMoveSpeed() * Gdx.graphics.getDeltaTime();


		if(bucket.getRectangle().x < 0)
			bucket.getRectangle().x = 0;
		if(bucket.getRectangle().x > 800 - 64)
			bucket.getRectangle().x = 800 - 64;
		if(bucket.getRectangle().y < 0)
			bucket.getRectangle().y = 0;
		if(bucket.getRectangle().y > 480 - 64)
			bucket.getRectangle().y = 480 -64;

		if(bucket2.getRectangle().x < 0)
			bucket2.getRectangle().x = 0;
		if(bucket2.getRectangle().x > 800 - 64)
			bucket2.getRectangle().x = 800 - 64;
		if(bucket2.getRectangle().y < 0)
			bucket2.getRectangle().y = 0;
		if(bucket2.getRectangle().y > 480 - 64)
			bucket2.getRectangle().y = 480 - 64;



		if(TimeUtils.nanoTime() - lastDropTime > diff)
			spawnRaindrop();




		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		drawBackground();
		for (HealItem healItem : healItems) {
			batch.draw(healItem.getTexture(), healItem.getRectangle().x, healItem.getRectangle().y);
		}
		for (SpeedItem speedItem : speedItems){
			batch.draw(speedItem.getTexture() , speedItem.getRectangle().x,speedItem.getRectangle().y);
		}
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.draw(bucket2.getTexture(), bucket2.getRectangle().x, bucket2.getRectangle().y);
		batch.draw(bucket.getTexture(), bucket.getRectangle().x, bucket.getRectangle().y);
		drawHPBar(hpBarX1, hpBarY, player1HP, batch);
		drawHPBar(hpBarX1, hpBarY, player1HP, batch);
		if (bucket.hasSpeedItem) {
			float statusPictureX = hpBarX1 + hpBarWidth + 10; // Adjust the position as needed
			float statusPictureY = hpBarY - 1 ; // Adjust the position as needed
			batch.draw(speedItemStatusTexture, statusPictureX, statusPictureY);
		}
		drawHPBar(hpBarX2, hpBarY, player2HP, batch);
		if (bucket2.hasSpeedItem) {
			float statusPictureX = hpBarX2 + hpBarWidth - 130; // Adjust the position as needed
			float statusPictureY = hpBarY - 2; // Adjust the position as needed
			batch.draw(speedItemStatusTexture, statusPictureX, statusPictureY);
		}
		batch.end();
	}

	private void renderStartScreen() {
		// Clear the screen
		ScreenUtils.clear(Color.BLACK);

		// Set up the camera and batch
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		// Render the "Press any key to start" message
		font.getData().setScale(2); // Set the font scale as needed
		String message = "Press any key to start";
		layout.setText(font, message);
		float messageX = (800 - layout.width) / 2;
		float messageY = (480 + layout.height) / 2;
		font.draw(batch, layout, messageX, messageY);
		batch.end();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
		if (MathUtils.random(0, 100) < 30) { // Adjust the probability as needed
			HealItem healItem = new HealItem(MathUtils.random(0, 800 - 32), 480);
			healItems.add(healItem);
		}
		if (MathUtils.random(0,100) < 25 ){
			SpeedItem speedItem = new SpeedItem(MathUtils.random(0, 800 - 32), 480);
			speedItems.add(speedItem);
		}
	}


	private void drawHPBar(float x, float y, int hp, SpriteBatch batch) {
		float widthPerHP = hpBarWidth / 3; // Assuming each player has 3 HP
		for (int i = 0; i < 3; i++) {
			Texture texture = (i < hp) ? hpFullTexture : hpEmptyTexture;
			batch.draw(texture, x + i * widthPerHP, y, widthPerHP, hpBarHeight);
		}
	}

	@Override
	public void dispose () {
//		img.dispose();
		dropImage.dispose();
		bucket.getTexture().dispose();
		batch.dispose();
		hpFullTexture.dispose();
		hpEmptyTexture.dispose();
		backgroundTexture.dispose();
		for (SpeedItem speedItem : speedItems) {
			speedItem.getTexture().dispose();
		}
	}
}
