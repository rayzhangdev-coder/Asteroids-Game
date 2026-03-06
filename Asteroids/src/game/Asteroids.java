package game;

/*
 * CLASS: Asteroids
 * DESCRIPTION: The main game controller for an Asteroids-style arcade game.
 *              Extends the Game class to handle game loop, rendering, input, 
 *              and game state management. Features waves of asteroids, ship 
 *              combat, progressive difficulty, and background music.
 * 
 * @author Ray Zhang and Gitan Mandell
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.File;

/**
 * The main game class for Asteroids that handles game initialization,
 * rendering, user input, and game state management.
 * 
 * <p>Features include:
 * <ul>
 *   <li>Wave-based asteroid spawning with increasing difficulty</li>
 *   <li>Ship movement with rotation and acceleration</li>
 *   <li>Bullet shooting mechanics</li>
 *   <li>Collision detection between ship, asteroids, and bullets</li>
 *   <li>Home screen and game over states</li>
 *   <li>Progressive time scaling for waves</li>
 *   <li>Background music with automatic playback</li>
 *   <li>Shield power-up after wave 5</li>
 * </ul>
 * 
 * @author Ray Zhang
 * @author Gitan Mandell
 */
class Asteroids extends Game {
	/** Flag indicating if left rotation key is currently pressed. */
	private boolean leftPressed = false;
	
	/** Flag indicating if right rotation key is currently pressed. */
	private boolean rightPressed = false;
	
	/** Flag indicating if forward acceleration key was just pressed. */
	private boolean acceleratePressed = false;
	
	/** Flag indicating whether the shield is currently active. */
	private boolean shieldActive = false;
	
	/** The timestamp of the last bullet fired. */
	private long lastShotTime = 0;
	
	/** The shield object that protects the ship from one collision. */
	private Shield shield;
	
	/** Flag indicating if the player clicked during the wave 5 cutscene. */
	private boolean clicked = false;
	
	/** The player's ship instance. */
	private Ship ship;

	/** List of all active asteroids in the game. */
	private List<Asteroid> asteroids;

	/** List of all active bullets fired by the ship. */
	private List<Bullet> bullets;

	/** Object representing the current game state and stats. */
	private GameState gameState;

	/** Clip used for playing background music during gameplay. */
	private Clip backgroundMusic;
	
	/**
	 * Constructs a new Asteroids game instance.
	 * Initializes game state, sets up input listeners, prepares the game world,
	 * and starts background music immediately.
	 */
	public Asteroids() {
		super("Asteroids!", 800, 600);
		
		playBackgroundMusic();

		asteroids = new ArrayList<>();
		bullets = new ArrayList<>();
		gameState = new GameState();
		
		// Handle mouse clicks to start the game from the home screen
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (gameState.isHomeScreen()) {
					gameState.setHomeScreen(false);
					gameState.setWaveStartTime(System.currentTimeMillis());
					ship = new Ship(new Point(400, 300));
					spawnWave();
				}
				//Handle transitioning to wave 6 after wave 5 end screen
				if(gameState.getWave()==6) {
					clicked = true;
				}
			}
		});
		
		// Handle keyboard input for controlling the ship and restarting
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (gameState.isGameOver()) {
					// Press R to restart
					if (e.getKeyCode() == KeyEvent.VK_R) {
						restartGame();
					}
					return;
				}

				if (ship == null) return; // safeguard

				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:  
					case KeyEvent.VK_A:     
						leftPressed = true;
						break;
					case KeyEvent.VK_RIGHT: 
					case KeyEvent.VK_D:     
						rightPressed = true;
						break;
					case KeyEvent.VK_UP:    
					case KeyEvent.VK_W:     
						ship.accelerate(0.6);
						if(shieldActive && shield != null) {
							shield.accelerate(ship.rotation, 0.6);
						}
						break;
					case KeyEvent.VK_SPACE:
						// Calculate tip position of ship for bullet origin
						if(!gameState.showWaveText() && System.currentTimeMillis() - lastShotTime > 100) {
							Point[] shipPoints = ship.getPolygon().getPoints();
							Point tipPosition = shipPoints[0];  // First point is the tip
							
							bullets.add(new Bullet(
								tipPosition.clone(),  // Tip of ship instead of center
								ship.getPolygon().rotation
							));
							lastShotTime = System.currentTimeMillis();
						}
						break;
				}
			}
			
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_A:
						leftPressed = false;
						break;
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_D:
						rightPressed = false;
						break;
				}
			}
		});
	}
	
	/**
	 * Loads and plays background music for the game.
	 * The music loops continuously throughout gameplay.
	 * If the music file cannot be loaded, the game continues without audio.
	 */
	private void playBackgroundMusic() {
		try {
			File file = new File("Song.wav");
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
			backgroundMusic = AudioSystem.getClip();
			backgroundMusic.open(audioStream);
			backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
			backgroundMusic.start();
		} catch (Exception e) {
			System.err.println("Failed to load music: " + e.getMessage());
		}
	}
	
	/**
	 * Stops the background music playback if it is currently running.
	 */
	private void stopBackgroundMusic() {
		if (backgroundMusic != null && backgroundMusic.isRunning()) {
			backgroundMusic.stop();
		}
	}
	
	/**
	 * Inner class representing the current state of the game.
	 * Manages wave progression, scoring, timing, and game state flags.
	 */
	private class GameState {
		/** The current wave number. */
		private int wave = 1;

		/** The player's current score. */
		private int score = 0;

		/** Whether the game is currently over. */
		private boolean gameOver = false;

		/** The timestamp when the current wave started. */
		private long waveStartTime;

		/** Whether the home screen is currently displayed. */
		private boolean homeScreen = true;

		/** The timestamp when the wave text began displaying. */
		private long waveTextDisplayTime = 0;

		/** Whether the wave transition text should be shown. */
		private boolean showWaveText = false;
		
		/**
		 * Resets the game state to initial values for a new game.
		 */
		public void reset() {
			wave = 1;
			score = 0;
			gameOver = false;
			waveStartTime = System.currentTimeMillis();
			homeScreen = false;
		}
		
		/**
		 * Gets the current wave number.
		 * 
		 * @return the current wave number
		 */
		public int getWave() { 
			return wave; 
		}
		
		/**
		 * Gets the current player score.
		 * 
		 * @return the current player score
		 */
		public int getScore() { 
			return score; 
		}
		
		/**
		 * Checks if the game is over.
		 * 
		 * @return true if the game is over, false otherwise
		 */
		public boolean isGameOver() { 
			return gameOver; 
		}
		
		/**
		 * Gets the start time of the current wave in milliseconds.
		 * 
		 * @return the start time of the current wave in milliseconds
		 */
		public long getWaveStartTime() { 
			return waveStartTime; 
		}
		
		/**
		 * Checks if the home screen is currently displayed.
		 * 
		 * @return true if the home screen is currently displayed
		 */
		public boolean isHomeScreen() { 
			return homeScreen; 
		}
		
		/**
		 * Gets the time when wave text display started.
		 * 
		 * @return the time when wave text display started in milliseconds
		 */
		public long getWaveTextDisplayTime() { 
			return waveTextDisplayTime; 
		}
		
		/**
		 * Checks if wave transition text should be displayed.
		 * 
		 * @return true if wave transition text should be displayed
		 */
		public boolean showWaveText() { 
			return showWaveText; 
		}
		
		/**
		 * Advances to the next wave and prepares wave transition display.
		 */
		public void nextWave() { 
			wave++; 
			setShowWaveText(true);
			waveTextDisplayTime = System.currentTimeMillis();
		}
		
		/**
		 * Adds points to the player's score.
		 * 
		 * @param points the number of points to add
		 */
		public void addScore(int points) { 
			score += points; 
		}
		
		/**
		 * Sets the game over state.
		 * 
		 * @param gameOver true to end the game, false to continue
		 */
		public void setGameOver(boolean gameOver) { 
			this.gameOver = gameOver; 
		}
		
		/**
		 * Sets whether the home screen should be displayed.
		 * 
		 * @param homeScreen true to show home screen, false to hide
		 */
		public void setHomeScreen(boolean homeScreen) { 
			this.homeScreen = homeScreen; 
		}
		
		/**
		 * Sets whether wave transition text should be displayed.
		 * 
		 * @param showWaveText true to show wave text, false to hide
		 */
		public void setShowWaveText(boolean showWaveText) { 
			this.showWaveText = showWaveText; 
		}
		
		/**
		 * Sets the time when the wave text began displaying.
		 * 
		 * @param waveTextDisplayTime the time in milliseconds when wave text display started
		 */
		public void setWaveTextDisplayTime(long waveTextDisplayTime) { 
			this.waveTextDisplayTime = waveTextDisplayTime; 
		}

		/**
		 * Sets the start time for the current wave.
		 * 
		 * @param waveStartTime the start time in milliseconds
		 */
		public void setWaveStartTime(long waveStartTime) { 
			this.waveStartTime = waveStartTime; 
		}
	}
	
	/**
	 * Restarts the game from the beginning.
	 * Resets game state, clears all entities, restarts music,
	 * and spawns the first wave.
	 */
	private void restartGame() {
		gameState.reset();
		asteroids.clear();
		bullets.clear();
		shieldActive = false;
		shield = null;
		spawnWave();
		playBackgroundMusic();
	}

	/**
	 * Spawns a new wave of asteroids based on the current wave number.
	 * Asteroids are spawned at random positions along screen edges
	 * with randomized velocities and sizes. Also resets the ship position 
	 * to the center of the screen.
	 */
	private void spawnWave() {
		int numAsteroids = 5 + gameState.getWave() * 2;    
	 
		for (int i = 0; i < numAsteroids; i++) {
			double x, y, vx, vy;
		 
			int edge = (int)(Math.random() * 4);
			switch(edge) {
				case 0: // Top edge
					//Asteroids a little bit slower on wave 1 for easier initial/first game experience
					if(gameState.getWave() < 2) {
						x = Math.random() * 800;
						y = -50;
						vx = (Math.random() - 0.5) * 1.8;
						vy = Math.random() * 1.8 + 0.5;
						break;
					}
					x = Math.random() * 800;
					y = -50;
					vx = (Math.random() - 0.5) * 2;
					vy = Math.random() * 2 + 0.5;
					break;
				case 1: // Right edge
					//Asteroids a little bit slower on wave 1 for easier initial/first game experience
					if(gameState.getWave() < 2) {
						x = 850;
						y = Math.random() * 600;
						vx = -(Math.random() * 1.8 + 0.5);
						vy = (Math.random() - 0.5) * 1.8;
						break;
					}
					x = 850;
					y = Math.random() * 600;
					vx = -(Math.random() * 2 + 0.5);
					vy = (Math.random() - 0.5) * 2;
					break;
				case 2: // Bottom edge
					//Asteroids a little bit slower on wave 1 for easier initial/first game experience
					if(gameState.getWave() < 2) {
						x = Math.random() * 800;
						y = 650;
						vx = (Math.random() - 0.5) * 1.8;
						vy = -(Math.random() * 1.8 + 0.5);
						break;
					}
					x = Math.random() * 800;
					y = 650;
					vx = (Math.random() - 0.5) * 2;
					vy = -(Math.random() * 2 + 0.5);
					break;
				default: // Left edge
					//Asteroids a little bit slower on wave 1 for easier initial/first game experience
					if(gameState.getWave() < 6) {
						x = -50;
						y = Math.random() * 600;
						vx = Math.random() * 1.8 + 0.5;
						vy = (Math.random() - 0.5) * 1.8;
						break;
					}
					x = -50;
					y = Math.random() * 600;
					vx = Math.random() * 2 + 0.5;
					vy = (Math.random() - 0.5) * 2;
					break;
			}
		 
			int size;
			int wave = gameState.getWave();
			
			if (wave <= 3) {
				// Waves 1-3: Only big asteroids
				size = 3;
			} else if (wave <= 5) {
				// Waves 4-5: Big and medium only
				double rand = Math.random();
				size = rand < 0.5 ? 2 : 3;
			} else {
				// Wave 6+: Normal distribution (small, medium, big)
				double rand = Math.random();
				if (rand < 0.1) size = 1;
				else if (rand < 0.5) size = 2;
				else size = 3;
			}
		 
			asteroids.add(new Asteroid(new Point(x, y), vx, vy, size));
		}
		ship = new Ship(new Point(400, 300));
		if(shieldActive) {
			shield = new Shield(new Point(406, 311));
		}
	}

	/**
	 * Renders the entire game screen, including background, ship, asteroids,
	 * bullets, UI, and various game states such as home screen, active play,
	 * wave transitions, and game over. Handles all collision detection and
	 * wave progression logic.
	 * 
	 * @param g the Graphics object used for drawing
	 */
	public void paint(Graphics g) {
		if (asteroids == null || bullets == null) return;

		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		if (gameState.isHomeScreen()) {
			drawHomeScreen(g);
			return;
		}

		if (gameState.isGameOver()) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("GAME OVER", 250, 250);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString("Final Score:", 300, 300);
			g.setFont(new Font("Arial", Font.PLAIN, 24));
			g.drawString("" + gameState.getScore(), 450, 300);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString("Wave Reached:", 300, 330);
			g.setFont(new Font("Arial", Font.PLAIN, 24));
			g.drawString("" + gameState.getWave(), 490, 330);
			g.drawString("Press R to Restart", 300, 380);
			return;
		}
		
		// Dynamic time scale based on wave progression
		int timeScale;
		int wave = gameState.getWave();
		//First wave is quickest so that they can see faster 
		//that there are waves in this game. 
		if (wave == 1) timeScale = 12; 
		else if (wave < 4) timeScale = 13;
		else if (wave < 9) timeScale = 13;
		else if (wave < 13) timeScale = 15;
		else if (wave < 18) timeScale = 20;
		else timeScale = 21;
		
		long currentTime = System.currentTimeMillis();
		long elapsedSeconds = (currentTime - gameState.getWaveStartTime()) / 1000;
		long remainingSeconds = timeScale - elapsedSeconds;
		
		if (gameState.showWaveText()) remainingSeconds = timeScale;
		
		// Advance wave if time expires and asteroids remain
		if (elapsedSeconds >= timeScale && !asteroids.isEmpty()) {
			asteroids.clear();
			gameState.nextWave();
		}
		
		// Display wave transition text
		if (gameState.showWaveText()) {
			if(gameState.getWave() == 6) {
				if (System.currentTimeMillis() - gameState.getWaveTextDisplayTime() < 60000) {
					bullets.clear();
					g.setColor(Color.white);
					g.setFont(new Font("Arial", Font.BOLD, 50));
					g.drawString("Wave 5 completed!", 150, 110);
					if(System.currentTimeMillis() - gameState.getWaveTextDisplayTime() > 1000) {
						g.setFont(new Font("Arial", Font.PLAIN, 40));
						g.drawString("Here.", 150, 200);
						ship = new Ship(new Point(400, 300));
						ship.draw(g);
					}
					if(System.currentTimeMillis() - gameState.getWaveTextDisplayTime() > 1700) {
						g.drawString("Have this shield for protection.", 150, 250);
					}
					if(System.currentTimeMillis() - gameState.getWaveTextDisplayTime() > 2700) {
						shield = new Shield(new Point(406, 311));
						shieldActive = true;
						shield.draw(g);
					}
					if(System.currentTimeMillis() - gameState.getWaveTextDisplayTime() > 3700) {
						g.setFont(new Font("Arial", Font.BOLD, 40));
						g.setColor(Color.red);
						g.drawString("Watch out for wave 18...", 150, 400);
					}
					if(System.currentTimeMillis() - gameState.getWaveTextDisplayTime() > 5700) {
						g.setFont(new Font("Arial", Font.PLAIN, 40));
						g.setColor(Color.white);
						g.setFont(new Font("Times New Roman", Font.PLAIN, 38));
						g.drawString("C l i c k   a n y w h e r e"
									+ "   t o   continue . . . ", 72, 475);
					}
					if(clicked) {
						gameState.setWaveTextDisplayTime(System.currentTimeMillis() - 60000);
						clicked = false;
					}
					return;
				}           		
				g.setColor(Color.white);
				g.setFont(new Font("Arial", Font.BOLD, 48));
				g.drawString("WAVE " + gameState.getWave(), 300, 300);
				if (System.currentTimeMillis() - gameState.getWaveTextDisplayTime() >= 62000) {
					gameState.setShowWaveText(false);
					gameState.setWaveStartTime(System.currentTimeMillis());
					spawnWave();
				}
				return;
			}
			g.setColor(gameState.getWave() < 18 ? Color.white : Color.red);
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("WAVE " + gameState.getWave(), 300, 300);
			if (System.currentTimeMillis() - gameState.getWaveTextDisplayTime() >= 2000) {
				gameState.setShowWaveText(false);
				gameState.setWaveStartTime(System.currentTimeMillis());
				spawnWave();
			}
		}
		
		// Handle continuous rotation
		if (ship != null && !gameState.showWaveText()) {
			if (leftPressed) {
				ship.rotate(-5);
			}
			if (rightPressed) {
				ship.rotate(5);
			}
		}
		
		// Draw UI
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.drawString("Wave: " + gameState.getWave(), 10, 20);
		g.drawString("Score: " + gameState.getScore(), 10, 40);
		g.drawString("Asteroids: " + asteroids.size(), 10, 60);
		g.drawString("Time: " + Math.max(0, remainingSeconds) + "s", 10, 80);

		// Draw ship
		if (ship != null && !gameState.showWaveText()) {
			ship.move();
			ship.draw(g);
		}
		
		// Draw shield if active
		if (shield != null && !gameState.showWaveText()) {
			shield.move();
			shield.draw(g);
		}

		// Draw asteroids
		if (!gameState.showWaveText()) {
			for (Asteroid a : asteroids) {
				a.move();
				a.draw(g);
			}
		}

		// Draw bullets
		for (Bullet b : bullets) {
			b.move();
			b.draw(g);
		}

		bullets.removeIf(b -> !b.isAlive());

		List<Asteroid> deadAsteroids = new ArrayList<>();

		// Bullet-asteroid collisions
		for (Asteroid a : asteroids) {
			for (Bullet b : bullets) {
				if (a.getPolygon().contains(b.getPolygon().position)) {
					b.destroy();
					deadAsteroids.add(a);
					gameState.addScore(100);
				}
			}
		}
		
		// Ship-asteroid collisions
		if (ship != null) {
			for (Point point : ship.getPolygon().getPoints()) {
				for (Asteroid a : asteroids) {
					if (a.getPolygon().contains(point)) {
						if(shieldActive) {
							shieldActive = false;
							deadAsteroids.add(a);
							shield = null;
						}
						else {
							gameState.setGameOver(true);
							stopBackgroundMusic();
						}                        
					}
				}
			}
		}

		asteroids.removeAll(deadAsteroids);

		// Advance to next wave when all asteroids are destroyed
		if (asteroids.isEmpty() && !gameState.isGameOver() && !gameState.showWaveText()) {
			gameState.nextWave();
		}
	}
	
	/**
	 * Draws the home screen with game title, controls, and start instructions.
	 * 
	 * @param g the Graphics object used for drawing
	 */
	private void drawHomeScreen(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 80));
		g.drawString("ASTEROIDS", 150, 170);
		
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString("by Gitan and Ray", 300, 210);

		g.setFont(new Font("Arial", Font.BOLD, 29));
		g.drawString("Controls", 320, 260);
		
		g.setFont(new Font("Arial", Font.PLAIN, 25));
		g.drawString("↑ or W - Accelerate", 270, 295);
		g.drawString("← → or A D - Rotate Ship", 245, 325);
		g.drawString("SPACE - Shoot", 295, 355);
		
		g.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		g.drawString("C l i c k   a n y w h e r e   t o   s t a r t . . . ", 190, 420);
	}

	/**
	 * Main entry point for the Asteroids game.
	 * Creates a new game instance and starts the game loop.
	 * 
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		Asteroids a = new Asteroids();
		a.repaint();
	}
}