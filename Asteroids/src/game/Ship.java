package game;

import java.awt.Graphics;
import java.awt.Color;

/**
 * Represents the player's spaceship in the Asteroids game.
 * Implements the Moveable interface to handle movement, rotation, acceleration,
 * and rendering of the ship. The ship is represented as a triangular polygon
 * with physics-based movement including velocity capping and screen wrapping.
 * 
 * @author Ray Zhang and Gitan Mandell
 */
public class Ship implements Moveable {
    private Polygon shape;
    private double velocityX = 0;
    private double velocityY = 0;
    private double MAX_SPEED = 3.0;
    public double rotation;

    /**
     * Constructs a new Ship at the specified position.
     * The ship is created as a triangular polygon pointing upward (0 degrees rotation).
     * 
     * @param position the initial position of the ship's center point
     */
    public Ship(Point position) {
        Point[] points = {
            new Point(0, -10),   // tip
            new Point(-7, 10),   // bottom left
            new Point(7, 10)     // bottom right
        };
        shape = new Polygon(points, position, 0); // start at 0 degrees
    }

    /**
     * Rotates the ship by the specified angle.
     * Positive values rotate clockwise, negative values rotate counter-clockwise.
     * 
     * @param angle the angle in degrees to rotate the ship
     */
    public void rotate(double angle) {
        shape.rotation += angle;
        rotation = shape.rotation;
    }

    /**
     * Accelerates the ship in the direction it's currently facing.
     * Applies thrust in the forward direction and enforces maximum speed limit.
     * 
     * @param thrust the amount of acceleration to apply
     */
    public void accelerate(double thrust) {
        // minus 90 because otherwise, forward would be to the right
        double radians = Math.toRadians(shape.rotation - 90);
        velocityX += thrust * Math.cos(radians);
        velocityY += thrust * Math.sin(radians);
        
        // Velocity cap to prevent excessive speed
        double currentSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (currentSpeed > MAX_SPEED) {
            // Scale down velocities to maintain direction but cap speed
            double scale = MAX_SPEED / currentSpeed;
            velocityX *= scale;
            velocityY *= scale;
        }
    }

    /**
     * Updates the ship's position based on current velocity.
     * Applies friction to gradually slow the ship and handles screen wrapping
     * so the ship reappears on the opposite side when leaving the screen.
     */
    public void move() {
        shape.position.x += velocityX;
        shape.position.y += velocityY;
        
        // Apply friction to gradually slow the ship
        velocityX *= 0.985;
        velocityY *= 0.985;
        
        // Screen wrapping - ship reappears on opposite side
        if (shape.position.x < 0) shape.position.x = 800;
        if (shape.position.x > 800) shape.position.x = 0;
        if (shape.position.y < 0) shape.position.y = 600;
        if (shape.position.y > 600) shape.position.y = 0;
    }

    /**
     * Renders the ship on the screen as a white filled polygon.
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
        g.fillPolygon(xs, ys, pts.length);
    }

    /**
     * Returns the polygon representing the ship's shape and position.
     * 
     * @return the ship's polygon
     */
    public Polygon getPolygon() {
        return shape;
    }
}

