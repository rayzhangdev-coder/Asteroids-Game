package game;

import java.awt.Graphics;

/**
 * Interface defining the common behavior for all movable game objects.
 * Any object that can move and be rendered on screen should implement this interface.
 * This provides a consistent API for game entities like ships, asteroids, and bullets.
 * 
 * @author Ray Zhang and Gitan Mandell
 */
public interface Moveable {
    
    /**
     * Updates the object's position or state based on its movement logic.
     * Called each frame to simulate object movement.
     */
    void move();
    
    /**
     * Renders the object on the screen using the provided Graphics context.
     * 
     * @param g the Graphics object used for drawing operations
     */
    void draw(Graphics g);
    
    /**
     * Returns the geometric representation of the object for collision detection
     * and spatial calculations.
     * 
     * @return the Polygon representing the object's shape and position
     */
    Polygon getPolygon();
}