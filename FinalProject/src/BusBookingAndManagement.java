
public class BusBookingAndManagement {
	
	private static UserAuthentication authenticator = UtilityClass.userAuthenticator;

	
	public static void main(String args[]) {
		
		try {
		authenticator.signup(new Name("Rohan","RohanM"), "rohan@123.com", "rohanphi", "rohanphi",1);
		authenticator.signup(new Name("Mathew", "Mathewq"), "mathew@123.com", "Mathewphi", "Mathewphi",1);
		authenticator.signup(new Name("Raman","Ramanq"), "Raman@123.com", "Ramanphi", "Ramanphi",2);
		}
		catch (InvalidUserException e) {}

		try {
			authenticator.signup(new AdminUser(authenticator,new Name("Admin", "Root"), "admin@123.com","admin","admin"));
		}
		catch (InvalidUserException e) {}
		
		authenticator = UtilityClass.userAuthenticator;
		new LoginUI(authenticator);
		authenticator.displayAllUsers();

		
	}
}



