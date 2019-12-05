
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class ResetPasswordUI{
	private JFrame resetPasswordWindow;
	private JPasswordField oldPassword;
	private JPasswordField newPassword;
	private int windowSize_x = 400;
	private int windowSize_y = 300;
	
	
	public ResetPasswordUI(User user) {
		
		oldPassword = new JPasswordField(20);
		newPassword = new JPasswordField(20);
		JButton resetButton = new JButton("Reset");
		
		JPanel oldPasswordPanel = new JPanel();
		oldPasswordPanel.setLayout(new BoxLayout(oldPasswordPanel,BoxLayout.X_AXIS));
		oldPasswordPanel.add(new JLabel("Old Password"));
		oldPasswordPanel.add(oldPassword);
		
		JPanel newPasswordPanel = new JPanel();
		newPasswordPanel.setLayout(new BoxLayout(newPasswordPanel,BoxLayout.X_AXIS));
		newPasswordPanel.add(new JLabel("New Password"));
		newPasswordPanel.add(newPassword);
		
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.add(oldPasswordPanel);
		mainPanel.add(newPasswordPanel);
		mainPanel.add(resetButton);
		
		resetButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent resetEvent) {
						String oldPasswordString = String.valueOf(oldPassword.getPassword());
						String newPasswordString = String.valueOf(newPassword.getPassword());
						
						try {
							String message;
							if (user.verifyPassword(oldPasswordString)) {
								user.resetPassword(newPasswordString);
								message = "Password updated Successfully";
								resetPasswordWindow.dispose();
							}else
								message = "Invalid Password Entered";
							JOptionPane.showMessageDialog(null, message);
						}catch(InvalidUserException exception) {
							JOptionPane.showMessageDialog(null, exception.getMessage());
						}
					}
				}
				);
		
		
		resetPasswordWindow = new JFrame("Reset Password");
		resetPasswordWindow.add(mainPanel);
		resetPasswordWindow.setLayout(new FlowLayout());
		resetPasswordWindow.setSize(windowSize_x, windowSize_y);
		resetPasswordWindow.setLocationRelativeTo(null);
		resetPasswordWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		resetPasswordWindow.setVisible(true);
		
	}
}


public class ProfileUI {
	
	private User user;
	private JFrame profileWindow;
	private LabelledTextFieldPanel firstNamePanel;
	private LabelledTextFieldPanel lastNamePanel;
	private LabelledTextFieldPanel emailPanel;
	private JButton saveButton;
	private JButton editButton;
	private int windowSize_x = 500;
	private int windowSize_y = 300;
	private class SaveButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent saveEvent) {
			String newFirstName = firstNamePanel.getValue();
			String newLastName = lastNamePanel.getValue();
			String newEmail = emailPanel.getValue();
			try {
			user.updateDetails(new Name(newFirstName,newLastName), newEmail);
			firstNamePanel.setEditable(false);
			lastNamePanel.setEditable(false);
			emailPanel.setEditable(false);
			editButton.setVisible(false);
			saveButton.setVisible(false);
			editButton.setVisible(true);
			}catch(InvalidUserException exception) {
				JOptionPane.showMessageDialog(profileWindow, exception.getMessage());
			}
		}
	}
	private class EditButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent editEvent) {
			firstNamePanel.setEditable(true);
			lastNamePanel.setEditable(true);
			emailPanel.setEditable(true);
			editButton.setVisible(false);
			saveButton.setVisible(true);
		}
	}
	
	
	public ProfileUI(User user) {
		this.user = user;
		profileWindow = new JFrame();
		
		firstNamePanel = new LabelledTextFieldPanel("First Name ",20,user.getFirstName());
		lastNamePanel = new LabelledTextFieldPanel("Last Name ",20,user.getLastName());
		emailPanel = new LabelledTextFieldPanel("Email ",20,user.getEmail());
		firstNamePanel.setEditable(false);
		lastNamePanel.setEditable(false);
		emailPanel.setEditable(false);
		
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new BoxLayout(dataPanel,BoxLayout.Y_AXIS));
		dataPanel.add(firstNamePanel);
		dataPanel.add(lastNamePanel);
		dataPanel.add(emailPanel);
		
		
		saveButton = new JButton("Save Changes");
		saveButton.setVisible(false);
		editButton = new JButton("Edit Details");
		
		saveButton.addActionListener(new SaveButtonActionListener());
		editButton.addActionListener(new EditButtonActionListener());
		
		JButton resetPasswordButton = new JButton("Reset Password");
		resetPasswordButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent resetEvent) {
						new ResetPasswordUI(user);
					}
				}
				);
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(saveButton);
		buttonPanel.add(editButton);
		buttonPanel.add(resetPasswordButton);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		
		mainPanel.add(dataPanel);
		mainPanel.add(buttonPanel);
		
		profileWindow.add(mainPanel);
		profileWindow.setLayout(new FlowLayout());
		profileWindow.setSize(windowSize_x, windowSize_y);
		profileWindow.setLocationRelativeTo(null);
		profileWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		profileWindow.setVisible(true);

		
	}
}
