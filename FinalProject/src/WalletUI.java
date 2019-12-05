import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class WalletUI {
	private JFrame walletWindow;
	JLabel availableBalanceLabel;
	private int windowSize_x = 400;
	private int windowSize_y = 200;
	
	public WalletUI(ApplicationUser user) {
			walletWindow = new JFrame("Wallet details");
			availableBalanceLabel = new JLabel(String.format("Available Balance %s",user.getBalance()));
			
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridLayout(1,2,20,20));
			JButton withdrawButton = new JButton("Withdraw");
			JButton depositButton = new JButton("Deposit");
			buttonsPanel.add(withdrawButton);
			buttonsPanel.add(depositButton);
			withdrawButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent withdrawEvent) {
							String amountInString = JOptionPane.showInputDialog("Enter amount to withdraw","0");
							try {
								double amount = Double.parseDouble(amountInString);
								user.withdrawAmount(amount);
								availableBalanceLabel.setText(String.format("Available Balance %s",user.getBalance()));
							}catch(NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null, "Invalid amount, Enter in format (xxx.xxx)");
							}catch(WalletException exception) {
								JOptionPane.showMessageDialog(null, exception.getMessage());	
							}catch(Exception otherExceptions) {
							}
						}					
					}
					);
			depositButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent depositEvent) {
							 String amountInString = JOptionPane.showInputDialog("Enter amount to deposit","0");
							try {
							 double amount = Double.parseDouble(amountInString);
							 user.depositAmount(amount);
							availableBalanceLabel.setText(String.format("Available Balance %s",user.getBalance()));
							}catch(NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null, "Invalid amount, Enter in format (xxx.xxx)");
							}catch(WalletException exception) {
								JOptionPane.showMessageDialog(null, exception.getMessage());	
							}catch(Exception otherExceptions) {
							}
						}						
					}
					);
			
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
			mainPanel.add(availableBalanceLabel);
			mainPanel.add(buttonsPanel);
			
			walletWindow.add(mainPanel);
			walletWindow.setLayout(new FlowLayout());
			walletWindow.setSize(windowSize_x, windowSize_y);
			walletWindow.setLocationRelativeTo(null);
			walletWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			walletWindow.setVisible(true);
//			loginWindow.setResizable(false);
	}
}

