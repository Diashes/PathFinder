package pathFinder;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PicturePanel extends JPanel implements Serializable {
	private static final long serialVersionUID = -2196551931206804954L;
	private ImageIcon mapPicture;
	private int width, height;
	
	public PicturePanel(String fileName){
		setLayout(null);
		mapPicture = new ImageIcon(fileName);
		height = mapPicture.getIconHeight();
		width = mapPicture.getIconWidth();
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		if (mapPicture != null)
			g.drawImage(mapPicture.getImage(), 0, 0, width, height, this);
	}
}
