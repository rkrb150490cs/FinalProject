import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class AdminUserUI {
	private JFrame mainWindow;

	private class ViewLocationsActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			new ViewLocationsUI();
		}
	}
	private class ViewAllUsersActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			new ViewAllUsersUI();
		}
	}
	
	public AdminUserUI(AdminUser user) {
		
		JLabel welcomeMessage = new JLabel(String.format("Welcome %s,", user.getName()));
		
		JButton viewLocationsButton = new JButton("View all Locations");
		viewLocationsButton.addActionListener(new ViewLocationsActionListener());
		JButton viewAllUsersButton = new JButton("View all users");
		viewAllUsersButton.addActionListener(new ViewAllUsersActionListener());
				
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1,2,20,20));
		buttonsPanel.add(viewLocationsButton);
		buttonsPanel.add(viewAllUsersButton);
		
		mainWindow = new JFrame();

		mainWindow.add(welcomeMessage);
		mainWindow.add(buttonsPanel);
		
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);

		
	}
}

class ViewLocationsUI {
	private JFrame mainWindow;
	private JTable locationsListTable;
	private DefaultTableModel locationsListTableModel;
	private LinkedList<String> allLocations;
	
	private class AddNewLocationActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String newLocation = JOptionPane.showInputDialog("Enter new Location");
			try {
				UtilityClass.locationManager.addLocation(newLocation);
				refresh();
			} catch (InvalidLocationStringException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("serial")
	public ViewLocationsUI() {
			
		allLocations = UtilityClass.locationManager.getAllLocations();

		String[] columnHeaderNames = {"Locations"};
		Vector<String> columnNamesVector = new Vector<String>(Arrays.asList(columnHeaderNames));
		
		Vector<Vector<String>> rowDataVector = new Vector<Vector<String>>();
		for (String location : allLocations) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(location);
			rowDataVector.add(columnDataVector);
		}
		locationsListTableModel = new DefaultTableModel(rowDataVector,columnNamesVector);
		locationsListTable = new JTable(locationsListTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		locationsListTable.setRowSelectionAllowed(false);
		JScrollPane datelistPane = new JScrollPane(locationsListTable);

		JButton addNewLocationButton = new JButton("Add new location");
		addNewLocationButton.addActionListener(new AddNewLocationActionListener());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(datelistPane);
		mainPanel.add(addNewLocationButton);
		
		mainWindow = new JFrame("Locations");
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);	
	}

	public void refresh() {
		allLocations = UtilityClass.locationManager.getAllLocations();
		
		locationsListTableModel.setRowCount(0);
		for (String location : allLocations) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(location);
			locationsListTableModel.addRow(columnDataVector);
		}

		mainWindow.revalidate();
		mainWindow.repaint();
		mainWindow.pack();
	}
}


class ViewAllUsersUI{
	private JFrame mainWindow;
	private UnEditableTableModel userListTableModel;
	private JTable userListTable;
	private LinkedList<User> allUsers;
	
	private class BlockUserActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
				int selectedIndex = userListTable.getSelectedRow();
				User selectedUser = allUsers.get(selectedIndex);
				selectedUser.block();
				refresh();
			}
			catch(StateException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage());
			}
			catch(IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(mainWindow, "Select a User from the list to view");
			}
		}
	}
	private class UnBlockUserActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
				int selectedIndex = userListTable.getSelectedRow();
				User selectedUser = allUsers.get(selectedIndex);
				selectedUser.validate();
				refresh();
			}
			catch(StateException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage());
			}
			catch(IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(mainWindow, "Select a User from the list to view");
			}
		}
	}
	
	public ViewAllUsersUI() {
		
		String[] columnHeaderNames = {"Name","Username", "Status"};
		Vector<String> columnNamesVector = new Vector<String>(Arrays.asList(columnHeaderNames));
		
		this.allUsers = UtilityClass.userAuthenticator.getAllUsers();
		Vector<Vector<String>> rowDataVector = new Vector<Vector<String>>();
		for (User user : allUsers) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(user.getName());
			columnDataVector.add(user.getUsername());
			columnDataVector.add(user.getState().toString());
			rowDataVector.add(columnDataVector);
		}
		userListTableModel = new UnEditableTableModel(rowDataVector,columnNamesVector);
		userListTable = new JTable(userListTableModel);
		userListTable.setRowSelectionAllowed(true);
		JScrollPane busListPane = new JScrollPane(userListTable);
	
		JButton blockUserButton = new JButton("Block User");
		blockUserButton.addActionListener(new BlockUserActionListener());
		JButton unBlockUserButton = new JButton("Validate/Unblock User");
		unBlockUserButton.addActionListener(new UnBlockUserActionListener());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(blockUserButton);
		buttonPanel.add(unBlockUserButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(busListPane);
		mainPanel.add(buttonPanel);
		
		mainWindow = new JFrame("View Buses");
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}
	public void refresh() {
		this.allUsers = UtilityClass.userAuthenticator.getAllUsers();
		userListTableModel.setRowCount(0);
		
		for (User user : allUsers) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(user.getName());
			columnDataVector.add(user.getUsername());
			columnDataVector.add(user.getState().toString());
			userListTableModel.addRow(columnDataVector);
		}
		mainWindow.revalidate();
		mainWindow.repaint();
		mainWindow.pack();
	}
}
