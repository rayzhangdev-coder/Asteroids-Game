package game;

import java.awt.Graphics;
import java.awt.Color;

/**
 * Represents a bullet projectile fired by the player's ship in the Asteroids game.
 * Implements the Moveable interface to handle movement and rendering of bullets.
 * Bullets travel in a straight line at fixed speed and are destroyed when they
 * go off-screen or collide with asteroids.
 * 
 * @author Ray Zhang and Gitan Mandell
 */
public class Bullet implements Moveable {
    private Polygon shape;
    private double velocityX, velocityY;
    private boolean alive = true;

    /**
     * Constructs a new Bullet with specified position and firing angle.
     * The bullet travels at a fixed speed of 5 pixels per frame in the direction
     * the ship is facing.
     * 
     * @param position the initial position where the bullet is fired from
     * @param angle the firing angle in degrees (0 = facing right, 90 = facing down)
     */
    public Bullet(Point position, double angle) {
        Point[] points = { new Point(0,0) };
        shape = new Polygon(points, position, angle);
        
        // subtract 90 to convert from standard rotation to ship's rotation system
        double radians = Math.toRadians(angle - 90);
        velocityX = 5 * Math.cos(radians);
        velocityY = 5 * Math.sin(radians);
    }

    /**
     * Updates the bullet's position based on its velocity.
     * Marks the bullet as not alive if it moves outside the screen boundaries.
     */
    public void move() {
        shape.position.x += velocityX;
        shape.position.y += velocityY;
        
        // destroy bullets that go off screen
        if (shape.position.x < 0 || shape.position.x > 800 ||
            shape.position.y < 0 || shape.position.y > 600) {
            alive = false;
        }
    }

    /**
     * Renders the bullet on the screen as a small white circle.
     * 
     * @param g the Graphics object used for drawing
     */
    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval((int)shape.position.x - 2, (int)shape.position.y - 2, 4, 4);
    }

    /**
     * Returns the polygon representing the bullet's position.
     * 
     * @return the bullet's polygon (contains only the center point)
     */
    public Polygon getPolygon() {
        return shape;
    }

    /**
     * Checks if the bullet is still active and should be rendered.
     * 
     * @return true if the bullet is alive and active, false if it should be removed
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Marks the bullet for destruction.
     * Typically called when the bullet collides with an asteroid.
     */
    public void destroy() {
        alive = false;
    }
}