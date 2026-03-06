package game;

import java.awt.Graphics;
import java.awt.Color;

public class Shield implements Moveable {
    private Point position;
    private double velocityX = 0;
    private double velocityY = 0;
    private double MAX_SPEED = 3.0;
    private double radius = 20.0;

    public Shield(Point position) {
        this.position = position;
    }

    public void accelerate(double angleDegrees, double thrust) {
        double radians = Math.toRadians(angleDegrees - 90);
        velocityX += thrust * Math.cos(radians);
        velocityY += thrust * Math.sin(radians);

        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > MAX_SPEED) {
            double scale = MAX_SPEED / speed;
            velocityX *= scale;
            velocityY *= scale;
        }
    }

    public void move() {
        position.x += velocityX;
        position.y += velocityY;

        velocityX *= 0.985;
        velocityY *= 0.985;

        // Screen wrapping
        if (position.x < 0) position.x = 800;
        if (position.x > 800) position.x = 0;
        if (position.y < 0) position.y = 600;
        if (position.y > 600) position.y = 0;
    }

    public void draw(Graphics g) {
        int r = (int) radius;
        int x = (int) (position.x - r);
        int y = (int) (position.y - r);

        g.setColor(Color.cyan);
        g.drawOval(x, y, r * 2, r * 2);
    }

    public Point getPosition() { return position; }

	@Override
	public Polygon getPolygon() {
		// TODO Auto-generated method stub
		return null;
	}
}
