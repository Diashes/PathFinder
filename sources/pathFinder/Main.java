package pathFinder;

import graphs.Edge;
import graphs.GraphMethods;
import graphs.ListGraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Amanda Stensland
 * Senast ändrad: 5 juni 2015
 */
public class Main extends JFrame {
	private ArrayList<Location> selectedLocations = new ArrayList<Location>();
	private MapListener mapListener = new MapListener();
	private ListGraph<Location> listGraph = new ListGraph<Location>();
	private ClickedListener clickedListener;
	private PicturePanel picturePanel;
	private Location clickedLocation, from, to;
	private Edge<Location> getConnection;
	private boolean savedState = true;
	private File savedFile = null;
	private static final FileNameExtensionFilter pictureFilter = new FileNameExtensionFilter(".jpg/.png/.gif-filer", "jpg", "png", "gif");
	private static final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(".pfr-filer", "pfr");
	private JMenuItem newFileMenuItem, openFileMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem, findPathMenuItem, showConnectionMenuItem, newLocationMenuItem,
			newConnectionMenuItem, changeConnectionMenuItem;
	private JButton findPathButton, showConnectionButton, newLocationButton, newConnectionButton, changeConnectionButton;
	private JLabel cLabel, tLabel;
	private JTextField cField, tField;
	private JTextArea textArea;
	private String currentFile;

	/**
	 * Skapar huvudfönstret.
	 */
	Main() {
		super("PathFinder");
		setLayout(new BorderLayout());
		setJMenuBar(createMenuBar());
		add(createButtons(), BorderLayout.NORTH);
		disableButtonsAndMenu();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitListener());
		setVisible(true);
		repaint();
		pack();
	}
	
	/**
	 * Skapar menyn
	 * @return
	 */
	private JMenuBar createMenuBar() { 
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Arkiv");
		menuBar.add(fileMenu);
		newFileMenuItem = new JMenuItem("Ny");
		openFileMenuItem = new JMenuItem("Öppna...");
		saveMenuItem = new JMenuItem("Spara");
		saveAsMenuItem = new JMenuItem("Spara som...");
		exitMenuItem = new JMenuItem("Avsluta");
		fileMenu.add(newFileMenuItem);
		fileMenu.add(openFileMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(exitMenuItem);
		newFileMenuItem.addActionListener(new NewFileListener());
		openFileMenuItem.addActionListener(new OpenFileListener());
		saveMenuItem.addActionListener(new SaveListener());
		saveAsMenuItem.addActionListener(new SaveAsListener());
		exitMenuItem.addActionListener(new ExitFromMenuListener());
		JMenu operationsMenu = new JMenu("Operationer");
		menuBar.add(operationsMenu);
		findPathMenuItem = new JMenuItem("Hitta väg");
		showConnectionMenuItem = new JMenuItem("Visa förbindelse");
		newLocationMenuItem = new JMenuItem("Ny plats");
		newConnectionMenuItem = new JMenuItem("Ny förbindelse");
		changeConnectionMenuItem = new JMenuItem("Ändra förbindelse");
		operationsMenu.add(findPathMenuItem);
		operationsMenu.add(showConnectionMenuItem);
		operationsMenu.add(newLocationMenuItem);
		operationsMenu.add(newConnectionMenuItem);
		operationsMenu.add(changeConnectionMenuItem);
		return menuBar;
	}

	/**
	 * Skapar knappar
	 * @return
	 */
	private JPanel createButtons() { 
		JPanel buttonPanel = new JPanel();
		findPathButton = new JButton("Hitta väg");
		showConnectionButton = new JButton("Visa förbindelse");
		newLocationButton = new JButton("Ny plats");
		newConnectionButton = new JButton("Ny förbindelse");
		changeConnectionButton = new JButton("Ändra förbindelse");
		buttonPanel.add(findPathButton);
		buttonPanel.add(showConnectionButton);
		buttonPanel.add(newLocationButton);
		buttonPanel.add(newConnectionButton);
		buttonPanel.add(changeConnectionButton);
		return buttonPanel;
	}

	

	/**
	 * Lyssnare: Öppnar den valda bilden som en ny karta.
	 */
	class NewFileListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			String fileName = selectFile(pictureFilter);
			if (fileName != null) {
				if (picturePanel != null)
					remove(picturePanel);
				picturePanel = new PicturePanel(fileName);
				add(picturePanel, BorderLayout.CENTER);
				picturePanel.setLayout(null);
				enableButtonsAndMenu();
				validate();
				repaint();
				pack();
			}
		}
	}

	/**
	 * Lyssnare för att öppna en fil.
	 */
	class OpenFileListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String fileName = selectFile(fileFilter);
				if (fileName == null)
					return;
				File selectedFile = new File(fileName);
				FileInputStream fis = new FileInputStream(selectedFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				removeOldData();
				picturePanel = (PicturePanel) ois.readObject();
				listGraph = (ListGraph<Location>) ois.readObject();
				ois.close();
				add(picturePanel, BorderLayout.CENTER);
				picturePanel.setLayout(null);
				for (Component mapItem : picturePanel.getComponents()) {
					((JButton) mapItem).addActionListener(mapListener);
					if (mapItem instanceof Location)
						((Location) mapItem).changeColor(Color.RED);
					else if (mapItem instanceof Connection)
						((Connection) mapItem).changeColor(Color.BLACK);
				}
				enableButtonsAndMenu();
				currentFile = fileName;
				savedState = true;
				validate();
				repaint();
				pack();
			} catch (FileNotFoundException fnfe) {
				JOptionPane.showMessageDialog(null, "Hittar inte filen!");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, "Ett fel inträffade: " + ioe.getMessage());
			} catch (ClassNotFoundException cnfe) {
				JOptionPane.showMessageDialog(null, "Class Not Found: " + cnfe.getMessage());
			} catch (NullPointerException npe) {
				return;
			}
		}
	}
	
	/**
	 * Återställer alla selektioner på kartan.
	 */
	private void removeOldData() { 
		if (picturePanel != null) {
			this.remove(picturePanel);
		}
		listGraph = new ListGraph<Location>();
		from = null;
		to = null;
		for (Location lo : selectedLocations)
			lo.changeColor(Color.RED);
		selectedLocations = new ArrayList<Location>();
		clickedLocation = null;
	}
	
	/**
	 * Lyssnar om användaren trycker på Spara.
	 */
	class SaveListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				save();
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, "Ett fel inträffade: " + ioe.getMessage());
			}
		}
	}
	
	/**
	 * Sparar kartan automatiskt.
	 */
	private void save() throws IOException, FileNotFoundException { // Sparar filen automatiskt.
		if (currentFile == null) { saveAs(); }
		else { saveData(new File(currentFile)); }
	}

	/**
	 * Lyssnar om användaren trycker på Spara som...
	 */
	class SaveAsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				saveAs();
			} catch (FileNotFoundException fnfe) {
				JOptionPane.showMessageDialog(null, "Hittar inte filen!");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, "Ett fel inträffade: " + ioe.getMessage());
			}
		}
	}

	/**
	 * Sparar filen genom att öppna en ny dialogruta.
	 */
	private void saveAs() throws IOException, FileNotFoundException { // Sparar kartan till en fil som användaren skapar.
		String selectedFilePath = selectFile(fileFilter);
		if (selectedFilePath != null) {
			if (!selectedFilePath.endsWith(".pfr")) {
				selectedFilePath += ".pfr";
			}
			savedFile = new File(selectedFilePath);
			saveData(savedFile);
		}
	}
	
	/**
	 * Öppnar dialogrutan på rätt sökväg och adderar fil-filter.
	 * @param filter
	 * @return
	 */
	private String selectFile(FileNameExtensionFilter filter) { // Öppnar filechooser och returnerar den valda filen
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "//Pictures");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(Main.this);
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		return fileChooser.getSelectedFile().getAbsolutePath();
	}

	/**
	 * Öppnar en filström och sparar data.
	 * @param selectedFile
	 */
	private void saveData(File selectedFile) throws IOException, FileNotFoundException { // Spara Data
		FileOutputStream fos = new FileOutputStream(selectedFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Component mapItem : picturePanel.getComponents()) {
			((JButton) mapItem).removeActionListener(mapListener);
		}
		oos.writeObject(picturePanel);
		oos.writeObject(listGraph);
		oos.close();
		for (Component mapItem : picturePanel.getComponents())
			((JButton) mapItem).addActionListener(mapListener);
		JOptionPane.showMessageDialog(null, "Din karta sparades.");
		savedState = true;
	}

	/**
	 * Lyssnar om användaren vill stänga fönstret.
	 */
	class ExitListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent we) {
			if (savedState == false) {
				int chosenOption = JOptionPane.showConfirmDialog(null, "Du har fortfarande osparade ändringar. Vill du spara dem?", "Avsluta", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (chosenOption == JOptionPane.YES_OPTION)
					try {
						saveAs();
					} catch (FileNotFoundException fnfe) {
						JOptionPane.showMessageDialog(null, "Hittar inte filen!");
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(null, "Ett fel inträffade: " + ioe.getMessage());
					}
				else if (chosenOption == JOptionPane.NO_OPTION)
					System.exit(0);
			} else {
				System.exit(0);
			}
		}
	}

	/**
	 * Lyssnar om användaren vill avsluta från menyn.
	 */
	class ExitFromMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (savedState == false) {
				int chosenOption = JOptionPane.showConfirmDialog(null, "Du har fortfarande osparade ändringar. Vill du avsluta ändå?", "Avsluta", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (chosenOption == JOptionPane.OK_OPTION)
					System.exit(0);
			} else {
				System.exit(0);
			}
		}
	}

	/**
	 * Lyssnare för knappen Hitta väg.
	 */
	class FindPathListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (checkSelection() != false)
				JOptionPane.showMessageDialog(null, createFindPathWindow(), "Kortaste Vägen", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/**
	 * Skapar Hitta Väg-fönstret.
	 * @return
	 */
	private JPanel createFindPathWindow() {
		JPanel messagePanel = new JPanel();
		ArrayList<Edge<Location>> path = GraphMethods.getFastestPath(listGraph, from, to);
		if (path != null) {
			JScrollPane scrollPane = new JScrollPane();
			textArea = new JTextArea("Från " + from + " till " + to + ": \n");
			textArea.add(scrollPane);
			int totaltAntalMin = 0;
			for (Edge<Location> edge : path) {
				textArea.append(edge.toString() + "\n");
				totaltAntalMin += edge.getWeight();
			}
			textArea.append("Totalt: " + totaltAntalMin + " minuter.");
			textArea.setEditable(false);
			messagePanel.add(textArea);
		} else {
			messagePanel.add(new JLabel("Det hittades ingen väg mellan " + from + " och " + to + "."));
		}
		return messagePanel;
	}

	/**
	 * Lyssnare för knappen Visa Förbindelse.
	 */
	class ShowConnectionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (!checkSelection() || checkConnection()) { return; }
				JOptionPane.showMessageDialog(null, createShowConnectionWindow(), "Visa förbindelse", JOptionPane.PLAIN_MESSAGE);
			} catch (IllegalStateException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		}
	}

	/**
	 * Skapar "Visa förbindelse"-fönstret
	 * @return
	 */
	private JPanel createShowConnectionWindow() { 
		JPanel messagePanel = new JPanel();
		cLabel = new JLabel("Förbindelsen från " + from + " till " + to + ":");
		tLabel = new JLabel("Tid (min):");
		cField = new JTextField(getConnection.getEdgeName());
		tField = new JTextField(String.valueOf(getConnection.getWeight()));
		cField.setEditable(false);
		tField.setEditable(false);
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(cLabel);
		messagePanel.add(cField);
		messagePanel.add(tLabel);
		messagePanel.add(tField);
		return messagePanel;
	}

	/**
	 * Lyssnare för knappen "Ny plats"
	 */
	class NewLocationListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			picturePanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			clickedListener = new ClickedListener();
			picturePanel.addMouseListener(clickedListener);
		}
	}

	/**
	 * Lyssnare för knappen "Ny förbindelse"
	 * @return
	 */
	class NewConnectionListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			String cName;
			int cTime;
			if (!checkSelection() || !checkConnection()) { return; }
			JPanel connectionWindow = createNewConnectionWindow();
			while (true) {
				try {
					int chosenOption = JOptionPane.showConfirmDialog(null, connectionWindow, "Ny förbindelse", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (chosenOption == JOptionPane.OK_OPTION) {
						cName = cField.getText();
						cTime = Integer.parseInt(tField.getText());
						if (cName.isEmpty()) {
							throw new IllegalArgumentException("Du måste skriva in ett namn.");
						}
						listGraph.connect(cName, cTime, from, to);
						Connection connection = new Connection(from, to, picturePanel);
						connection.addActionListener(mapListener);
						picturePanel.add(connection);
						savedState = false;
						validate();
						repaint();
						pack();
						break;
					} else {
						break;
					}
				} catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, ise.getMessage());
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(null, "Skriv tiden i siffror.");
				} catch (NullPointerException npe) {
					JOptionPane.showMessageDialog(null, "Du måste välja två platser först.");
				} catch (IndexOutOfBoundsException ioe) {
					JOptionPane.showMessageDialog(null, "Du måste välja två platser först.");
					return;
				}
			}
		}
	}

	/**
	 * Lyssnare för knappen "Ändra förbindelse"
	 */
	class ChangeConnectionListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			int cTime;
			try {
				if (!checkSelection() || checkConnection()) { return; }
				int chosenOption = JOptionPane.showConfirmDialog(null, createChangeConnectionWindow(), "Ändra förbindelse", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (chosenOption == JOptionPane.OK_OPTION) {
					cTime = Integer.parseInt(tField.getText());
					listGraph.setConnectionWeight(from, to, cTime);
					JOptionPane.showMessageDialog(null, "Tiden på förbindelsen mellan " + from + " och " + to + " ändrades till " + getConnection.getWeight() + " minuter.");
					savedState = false;
				}
			} catch (IllegalStateException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Fel format på tiden.");
			} catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Det finns ingen förbindelse att ändra mellan platserna.");
			}
		}
	}
	
	/**
	 * Skapar "Ändra förbindelse"-fönstret
	 * @return
	 */
	private JPanel createChangeConnectionWindow() { 
		JPanel messagePanel = new JPanel();
		getConnection = listGraph.getEdgeBetween(from, to);
		cLabel = new JLabel("Förbindelsen från " + from + " till " + to + ":");
		tLabel = new JLabel("Tid (min):");
		cField = new JTextField(getConnection.getWeight() + " minuter.");
		tField = new JTextField(10);
		tField.addAncestorListener(new RequestFocusListener());
		cField.setEditable(false);
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(cLabel);
		messagePanel.add(cField);
		messagePanel.add(tLabel);
		messagePanel.add(tField);
		return messagePanel;
	}

	/**
	 * Lyssnar vart användaren klickar och skapar en ny plats där.
	 */
	class ClickedListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent me) {
			picturePanel.removeMouseListener(clickedListener);
			picturePanel.setCursor(Cursor.getDefaultCursor());
			int x = me.getX();
			int y = me.getY();
			JPanel messagePanel = new JPanel();
			JLabel nLabel = new JLabel("Platsens namn:");
			JTextField nField = new JTextField(10);
			nField.addAncestorListener(new RequestFocusListener());
			messagePanel.add(nLabel);
			messagePanel.add(nField);
			int returnValue;
			while (true) {
				returnValue = JOptionPane.showConfirmDialog(null, messagePanel, "Ny plats", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (returnValue == JOptionPane.CANCEL_OPTION) { return; }
				if (nField.getText().isEmpty()) { JOptionPane.showMessageDialog(null, "Platsens namn är för kort."); } else { break; }
			}
			Location newLocation = new Location(nField.getText(), x, y, picturePanel);
			newLocation.addActionListener(mapListener);
			listGraph.add(newLocation);
			picturePanel.add(newLocation);
			savedState = false;
			validate();
			repaint();
			pack();
		}
	}
	
	/**
	 * Ser till att rätt komponent i jOptionPane blir fokuserat.
	 */
	public class RequestFocusListener implements AncestorListener {
		private boolean removeListener;
		public RequestFocusListener() { this(true); }
		public RequestFocusListener(boolean removeListener) { this.removeListener = removeListener; }
		@Override
		public void ancestorAdded(AncestorEvent e) {
			JComponent component = e.getComponent();
			component.requestFocusInWindow();
			if (removeListener)
				component.removeAncestorListener( this );
		}
		@Override
		public void ancestorMoved(AncestorEvent e) {}
		@Override
		public void ancestorRemoved(AncestorEvent e) {}
	}
	
	/**
	 * Ändrar färg och selektion på platser.
	 */
	class MapListener implements ActionListener { 
		public void actionPerformed(ActionEvent e) {
			Object clickedItem = e.getSource();
			if (clickedItem instanceof Location) {
				clickedLocation = (Location) clickedItem;
				if (selectedLocations.contains(clickedLocation)) {
					selectedLocations.remove(clickedLocation);
					clickedLocation.changeColor(Color.RED);
				} else if (selectedLocations.size() < 2) {
					selectedLocations.add(clickedLocation);
					clickedLocation.changeColor(Color.YELLOW);
				}
			}
		}
	}

	/**
	 * Skapar "Ny förbindelse"-fönstret
	 * @return
	 */
	private JPanel createNewConnectionWindow() { 
		JPanel messagePanel = new JPanel();
		getConnection = listGraph.getEdgeBetween(from, to);
		cLabel = new JLabel("Förbindelsen från " + from + " till " + to + ":");
		tLabel = new JLabel("Tid (min):");
		cField = new JTextField(10);
		cField.addAncestorListener(new RequestFocusListener());
		tField = new JTextField(10);
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(cLabel);
		messagePanel.add(cField);
		messagePanel.add(tLabel);
		messagePanel.add(tField);
		return messagePanel;
	}

	/**
	 * Kontrollerar de valda platserna finns.
	 * @return
	 */
	private boolean checkSelection() { 
		try {
			from = selectedLocations.get(0);
			to = selectedLocations.get(1);
		} catch (IndexOutOfBoundsException ioe) {
			return false;
		}
		return true;
	}

	/**
	 * Kontrollerar den valda förbindelsen finns.
	 * @return
	 */
	private boolean checkConnection() {
		getConnection = listGraph.getEdgeBetween(from, to);
		if (getConnection != null) { return false; }
		return true;
	}

	/**
	 * Gör knappar oklickbara.
	 */
	private void disableButtonsAndMenu() {
		saveMenuItem.setEnabled(false);
		saveAsMenuItem.setEnabled(false);
		findPathMenuItem.setEnabled(false);
		showConnectionMenuItem.setEnabled(false);
		newLocationMenuItem.setEnabled(false);
		newConnectionMenuItem.setEnabled(false);
		changeConnectionMenuItem.setEnabled(false);
		findPathButton.setEnabled(false);
		showConnectionButton.setEnabled(false);
		newLocationButton.setEnabled(false);
		newConnectionButton.setEnabled(false);
		changeConnectionButton.setEnabled(false);
	}
	
	/**
	 * Gör knappar klickbara och lägger till lyssnare.
	 */
	private void enableButtonsAndMenu() {
		saveMenuItem.setEnabled(true);
		saveAsMenuItem.setEnabled(true);
		findPathMenuItem.setEnabled(true);
		showConnectionMenuItem.setEnabled(true);
		newLocationMenuItem.setEnabled(true);
		newConnectionMenuItem.setEnabled(true);
		changeConnectionMenuItem.setEnabled(true);
		findPathMenuItem.addActionListener(new FindPathListener());
		showConnectionMenuItem.addActionListener(new ShowConnectionListener());
		newLocationMenuItem.addActionListener(new NewLocationListener());
		newConnectionMenuItem.addActionListener(new NewConnectionListener());
		changeConnectionMenuItem.addActionListener(new ChangeConnectionListener());
		findPathButton.setEnabled(true);
		showConnectionButton.setEnabled(true);
		newLocationButton.setEnabled(true);
		newConnectionButton.setEnabled(true);
		changeConnectionButton.setEnabled(true);
		findPathButton.addActionListener(new FindPathListener());
		showConnectionButton.addActionListener(new ShowConnectionListener());
		newLocationButton.addActionListener(new NewLocationListener());
		newConnectionButton.addActionListener(new NewConnectionListener());
		changeConnectionButton.addActionListener(new ChangeConnectionListener());
	}

	public static void main(String args[]) {
		new Main();
	}
}
