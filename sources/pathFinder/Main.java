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
 * Senast �ndrad: 5 juni 2015
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
	 * Skapar huvudf�nstret.
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
		openFileMenuItem = new JMenuItem("�ppna...");
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
		findPathMenuItem = new JMenuItem("Hitta v�g");
		showConnectionMenuItem = new JMenuItem("Visa f�rbindelse");
		newLocationMenuItem = new JMenuItem("Ny plats");
		newConnectionMenuItem = new JMenuItem("Ny f�rbindelse");
		changeConnectionMenuItem = new JMenuItem("�ndra f�rbindelse");
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
		findPathButton = new JButton("Hitta v�g");
		showConnectionButton = new JButton("Visa f�rbindelse");
		newLocationButton = new JButton("Ny plats");
		newConnectionButton = new JButton("Ny f�rbindelse");
		changeConnectionButton = new JButton("�ndra f�rbindelse");
		buttonPanel.add(findPathButton);
		buttonPanel.add(showConnectionButton);
		buttonPanel.add(newLocationButton);
		buttonPanel.add(newConnectionButton);
		buttonPanel.add(changeConnectionButton);
		return buttonPanel;
	}

	

	/**
	 * Lyssnare: �ppnar den valda bilden som en ny karta.
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
	 * Lyssnare f�r att �ppna en fil.
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
				JOptionPane.showMessageDialog(null, "Ett fel intr�ffade: " + ioe.getMessage());
			} catch (ClassNotFoundException cnfe) {
				JOptionPane.showMessageDialog(null, "Class Not Found: " + cnfe.getMessage());
			} catch (NullPointerException npe) {
				return;
			}
		}
	}
	
	/**
	 * �terst�ller alla selektioner p� kartan.
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
	 * Lyssnar om anv�ndaren trycker p� Spara.
	 */
	class SaveListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				save();
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, "Ett fel intr�ffade: " + ioe.getMessage());
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
	 * Lyssnar om anv�ndaren trycker p� Spara som...
	 */
	class SaveAsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				saveAs();
			} catch (FileNotFoundException fnfe) {
				JOptionPane.showMessageDialog(null, "Hittar inte filen!");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(null, "Ett fel intr�ffade: " + ioe.getMessage());
			}
		}
	}

	/**
	 * Sparar filen genom att �ppna en ny dialogruta.
	 */
	private void saveAs() throws IOException, FileNotFoundException { // Sparar kartan till en fil som anv�ndaren skapar.
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
	 * �ppnar dialogrutan p� r�tt s�kv�g och adderar fil-filter.
	 * @param filter
	 * @return
	 */
	private String selectFile(FileNameExtensionFilter filter) { // �ppnar filechooser och returnerar den valda filen
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "//Pictures");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(Main.this);
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		return fileChooser.getSelectedFile().getAbsolutePath();
	}

	/**
	 * �ppnar en filstr�m och sparar data.
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
	 * Lyssnar om anv�ndaren vill st�nga f�nstret.
	 */
	class ExitListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent we) {
			if (savedState == false) {
				int chosenOption = JOptionPane.showConfirmDialog(null, "Du har fortfarande osparade �ndringar. Vill du spara dem?", "Avsluta", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (chosenOption == JOptionPane.YES_OPTION)
					try {
						saveAs();
					} catch (FileNotFoundException fnfe) {
						JOptionPane.showMessageDialog(null, "Hittar inte filen!");
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(null, "Ett fel intr�ffade: " + ioe.getMessage());
					}
				else if (chosenOption == JOptionPane.NO_OPTION)
					System.exit(0);
			} else {
				System.exit(0);
			}
		}
	}

	/**
	 * Lyssnar om anv�ndaren vill avsluta fr�n menyn.
	 */
	class ExitFromMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (savedState == false) {
				int chosenOption = JOptionPane.showConfirmDialog(null, "Du har fortfarande osparade �ndringar. Vill du avsluta �nd�?", "Avsluta", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (chosenOption == JOptionPane.OK_OPTION)
					System.exit(0);
			} else {
				System.exit(0);
			}
		}
	}

	/**
	 * Lyssnare f�r knappen Hitta v�g.
	 */
	class FindPathListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (checkSelection() != false)
				JOptionPane.showMessageDialog(null, createFindPathWindow(), "Kortaste V�gen", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/**
	 * Skapar Hitta V�g-f�nstret.
	 * @return
	 */
	private JPanel createFindPathWindow() {
		JPanel messagePanel = new JPanel();
		ArrayList<Edge<Location>> path = GraphMethods.getFastestPath(listGraph, from, to);
		if (path != null) {
			JScrollPane scrollPane = new JScrollPane();
			textArea = new JTextArea("Fr�n " + from + " till " + to + ": \n");
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
			messagePanel.add(new JLabel("Det hittades ingen v�g mellan " + from + " och " + to + "."));
		}
		return messagePanel;
	}

	/**
	 * Lyssnare f�r knappen Visa F�rbindelse.
	 */
	class ShowConnectionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (!checkSelection() || checkConnection()) { return; }
				JOptionPane.showMessageDialog(null, createShowConnectionWindow(), "Visa f�rbindelse", JOptionPane.PLAIN_MESSAGE);
			} catch (IllegalStateException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		}
	}

	/**
	 * Skapar "Visa f�rbindelse"-f�nstret
	 * @return
	 */
	private JPanel createShowConnectionWindow() { 
		JPanel messagePanel = new JPanel();
		cLabel = new JLabel("F�rbindelsen fr�n " + from + " till " + to + ":");
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
	 * Lyssnare f�r knappen "Ny plats"
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
	 * Lyssnare f�r knappen "Ny f�rbindelse"
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
					int chosenOption = JOptionPane.showConfirmDialog(null, connectionWindow, "Ny f�rbindelse", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (chosenOption == JOptionPane.OK_OPTION) {
						cName = cField.getText();
						cTime = Integer.parseInt(tField.getText());
						if (cName.isEmpty()) {
							throw new IllegalArgumentException("Du m�ste skriva in ett namn.");
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
					JOptionPane.showMessageDialog(null, "Du m�ste v�lja tv� platser f�rst.");
				} catch (IndexOutOfBoundsException ioe) {
					JOptionPane.showMessageDialog(null, "Du m�ste v�lja tv� platser f�rst.");
					return;
				}
			}
		}
	}

	/**
	 * Lyssnare f�r knappen "�ndra f�rbindelse"
	 */
	class ChangeConnectionListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			int cTime;
			try {
				if (!checkSelection() || checkConnection()) { return; }
				int chosenOption = JOptionPane.showConfirmDialog(null, createChangeConnectionWindow(), "�ndra f�rbindelse", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (chosenOption == JOptionPane.OK_OPTION) {
					cTime = Integer.parseInt(tField.getText());
					listGraph.setConnectionWeight(from, to, cTime);
					JOptionPane.showMessageDialog(null, "Tiden p� f�rbindelsen mellan " + from + " och " + to + " �ndrades till " + getConnection.getWeight() + " minuter.");
					savedState = false;
				}
			} catch (IllegalStateException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Fel format p� tiden.");
			} catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Det finns ingen f�rbindelse att �ndra mellan platserna.");
			}
		}
	}
	
	/**
	 * Skapar "�ndra f�rbindelse"-f�nstret
	 * @return
	 */
	private JPanel createChangeConnectionWindow() { 
		JPanel messagePanel = new JPanel();
		getConnection = listGraph.getEdgeBetween(from, to);
		cLabel = new JLabel("F�rbindelsen fr�n " + from + " till " + to + ":");
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
	 * Lyssnar vart anv�ndaren klickar och skapar en ny plats d�r.
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
				if (nField.getText().isEmpty()) { JOptionPane.showMessageDialog(null, "Platsens namn �r f�r kort."); } else { break; }
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
	 * Ser till att r�tt komponent i jOptionPane blir fokuserat.
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
	 * �ndrar f�rg och selektion p� platser.
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
	 * Skapar "Ny f�rbindelse"-f�nstret
	 * @return
	 */
	private JPanel createNewConnectionWindow() { 
		JPanel messagePanel = new JPanel();
		getConnection = listGraph.getEdgeBetween(from, to);
		cLabel = new JLabel("F�rbindelsen fr�n " + from + " till " + to + ":");
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
	 * Kontrollerar den valda f�rbindelsen finns.
	 * @return
	 */
	private boolean checkConnection() {
		getConnection = listGraph.getEdgeBetween(from, to);
		if (getConnection != null) { return false; }
		return true;
	}

	/**
	 * G�r knappar oklickbara.
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
	 * G�r knappar klickbara och l�gger till lyssnare.
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
