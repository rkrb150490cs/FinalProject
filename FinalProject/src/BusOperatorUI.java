
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;
import java.awt.event.ActionEvent;

public class BusOperatorUI {
	private BusOperator user;
	private JFrame mainWindow;

	private class AddBusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent addBusEvent) {
			new AddBusUI(user);
		}
	}
	private class ViewBusesActionListener implements ActionListener{
		public void actionPerformed(ActionEvent viewBusesEvent) {
			new ViewBussesUI(user);
		}
	}
	private class ViewWalletActionListener implements ActionListener{
		public void actionPerformed(ActionEvent viewWalletEvent) {
			new WalletUI(user);
		}
	}
	private class ViewProfileActionListener implements ActionListener{
		public void actionPerformed(ActionEvent viewProfileEvent) {
			new ProfileUI(user);
		}
	}

	public BusOperatorUI (BusOperator user) {
		this.user = user;
		mainWindow = new JFrame();
		
		JLabel welcomeMessage = new JLabel(String.format("Welcome %s,", user.getName()));
		
		JButton addBusButton = new JButton("Add new bus");
		JButton viewBusesButton = new JButton("View all busses");
		JButton viewWalletButton = new JButton("View Wallet");
		JButton viewProfileButton = new JButton("View Profile");
		
		addBusButton.addActionListener(new AddBusActionListener());
		viewBusesButton.addActionListener(new ViewBusesActionListener());
		viewWalletButton.addActionListener(new ViewWalletActionListener());
		viewProfileButton.addActionListener(new ViewProfileActionListener());

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3,2,20,20));
		buttonsPanel.add(addBusButton);
		buttonsPanel.add(viewBusesButton);
		buttonsPanel.add(viewWalletButton);
		buttonsPanel.add(viewProfileButton);
		
		mainWindow.add(welcomeMessage);
		mainWindow.add(buttonsPanel);
		
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}

class AddBusUI{
	private BusOperator operator;
	private JFrame mainWindow;
	private LabelledTextFieldPanel busNamePanel;
	private LabelledTextFieldPanel busNumberPanel;
	private JRadioButton acBus;
	private JRadioButton nonAcBus;
	private JRadioButton sleeper;
	private JRadioButton semisleeper;
	private ButtonGroup busType;
	
	private JPanel routesPanel;
	private LinkedList<String> locations = UtilityClass.locationManager.getAllLocations();
	
	private class AddIntermediateStopActionListener implements ActionListener{
		private int count;
		public AddIntermediateStopActionListener() {
			count = 0;
		}
		public void actionPerformed(ActionEvent event) {
			count++;
			JPanel intermediateLocationPanel = new JPanel();
			intermediateLocationPanel.setLayout(new BoxLayout(intermediateLocationPanel,BoxLayout.X_AXIS));
			LabelledTextFieldPanel intermediateLocationFarePanel = new LabelledTextFieldPanel("Fare from source", 10);
			intermediateLocationPanel.add(new LabelledComboBoxPanel(String.format("Intermediate %d",count),locations));
			intermediateLocationPanel.add(intermediateLocationFarePanel);
			intermediateLocationPanel.add(new TimePanel());
			routesPanel.add(intermediateLocationPanel,count);
			routesPanel.revalidate();
			routesPanel.repaint();
			mainWindow.pack();
		}
	}
	private class AddBusButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String busName = busNamePanel.getValue();
			String plateNumber = busNumberPanel.getValue();
			ButtonModel selectedType =  busType.getSelection();

			BusStop busStop;
			LinkedList<BusStop> busRoute = new LinkedList<BusStop>();
			for (Component component : routesPanel.getComponents()) {
				JPanel locationAndFarePanel = ((JPanel)component);
				try {
					if (locationAndFarePanel.getComponentCount() == 2) {
						String location = (String)((LabelledComboBoxPanel)locationAndFarePanel.getComponent(0)).getSelectedItem();
						SimpleTime time = (SimpleTime)((TimePanel)locationAndFarePanel.getComponent(1)).getSelectedTime();
						busStop = new BusStop(location,time);
					}
					else {
						String location = (String)((LabelledComboBoxPanel)locationAndFarePanel.getComponent(0)).getSelectedItem();
						double fare = Double.parseDouble((String)((LabelledTextFieldPanel)locationAndFarePanel.getComponent(1)).getValue());
						SimpleTime time = (SimpleTime)((TimePanel)locationAndFarePanel.getComponent(2)).getSelectedTime();
						busStop = new BusStop(location, fare,time);
					}
					busRoute.add(busStop);
				}
				catch(InvalidBusException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage());
				}catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(mainWindow, "Enter valid fare values(xx.xx)");
				}
			}
			
			int numberofSeats = 40;
			try {
				if (selectedType.equals(acBus.getModel())) {
					UtilityClass.busManagement.addNewBus(operator, busName, plateNumber, busRoute, numberofSeats,BusType.AC);					
				}
				else {
					UtilityClass.busManagement.addNewBus(operator, busName, plateNumber, busRoute, numberofSeats,BusType.NONAC);	
				}
				JOptionPane.showMessageDialog(mainWindow,String.format("Bus Added Successfully"));
				mainWindow.dispose();			
			}
			catch(InvalidBusException e) {
				JOptionPane.showMessageDialog(mainWindow,e.getMessage());
			}
		}
	}
	
	public AddBusUI(BusOperator user) {
		this.operator = user;
		
		mainWindow = new JFrame("Bus Operator");
		busNamePanel = new LabelledTextFieldPanel("Bus Name ",20);
		busNumberPanel = new LabelledTextFieldPanel("Bus Number ",20);
		
		JPanel busDetailsPanel = new JPanel();
		busDetailsPanel.setLayout(new BoxLayout(busDetailsPanel,BoxLayout.Y_AXIS));
		busDetailsPanel.add(busNamePanel);
		busDetailsPanel.add(busNumberPanel);
		
		JPanel busTypePanel = new JPanel();
		busType = new ButtonGroup();
		acBus = new JRadioButton("AC",true);
		nonAcBus= new JRadioButton("NON-AC");
		sleeper= new JRadioButton("SL");
		semisleeper= new JRadioButton("SSL");
		busType.add(acBus);
		busType.add(nonAcBus);
		busType.add(sleeper);
		busType.add(semisleeper);
		busTypePanel.add(acBus);
		busTypePanel.add(nonAcBus);
		busTypePanel.add(sleeper);
		busTypePanel.add(semisleeper);
		
		JLabel routeLabel = new JLabel ("Route Details");
		routesPanel = new JPanel();
		routesPanel.setLayout(new BoxLayout(routesPanel,BoxLayout.Y_AXIS));
		
		JPanel sourcePanel = new JPanel();
		sourcePanel.setLayout(new BoxLayout(sourcePanel,BoxLayout.X_AXIS));
		sourcePanel.add(new LabelledComboBoxPanel("Source",locations));
		sourcePanel.add(new TimePanel());
		routesPanel.add(sourcePanel);
		
		
		LabelledTextFieldPanel destinationFarePanel = new LabelledTextFieldPanel("Fare from source", 10);
		JPanel destinationPanel = new JPanel();
		destinationPanel.setLayout(new BoxLayout(destinationPanel,BoxLayout.X_AXIS));
		destinationPanel.add(new LabelledComboBoxPanel("Destination",locations));
		destinationPanel.add(destinationFarePanel);
		destinationPanel.add(new TimePanel());
		routesPanel.add(destinationPanel);

		
		JButton addIntermediateStopButton = new JButton("Add Intermediate Stop");
		JButton addBusButton = new JButton("Add Bus");
		addIntermediateStopButton.addActionListener(new AddIntermediateStopActionListener());
		addBusButton.addActionListener(new AddBusButtonActionListener());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.add(busDetailsPanel);
		mainPanel.add(busTypePanel);
		mainPanel.add(routeLabel);
		mainPanel.add(routesPanel);
		mainPanel.add(addIntermediateStopButton);
		mainPanel.add(addBusButton);
		
		
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}

class ViewBussesUI implements RefreshableComponent{
	private BusOperator operator;
	private JFrame mainWindow;
	private UnEditableTableModel busListTableModel;
	private JTable busListTable;
	private LinkedList<Bus> operatorBuses;
	
	private class viewBusDetailsActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
				int selectedIndex = busListTable.getSelectedRow();
				Bus selectedBus = operatorBuses.get(selectedIndex);
				new ViewBusUI(ViewBussesUI.this,selectedBus);
			}
			catch(IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(mainWindow, "Select a bus from the list to view");
			}
		}
	}
	private class ScheduleBusesActionListener implements ActionListener{
		public void actionPerformed(ActionEvent viewProfileEvent) {
			try {
			int selectedIndex = busListTable.getSelectedRow();
			Bus selectedBus = operatorBuses.get(selectedIndex);
			new ScheduledBusesUI(selectedBus);
			}
			catch(IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(mainWindow, "Select a bus from the list to schedule");
			}
		}
	}
	
	public ViewBussesUI(BusOperator user) {
		this.operator  = user;
		
		String[] columnHeaderNames = {"Bus Name", "Plate Number","Bus Type"};
		Vector<String> columnNamesVector = new Vector<String>(Arrays.asList(columnHeaderNames));
		
		operatorBuses = UtilityClass.busManagement.getAllBusses(operator);
		Vector<Vector<String>> rowDataVector = new Vector<Vector<String>>();
		for (Bus bus : operatorBuses) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(bus.getBusName());
			columnDataVector.add(bus.getPlateNumber());
			columnDataVector.add(bus.getBusType().toString());
			rowDataVector.add(columnDataVector);
		}
		busListTableModel = new UnEditableTableModel(rowDataVector,columnNamesVector);
		busListTable = new JTable(busListTableModel);
		busListTable.setRowSelectionAllowed(true);
		JScrollPane busListPane = new JScrollPane(busListTable);

		JButton viewBusDetailsButton = new JButton("View bus details");
		viewBusDetailsButton.addActionListener(new viewBusDetailsActionListener());
		JButton scheduleBusesButton = new JButton("View Schedule Buses");
		scheduleBusesButton.addActionListener(new ScheduleBusesActionListener());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(viewBusDetailsButton);
		buttonPanel.add(scheduleBusesButton);
		
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
		operatorBuses = UtilityClass.busManagement.getAllBusses(operator);
		busListTableModel.setRowCount(0);
		
		for (Bus bus : operatorBuses) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(bus.getBusName());
			columnDataVector.add(bus.getPlateNumber());
			columnDataVector.add(bus.getBusType().toString());
			busListTableModel.addRow(columnDataVector);
		}
		mainWindow.revalidate();
		mainWindow.repaint();
		mainWindow.pack();
	}
}

class ViewBusUI{
	private Bus bus;
	private RefreshableComponent callingWindow;
	private JFrame mainWindow;
	private LabelledTextFieldPanel busNamePanel;
	private LabelledTextFieldPanel busNumberPanel;
	private JPanel routesPanel;
	private LinkedList<String> locations = UtilityClass.locationManager.getAllLocations();
	private JButton editButton;
	private JButton addIntermediateStopButton;
	private LabelledTextFieldPanel intermediateStopInput;
	private JButton saveBusButton;
	
	private class AddIntermediateStopActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
				int stopLocation = Integer.parseInt(intermediateStopInput.getValue());
				if (stopLocation <= 0)
					throw new IllegalArgumentException();
				JPanel intermediateLocationPanel = new JPanel();
				intermediateLocationPanel.setLayout(new BoxLayout(intermediateLocationPanel,BoxLayout.X_AXIS));
				LabelledTextFieldPanel intermediateLocationFarePanel = new LabelledTextFieldPanel("Fare from source", 10);
				TimePanel intermediateLocationTimePanel = new TimePanel();
				intermediateLocationPanel.add(new LabelledComboBoxPanel("Intermediate #",locations));
				intermediateLocationPanel.add(intermediateLocationFarePanel);
				intermediateLocationPanel.add(intermediateLocationTimePanel);
				
				routesPanel.add(intermediateLocationPanel,stopLocation);
				routesPanel.revalidate();
				routesPanel.repaint();
				mainWindow.pack();
			}
			catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(mainWindow, "Enter valid integer as stop position.");
			}
			catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(mainWindow, "Enter valid integer, within range, as the stop position.");
			}
		}
	}


	private class EditActionListener implements ActionListener{
	public void actionPerformed(ActionEvent event) {
		busNamePanel.setEditable(true);
				
		editButton.setVisible(false);
		addIntermediateStopButton.setVisible(true);
		intermediateStopInput.setVisible(true);
		saveBusButton.setVisible(true);
		mainWindow.pack();
	}
}
	private class SaveBusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String busName = busNamePanel.getValue();

			BusStop busStop;
			LinkedList<BusStop> busRoute = new LinkedList<BusStop>();
			for (Component component : routesPanel.getComponents()) {
				JPanel locationAndFarePanel = ((JPanel)component);
				try {
					if (locationAndFarePanel.getComponentCount() == 2) {
						String location = (String)((LabelledComboBoxPanel)locationAndFarePanel.getComponent(0)).getSelectedItem();
						SimpleTime time = (SimpleTime)((TimePanel)locationAndFarePanel.getComponent(1)).getSelectedTime();
						busStop = new BusStop(location, time);
					}
					else {
						String location = (String)((LabelledComboBoxPanel)locationAndFarePanel.getComponent(0)).getSelectedItem();
						double fare = Double.parseDouble((String)((LabelledTextFieldPanel)locationAndFarePanel.getComponent(1)).getValue());
						SimpleTime time = (SimpleTime)((TimePanel)locationAndFarePanel.getComponent(2)).getSelectedTime();
						busStop = new BusStop(location, fare, time);
					}
					busRoute.add(busStop);
				}
				catch(InvalidBusException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage());
					return;
				}
				catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(mainWindow, "Enter Valid values.");
					return;
				}
				
				
			}
			
			try {
				bus.updateBus(busName, busRoute);
				UtilityClass.busManagement.updateBus(bus);
				JOptionPane.showMessageDialog(mainWindow,String.format("Bus Saved Successfully"));
				callingWindow.refresh();
				mainWindow.dispose();
			}
			catch(InvalidBusException e) {
				JOptionPane.showMessageDialog(mainWindow,e.getMessage());
			}
		}
	}
	
	public ViewBusUI(Bus bus) {
		this(new NullRefreshableComponent(), bus);
	}
	public ViewBusUI(RefreshableComponent callingWindow, Bus bus) {
		this.bus = bus;
		this.callingWindow = callingWindow;
		
		mainWindow = new JFrame("Bus Operator");
		busNamePanel = new LabelledTextFieldPanel("Bus Name ",20,bus.getBusName());
		busNamePanel.setEditable(false);
		busNumberPanel = new LabelledTextFieldPanel("Bus Number ",20, bus.getPlateNumber());
		busNumberPanel.setEditable(false);
		
		JPanel busDetailsPanel = new JPanel();
		busDetailsPanel.setLayout(new BoxLayout(busDetailsPanel,BoxLayout.Y_AXIS));
		busDetailsPanel.add(busNamePanel);
		busDetailsPanel.add(busNumberPanel);
		
		
		JLabel routeLabel = new JLabel ("Route Details");
		routesPanel = new JPanel();
		routesPanel.setLayout(new BoxLayout(routesPanel,BoxLayout.Y_AXIS));
		
		LinkedList<BusStop> busRoute = bus.getBusRoute();
		
		BusStop source = busRoute.get(0);
		JPanel sourcePanel = new JPanel();
		sourcePanel.setLayout(new BoxLayout(sourcePanel,BoxLayout.X_AXIS));
		LabelledComboBoxPanel sourceComboBox = new LabelledComboBoxPanel("Source",locations,source.getLocation());
		sourceComboBox.setEditable(false);
		TimePanel timePanel = new TimePanel();
		timePanel.setSelectedTime(source.getTime());
		timePanel.setEditable(false);
		sourcePanel.add(sourceComboBox);
		sourcePanel.add(timePanel);
		routesPanel.add(sourcePanel);
		
		busRoute.remove(0);
		for(BusStop stop : busRoute) {
			LabelledTextFieldPanel farePanel = new LabelledTextFieldPanel("Fare from source", 10,Double.toString(stop.getFareFromSource()));
			farePanel.setEditable(false);
			JPanel locationPanel = new JPanel();
			locationPanel.setLayout(new BoxLayout(locationPanel,BoxLayout.X_AXIS));
			LabelledComboBoxPanel locationComboBox = new LabelledComboBoxPanel("Stop : #",locations,stop.getLocation());
			locationComboBox.setEditable(false);
			TimePanel locationtimePanel = new TimePanel();
			locationtimePanel.setSelectedTime(stop.getTime());
			locationtimePanel.setEditable(false);
			locationPanel.add(locationComboBox);
			locationPanel.add(farePanel);
			locationPanel.add(locationtimePanel);
			routesPanel.add(locationPanel);
		}
		
		editButton = new JButton("Edit details");
		editButton.addActionListener(new EditActionListener());
		
		intermediateStopInput = new LabelledTextFieldPanel("Stop location",10);
		intermediateStopInput.setVisible(false);
		addIntermediateStopButton = new JButton("Add Intermediate Stop");
		addIntermediateStopButton.setVisible(false);
		addIntermediateStopButton.addActionListener(new AddIntermediateStopActionListener());
		JPanel intermediateStopPanel = new JPanel();
		intermediateStopPanel.setLayout(new BoxLayout(intermediateStopPanel,BoxLayout.X_AXIS));
		intermediateStopPanel.add(intermediateStopInput);
		intermediateStopPanel.add(addIntermediateStopButton);
		
		saveBusButton = new JButton("Save Bus details");
		saveBusButton.setVisible(false);
		saveBusButton.addActionListener(new SaveBusActionListener());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.add(busDetailsPanel);
		mainPanel.add(routeLabel);
		mainPanel.add(routesPanel);
		mainPanel.add(editButton);
		mainPanel.add(intermediateStopPanel);
		mainPanel.add(saveBusButton);
		
		
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}

class ScheduledBusesUI implements RefreshableComponent{
	private JFrame mainWindow;
	private JTable datelistTable;
	private DefaultTableModel datelistTableModel;
	private Bus bus;
	private LinkedList<ScheduledBus> scheduledBuses;
	
	private class ScheduleNewBusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			new ScheduleNewBusUI((RefreshableComponent)ScheduledBusesUI.this,bus);
		}
	}
	@SuppressWarnings("serial")
	public ScheduledBusesUI(Bus bus) {
		this.bus = bus;
		scheduledBuses = bus.getScheduledBuses();
		
		
		String[] columnHeaderNames = {"Date of Travel"};
		Vector<String> columnNamesVector = new Vector<String>(Arrays.asList(columnHeaderNames));
		
		Vector<Vector<String>> rowDataVector = new Vector<Vector<String>>();
		for (ScheduledBus scheduledBus : scheduledBuses) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(scheduledBus.getDate().toString());
			rowDataVector.add(columnDataVector);
		}
		datelistTableModel = new DefaultTableModel(rowDataVector,columnNamesVector);
		datelistTable = new JTable(datelistTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		datelistTable.setRowSelectionAllowed(true);
		JScrollPane datelistPane = new JScrollPane(datelistTable);

		JButton scheduleNewBusButton = new JButton("Schedule New Bus");
		scheduleNewBusButton.addActionListener(new ScheduleNewBusActionListener());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(datelistPane);
		mainPanel.add(scheduleNewBusButton);
		
		mainWindow = new JFrame("Bus Schedule");
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);	
	}

	public void refresh() {
		scheduledBuses = bus.getScheduledBuses();
		
		datelistTableModel.setRowCount(0);
		for (ScheduledBus scheduledBus : scheduledBuses) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(scheduledBus.getDate().toString());
			datelistTableModel.addRow(columnDataVector);
		}

		mainWindow.revalidate();
		mainWindow.repaint();
		mainWindow.pack();
	}
}

class ScheduleNewBusUI{
	private JFrame mainWindow;
	private RefreshableComponent callingWindow;
	private CalendarPanel calendarPanel;
	private Bus bus;
	
	private class OKButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
			SimpleDate selectedDate = calendarPanel.getValue();
			if (new SimpleDate.DateComparator().compare(selectedDate, SimpleDate.getCurrentDate()) <= 0) {
				JOptionPane.showMessageDialog(null,"Cannot book ticket for selected date.\nBook tickets for dates after today.");
				return;
			}
			
			bus.scheduleNewBus(selectedDate);
			UtilityClass.busManagement.updateBus(bus);
			callingWindow.refresh();
			mainWindow.dispose();
			}
			catch(InvalidDateException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage());
			}
			catch(ScheduleBusException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage());
			}
		}
	}
	
	public ScheduleNewBusUI(Bus bus) {
		this(new NullRefreshableComponent(),bus);
	}
	
	public ScheduleNewBusUI(RefreshableComponent callingWindow,Bus bus) {
		this.callingWindow = callingWindow;
		this.bus = bus;
		calendarPanel = new CalendarPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonActionListener());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(calendarPanel);
		mainPanel.add(okButton);
				
		mainWindow = new JFrame("Enter Date");
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
		 
	}
}

