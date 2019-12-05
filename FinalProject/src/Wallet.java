import java.io.Serializable;


@SuppressWarnings("serial")
public class Wallet implements Serializable{
	double balanceAmount;
	
	public Wallet() {
		this.balanceAmount = 0.0;
	}
	public double getBalance() {
		return this.balanceAmount;
	}
	public void deposit(double amount) throws WalletException{
		validateAmount(amount);
		this.balanceAmount += amount;
	}
	public boolean isWithdrawable(double amount) throws WalletException{
		validateAmount(amount);
		return (this.balanceAmount - amount) >= 0.0;
	}
	public void withdraw(double amount) throws WalletException{
		validateAmount(amount);
		if (isWithdrawable(amount))
			this.balanceAmount -= amount;
		else
			throw new InsufficientBalanceException();
	}
	public void makePayment(Wallet recipientWallet,double amount) throws WalletException{
		validateAmount(amount);
		if (!isWithdrawable(amount))
			throw new InsufficientBalanceException();
		else {
		this.balanceAmount -= amount;
		recipientWallet.deposit(amount);
		}
	}
	
	public static void validateAmount(Double amount) throws WalletException{
		if (amount < 0)
			throw new InvalidAmountException();
		}
	}

@SuppressWarnings("serial")
class WalletException extends Exception{
	public WalletException(String exceptionMessage) {
		super(exceptionMessage);
	}
}

@SuppressWarnings("serial")
class InsufficientBalanceException extends WalletException{
	public InsufficientBalanceException() {
		super("Insufficient Funds, Cannot withdraw.");
	}
}

@SuppressWarnings("serial")
class InvalidAmountException extends WalletException{
	public InvalidAmountException() {
		super("Invalid amount, Enter positive values only.");
	}
}

   