package pathFinder;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JButton;

public class Location extends JButton implements Serializable {
	private static final long serialVersionUID = 6173291150456795007L;
	private String name;
	private Color color = Color.RED;
	private int x;
	private int y;
	private int size = 12;
	private int radius = size / 2;

	public Location(String name, int x, int y, PicturePanel picturePanel) {
		setBounds(x - radius, y - radius, size, size);
		this.name = name;
		this.x = x;
		this.y = y;
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
	}

	public int getPosX() {
		return x;
	}

	public int getPosY() {
		return y;
	}

	public int getRadius() {
		return radius;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(color);
		g.fillOval(0, 0, size, size);
		validate();
		repaint();
	}

	public void changeColor(Color color) {
		this.color = color;
		validate();
		repaint();
	}

	public String toString() {
		return name;
	}

}
