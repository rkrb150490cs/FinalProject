import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class LoginUI {
	
	private UserAuthentication userAuthentication;
	private JFrame loginWindow;
	private LabelledTextFieldPanel usernamePanel;
	private LabelledPasswordFieldPanel passwordPanel;
	private static int windowSize_x = 400;
	private static int windowSize_y = 150;
	
	private class LoginActionListener implements ActionListener{
		public void actionPerformed(ActionEvent loginEvent) {
			String username = usernamePanel.getValue();
			String password = passwordPanel.getValue();
			User user = userAuthentication.login(username,password);
			loginWindow.dispose();
			new UserUI(user);
		}
	}

	private class SignupActionListener implements ActionListener{
		public void actionPerformed(ActionEvent loginEvent) {
			new SignupUI(userAuthentication);
			loginWindow.dispose();
		}
	}

	public LoginUI (UserAuthentication userAuthentication) {
		this.userAuthentication = userAuthentication;
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		
		usernamePanel = new LabelledTextFieldPanel("Username", 20);
		passwordPanel = new LabelledPasswordFieldPanel("Password",20);
		
		JButton login = new JButton("Login");		
		login.addActionListener(new LoginActionListener());

		JButton signup = new JButton("Signup");
		signup.addActionListener(new SignupActionListener());

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
		buttonsPanel.add(login);
		buttonsPanel.add(signup);
		
		mainPanel.add(usernamePanel);
		mainPanel.add(passwordPanel);
		mainPanel.add(buttonsPanel);
		
		loginWindow = new JFrame("Login");
		loginWindow.add(mainPanel);
		loginWindow.setLayout(new FlowLayout());
		loginWindow.setSize(windowSize_x, windowSize_y);
		loginWindow.setLocationRelativeTo(null);
		loginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginWindow.setVisible(true);
	}

}



