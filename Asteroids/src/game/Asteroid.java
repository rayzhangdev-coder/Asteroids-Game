package game;

import java.awt.Graphics;
import java.awt.Color;

/**
 * Represents an asteroid in the Asteroids game.
 * Implements the Moveable interface to handle movement and rendering of asteroids.
 * Asteroids are hexagonal polygons that move across the screen with random rotation
 * and implement screen wrapping behavior.
 * 
 * @author Ray Zhang and Gitan Mandell
 */
public class Asteroid implements Moveable {
    private Polygon shape;
    private double velocityX, velocityY;
    private int size; // 1=small, 2=medium, 3=large

    /**
     * Constructs a new Asteroid with specified position, velocity, and size.
     * Creates a hexagonal polygon scaled according to the asteroid size.
     * 
     * @param position the initial position of the asteroid's center
     * @param vx the initial horizontal velocity
     * @param vy the initial vertical velocity
     * @param size the size of the asteroid (1=small, 2=medium, 3=large)
     */
    public Asteroid(Point position, double vx, double vy, int size) {
        this.size = size;
        double scale = size * 10.0; // 10, 20, or 30 pixels
        
        // hexagon scaled by size
        Point[] points = {
            new Point(scale, 0),
            new Point(scale/2, scale),
            new Point(-scale/2, scale),
            new Point(-scale, 0),
            new Point(-scale/2, -scale),
            new Point(scale/2, -scale)
        };
        shape = new Polygon(points, position, Math.random()*360);
        velocityX = vx;
        velocityY = vy;
    }
    
    /**
     * Constructs a new large Asteroid (size 3) with specified position and velocity.
     * 
     * @param position the initial position of the asteroid's center
     * @param vx the initial horizontal velocity
     * @param vy the initial vertical velocity
     */
    public Asteroid(Point position, double vx, double vy) {
        this(position, vx, vy, 3);
    }

    /**
     * Updates the asteroid's position based on its velocity.
     * Applies continuous rotation and handles screen wrapping with a buffer
     * so asteroids disappear completely before reappearing on the opposite side.
     */
    public void move() {
        shape.position.x += velocityX;
        shape.position.y += velocityY;
        shape.rotation += 1; // Continuous rotation effect
        
        // Screen wrapping with buffer to ensure smooth transitions
        if (shape.position.x < -20) shape.position.x = 820;
        if (shape.position.x > 820) shape.position.x = -20;
        if (shape.position.y < -20) shape.position.y = 620;
        if (shape.position.y > 620) shape.position.y = -20;
    }

    /**
     * Renders the asteroid on the screen as a white outlined hexagon.
     * 
     * @param g the Graphics object used for drawing
     */
    public void draw(Graphics g) {
        Point[] pts = shape.getPoints();
        int[] xs = new int[pts.length];
        int[] ys = new int[pts.length];
        for (int i = 0; i < pts.length; i++) {
            xs[i] = (int) pts[i].x;
            ys[i] = (int) pts[i].y;
        }
        g.setColor(Color.white);
        g.drawPolygon(xs, ys, pts.length);
    }

    /**
     * Returns the polygon representing the asteroid's shape and position.
     * 
     * @return the asteroid's polygon
     */
    public Polygon getPolygon() {
        return shape;
    }
    
    /**
     * Returns the size of the asteroid.
     * 
     * @return the asteroid size (1=small, 2=medium, 3=large)
     */
    public int getSize() {
        return size;
    }
}
