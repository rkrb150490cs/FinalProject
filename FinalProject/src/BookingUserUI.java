
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;
import java.awt.event.ActionEvent;


public class BookingUserUI {
	private JFrame mainWindow;
	private BookingUser user;
	
	private class BookTicketActionListener implements ActionListener{
		public void actionPerformed(ActionEvent bookTicketEvent) {
			new SearchTicketUI(user);
		}
	}
	private class ViewTicketsActionListener implements ActionListener{
		public void actionPerformed(ActionEvent bookTicketEvent) {
			new ViewTicketsUI(user);
		}
	}
	private class ViewWalletActionListener implements ActionListener{
		public void actionPerformed(ActionEvent viewWalletEvent) {
			new WalletUI(user);
		}
	}
	private class ViewProfileActionListener implements ActionListener{
		public void actionPerformed(ActionEvent bookTicketEvent) {
			new ProfileUI(user);
		}
	}
	
	public BookingUserUI (BookingUser user) {
		mainWindow = new JFrame();
		this.user = user;
		
		JLabel welcomeMessage = new JLabel(String.format("Welcome %s,", user.getName()));
		
		JButton bookTicketButton = new JButton("Book Ticket");
		bookTicketButton.addActionListener(new BookTicketActionListener());
		JButton viewHistoryButton = new JButton("View Tickets");
		viewHistoryButton.addActionListener(new ViewTicketsActionListener());
		JButton viewWalletButton = new JButton("View Wallet");
		viewWalletButton.addActionListener(new ViewWalletActionListener());
		JButton viewProfileButton = new JButton("View Profile");
		viewProfileButton.addActionListener(new ViewProfileActionListener());		
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(bookTicketButton);
		buttonsPanel.add(viewHistoryButton);
		buttonsPanel.add(viewWalletButton);
		buttonsPanel.add(viewProfileButton);
		buttonsPanel.setLayout(new GridLayout(2,2,20,20));
		
		mainWindow.add(welcomeMessage);
		mainWindow.add(buttonsPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}

class SearchTicketUI {
	private JFrame mainWindow;
	private LabelledComboBoxPanel sourceLocationPanel;
	private LabelledComboBoxPanel destinationLocationPanel;
	private CalendarPanel calendarPanel;
	private BookingUser user;
	private static LinkedList<String> locations = UtilityClass.locationManager.getAllLocations();
	
	private class OKButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try
			{	
				String source = (String)sourceLocationPanel.getSelectedItem();
				String destination = (String)destinationLocationPanel.getSelectedItem();
				SimpleDate dateOfTravel = calendarPanel.getValue();
				
				if (new SimpleDate.DateComparator().compare(dateOfTravel, SimpleDate.getCurrentDate()) <= 0) {
					JOptionPane.showMessageDialog(null,"Cannot book ticket for selected date.\nBook tickets for dates after today.");
					return;
				}
				new SelectBusUI(user, source,destination,dateOfTravel);
			}
			catch(InvalidDateException e) {
				JOptionPane.showMessageDialog(null,e.getMessage());
			}
			
		}
	}
	
	public SearchTicketUI(BookingUser user) {
		this.user = user;
		
		sourceLocationPanel = new LabelledComboBoxPanel("Source Location",locations);
		destinationLocationPanel = new LabelledComboBoxPanel("Destination Location",locations);
		calendarPanel = new CalendarPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonActionListener());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(sourceLocationPanel);
		mainPanel.add(destinationLocationPanel);
		mainPanel.add(calendarPanel);
		mainPanel.add(okButton);

		mainWindow = new JFrame("Search for Tickets");
		mainWindow.add(mainPanel);
		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}

class SelectBusUI{
	
	private JFrame mainWindow;
	private UnEditableTableModel busListTableModel;
	private JTable busListTable;
	
	private BookingUser user;
	private String sourceLocation;
	private String destinationLocation;
	private LinkedList<ScheduledBus> scheduledBusses;
	
	private class SelectBusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			try {
				int selectedIndex = busListTable.getSelectedRow();
				ScheduledBus selectedBus = scheduledBusses.get(selectedIndex);
				new SelectSeatUI(user, selectedBus, sourceLocation, destinationLocation);
				mainWindow.dispose();
				}
				catch(IndexOutOfBoundsException e) {
					JOptionPane.showMessageDialog(mainWindow, "Select a bus from the list to view");
				}
		}
	}
	
	public SelectBusUI(BookingUser user, String source, String destination, SimpleDate dateOfTravel) {
		this.user = user;
		this.sourceLocation = source;
		this.destinationLocation = destination;
		
		scheduledBusses = UtilityClass.busManagement.getAvailableScheduledBusses(source,destination,dateOfTravel);	
	
		String[] columnHeaderNames = {"Bus Name", "Fare", "Departure Time", "Destination Time"};
		Vector<String> columnNamesVector = new Vector<String>(Arrays.asList(columnHeaderNames));
		
		Vector<Vector<String>> rowDataVector = new Vector<Vector<String>>();
		for (ScheduledBus scheduledBus : scheduledBusses) {
			Vector<String> columnDataVector = new Vector<String>();
			Bus bus = scheduledBus.getBus();
			columnDataVector.add(bus.getBusName());
			columnDataVector.add(bus.getBusType().toString());
			columnDataVector.add(Double.toString(bus.getFare(source, destination)));
			columnDataVector.add(bus.getTimeAtLocation(source).toString());
			columnDataVector.add(bus.getTimeAtLocation(destination).toString());
			rowDataVector.add(columnDataVector);
		}
		busListTableModel = new UnEditableTableModel(rowDataVector,columnNamesVector);
		busListTable = new JTable(busListTableModel);
		busListTable.setRowSelectionAllowed(true);
		JScrollPane busListPane = new JScrollPane(busListTable);

		JButton selectBusButton = new JButton("Select bus");
		selectBusButton.addActionListener(new SelectBusActionListener());

		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(selectBusButton);

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
}


class ViewTicketsUI implements RefreshableComponent{
	
	private JFrame mainWindow;
	private UnEditableTableModel busListTableModel;
	private JTable busListTable;
	
	private BookingUser user;
	private LinkedList<Ticket> bookedTickets;
	
	private class CancelTicketActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try{
				int selectedIndex = busListTable.getSelectedRow();
				Ticket selectedTicket = bookedTickets.get(selectedIndex);
				UtilityClass.ticketManagement.cancelTicket(selectedTicket);
				JOptionPane.showMessageDialog(mainWindow, "Ticket cancelled successfully, Amount is refunded.");
				refresh();
			}
			catch(IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(mainWindow, "Select a ticket from the list to cancel");
			}catch (CancellationException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage());
			}catch (WalletException e) {
				JOptionPane.showMessageDialog(mainWindow, "Ticket cancelled successfully, Amount will be refunded in future days.");
			}
		}
	}
	
	public ViewTicketsUI(BookingUser user) {
		this.user = user;
		
		bookedTickets = UtilityClass.ticketManagement.getAllTickets(user);

		String[] columnHeaderNames = {"Ticket ID", "Status", "Source", "Destination", "Departure Time","Destination Time","Date of travel", "Bus name", "Plate number", "Seat","Fare"};
		Vector<String> columnNamesVector = new Vector<String>(Arrays.asList(columnHeaderNames));
		
		Vector<Vector<String>> rowDataVector = new Vector<Vector<String>>();
		for (Ticket ticket : bookedTickets) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(Integer.toString(ticket.getTicketId()));
			columnDataVector.add(ticket.getTicketStatus().toString());
			columnDataVector.add(ticket.getSourceLocation());
			columnDataVector.add(ticket.getDestinationLocation());
			columnDataVector.add(ticket.getBookedBus().getBus().getTimeAtLocation(ticket.getSourceLocation()).toString());
			columnDataVector.add(ticket.getBookedBus().getBus().getTimeAtLocation(ticket.getDestinationLocation()).toString());
			columnDataVector.add(ticket.getDateOfTravel().toString());
			columnDataVector.add(ticket.getBookedBus().getBus().getBusName());
			columnDataVector.add(ticket.getBookedBus().getBus().getPlateNumber());
			columnDataVector.add(Integer.toString(ticket.getReservedSeat().getSeatNumber()));
			columnDataVector.add(Double.toString(ticket.getFare()));
			rowDataVector.add(columnDataVector);
		}
		busListTableModel = new UnEditableTableModel(rowDataVector,columnNamesVector);
		busListTable = new JTable(busListTableModel);
		busListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		busListTable.setRowSelectionAllowed(true);
		JScrollPane busListPane = new JScrollPane(busListTable);
				
		JButton cancelTicketButton = new JButton("Cancel Ticket");
		cancelTicketButton.addActionListener(new CancelTicketActionListener());

		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(cancelTicketButton);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//		mainPanel.add(busListPane,BorderLayout.CENTER);
//		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		mainWindow = new JFrame("View Buses");
//		mainWindow.setLayout(new BoxLayout(mainWindow, BoxLayout.Y_AXIS));
		mainWindow.setLayout(new BorderLayout());
		mainWindow.add(busListPane,BorderLayout.CENTER);
		mainWindow.add(buttonPanel,BorderLayout.SOUTH);
		
		
//		mainWindow.add(mainPanel);
//		mainWindow.setLayout(new FlowLayout());
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setVisible(true);
	}
	
	public void refresh() {
		bookedTickets = UtilityClass.ticketManagement.getAllTickets(user);

		busListTableModel.setRowCount(0);
		for (Ticket ticket : bookedTickets) {
			Vector<String> columnDataVector = new Vector<String>();
			columnDataVector.add(Integer.toString(ticket.getTicketId()));
			columnDataVector.add(ticket.getTicketStatus().toString());
			columnDataVector.add(ticket.getSourceLocation());
			columnDataVector.add(ticket.getDestinationLocation());
			columnDataVector.add(ticket.getBookedBus().getBus().getTimeAtLocation(ticket.getSourceLocation()).toString());
			columnDataVector.add(ticket.getBookedBus().getBus().getTimeAtLocation(ticket.getDestinationLocation()).toString());
			columnDataVector.add(ticket.getDateOfTravel().toString());
			columnDataVector.add(ticket.getBookedBus().getBus().getBusName());
			columnDataVector.add(ticket.getBookedBus().getBus().getPlateNumber());
			columnDataVector.add(Integer.toString(ticket.getReservedSeat().getSeatNumber()));
			columnDataVector.add(Double.toString(ticket.getFare()));
			busListTableModel.addRow(columnDataVector);
		}
	}
}