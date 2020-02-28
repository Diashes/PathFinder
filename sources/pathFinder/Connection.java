package pathFinder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Connection extends JButton implements Serializable {
	private static final long serialVersionUID = 7360971019806752464L;
	private Point firstPoint;
	private Point secondPoint;
	private Rectangle rect;
	private Color color;
	GeneralPath path;

	/**
	 * Skapar en rektangel baserat på koordinaterna för de två noderna och använder
	 * den för storleken på komponenten. Gör komponenten osynlig.
	 * @param from
	 * @param to
	 * @param picturePanel
	 */
	Connection(Location from, Location to, JPanel picturePanel) {
		firstPoint = new Point(from.getPosX(), from.getPosY()); 
		secondPoint = new Point(to.getPosX(), to.getPosY());
		rect = new Rectangle(firstPoint);
		rect.add(secondPoint);
		setBounds(rect);
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		repaint();
		validate();
	}

	/**
	 * Ritar ut strecket mellan noderna.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(1));
		if ((firstPoint.getX() < secondPoint.getX() && firstPoint.getY() < secondPoint.getY()) // Rita linje om startpunktens X är mindre än slutpunktens X och startpunktens Y är
																								// större än slutpunktens Y.
				|| (secondPoint.getX() < firstPoint.getX() && secondPoint.getY() < firstPoint.getY())) {
			g2d.drawLine(0, 0, (int) rect.getWidth(), (int) rect.getHeight());
		} else if ((firstPoint.getX() > secondPoint.getX() && firstPoint.getY() < secondPoint.getY()) // Rita linje om startpunktens X är större än slutpunktens X och startpunktens
																										// Y är mindre än slutpunktens Y.
				|| (secondPoint.getX() > firstPoint.getX() && secondPoint.getY() < firstPoint.getY())) {
			g2d.drawLine((int) rect.getWidth(), 0, 0, (int) rect.getHeight());
		}
	}

	public void changeColor(Color color) {
		this.color = color;
	}
}
