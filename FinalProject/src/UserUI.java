import javax.swing.*;

public class UserUI {
	public UserUI (User user) {
		if (user instanceof NullUser)
			JOptionPane.showMessageDialog(null,String.format("Invalid Username / Password"));
		else if (! user.isValid())
			JOptionPane.showMessageDialog(null,String.format("You are under state:%s\nContact admin.",user.getState().toString()));
		else if (user instanceof BookingUser)
			new BookingUserUI((BookingUser)user);
		else if (user instanceof BusOperator)
			new BusOperatorUI((BusOperator)user);
		else if (user instanceof AdminUser)
			new AdminUserUI((AdminUser)user);
	}

}
