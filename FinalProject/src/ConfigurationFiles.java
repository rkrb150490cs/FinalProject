
public class ConfigurationFiles {
	public static final String bookingUsersfileLocation = "BookingUsers.txt";
	public static final String busOperatorsfileLocation = "BusOperators.txt";
	public static final String adminUsersfileLocation = "AdminUsers.txt";
	public static final String busRoutesFileLocation = "Locations.txt";
	public static final String busFileLocation = "Bus.txt";
	public static final String ticketFileLocation = "Tickets.txt";
}


class UtilityClass{
	public static LocationManager locationManager = new LocationManager(new LocationFileStorage());
	public static UserAuthentication userAuthenticator = new UserAuthentication(new UserFileStorage());
	public static TicketManagement ticketManagement = new TicketManagement(new TicketFileStorage());
	public static BusManagement busManagement = new BusManagement(new BusFileStorage());
}
