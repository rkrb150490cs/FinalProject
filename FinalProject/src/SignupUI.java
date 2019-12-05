
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class SignupUI {
	private int windowSize_x = 400;
	private int windowSize_y = 300;
	private JFrame signupWindow;
	private LabelledTextFieldPanel firstNamePanel;
	private LabelledTextFieldPanel lastNamePanel;
	private LabelledTextFieldPanel emailPanel;
	private LabelledTextFieldPanel usernamePanel;
	private LabelledPasswordFieldPanel passwordPanel;
	private JRadioButton bookingUser;
	private JRadioButton busOperator;
	private ButtonGroup userType;
	private JButton signupButton;
	
	private JPanel createUserTypePanel() {
		JPanel userTypePanel = new JPanel();
		userType = new ButtonGroup();
		bookingUser = new JRadioButton("Booking User",true);
		busOperator = new JRadioButton("Bus Operator");
		userType.add(bookingUser);
		userType.add(busOperator);
		userTypePanel.add(bookingUser);
		userTypePanel.add(busOperator);
		return userTypePanel;
	}
	
	private class SignupActionListener implements ActionListener{
		private UserAuthentication userAuthentication;
		
		public SignupActionListener(UserAuthentication userAuthentication) {
			this.userAuthentication = userAuthentication;
		}
		
		public void actionPerformed(ActionEvent signupEvent) {
			String firstName = firstNamePanel.getValue();
			String lastName = lastNamePanel.getValue();
			String email = emailPanel.getValue();
			String username = usernamePanel.getValue();
			String password = passwordPanel.getValue();
			ButtonModel selectedType =  userType.getSelection();
	
			try {
			if (selectedType.equals(bookingUser.getModel()))
				userAuthentication.signup(new BookingUser(userAuthentication,new Name(firstName, lastName), email,username,password));
			else 		
				userAuthentication.signup(new BusOperator(userAuthentication,new Name(firstName, lastName), email,username,password));
			JOptionPane.showMessageDialog(signupWindow,"Sign up successfull.");
			new LoginUI(userAuthentication);
			signupWindow.dispose();
			}catch (InvalidUserException exception) {
				JOptionPane.showMessageDialog(signupWindow,exception.getMessage());
			}
		}
	}
	
	public SignupUI(UserAuthentication userAuthentication){ 
		signupWindow = new JFrame ("Signup");
		
		JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new BoxLayout(windowPanel,BoxLayout.Y_AXIS));
		
		firstNamePanel = new LabelledTextFieldPanel("First Name ",20);
		lastNamePanel = new LabelledTextFieldPanel("Last Name ",20);
		emailPanel = new LabelledTextFieldPanel("Email ",20);
		usernamePanel = new LabelledTextFieldPanel("Username ",20);
		passwordPanel = new LabelledPasswordFieldPanel("Password ",20);
		
		JPanel userTypePanel = createUserTypePanel();

		signupButton = new JButton("Signup");
		signupButton.addActionListener(new SignupActionListener(userAuthentication));
		
		windowPanel.add(firstNamePanel);
		windowPanel.add(lastNamePanel);
		windowPanel.add(emailPanel);
		windowPanel.add(usernamePanel);
		windowPanel.add(passwordPanel);
		windowPanel.add(userTypePanel);
		windowPanel.add(signupButton);
		
		signupWindow.add(windowPanel);
		signupWindow.setLayout(new FlowLayout());
		signupWindow.setSize(windowSize_x, windowSize_y);
		signupWindow.setLocationRelativeTo(null);
		signupWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		signupWindow.setVisible(true);
	}







}
