import java.io.Serializable;

interface UserState{
	public abstract UserState getState();
}

enum AdminState implements UserState{
	VALID,
	BLOCKED;
	public AdminState getState() {
		return this;
	}
}

enum BookingUserState implements UserState{
	VALID,
	BLOCKED;
	public BookingUserState getState() {
		return this;
	}
}

enum BusOperatorState implements UserState{
	VALID,
	BLOCKED,
	PENDING;
	public BusOperatorState getState() {
		return this;
	}
}

public abstract class User implements Serializable{
	private static final long serialVersionUID = 42L;
	private Name name;
	private String email;
	private String username;
	private String password;
	
	protected UserState state;
	private UserAuthentication userAuthentication;
	
	public User(UserAuthentication auth,Name name, String email,String username,String password) throws InvalidUserException{
		
		UserDataValidator.validateName(name);
		UserDataValidator.validateEmail(email);
		UserDataValidator.validateUsername(username);
		UserDataValidator.validatePassword(password);
		
		this.name = name;
		this.email = email;
		this.username = username;
		this.password = saltHashedPassword(password);
		this.userAuthentication = auth;
	}
	protected User() {}
	public abstract void block() throws StateException;
	public abstract void validate() throws StateException;
	public abstract boolean isValid();

	public UserState getState() {
		return this.state.getState();
	}
	public String getName() {
		return this.name.getName();
	}
	public String getFirstName() {
		return this.name.getFirstName();
	}
	public String getLastName() {
		return this.name.getLastName();
	}
	public String getEmail() {
		return this.email;
	}
	public String getUsername() {
		return this.username;
	}
	private String saltHashedPassword(String password) {
		//return Integer.toString(password.hashCode());
		return password;
	}
	public boolean verifyPassword(String password) {
		return (this.password).equals(saltHashedPassword(password));
	}

	
	public boolean isNull() {
		return false;
	}

	public String toString() {
		return String.format("Name %s\nEmail %s\nUsername %s\nPassword %s\nStatus %s\n",name.getName(),email ,username,password,state);
	}
	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof User))
            return false; 
        User userToCheck = (User)o;
        if (! this.getName().equals(userToCheck.getName()))
        	return false;
        if (! this.getEmail().equals(userToCheck.getEmail()))
        	return false;
        if (! this.getUsername().equals(userToCheck.getUsername()))
        	return false;
        if (! (this.state.getState()).equals((userToCheck.state).getState()))
        	return false;
        return true;
	}
	public void setName(Name newName) throws InvalidUserException {
		updateDetails(newName, this.email);
	}
	public void setEmail(String newemail) throws InvalidUserException {
		updateDetails(this.name, newemail);
	}
	public void resetPassword(String newPassword) throws InvalidPasswordException {
		UserDataValidator.validatePassword(newPassword);
		this.password= saltHashedPassword(newPassword);
		userStateChanged();
	}

	public void updateDetails(Name newName, String newEmail) throws InvalidUserException{
		UserDataValidator.validateName(newName);
		UserDataValidator.validateEmail(newEmail);
		this.name = newName;
		this.email = newEmail;
		userStateChanged();
	}
	
	protected void userStateChanged() {
		userAuthentication.modifyUser(this);
	}

}

abstract class ApplicationUser extends User implements Serializable{
	private static final long serialVersionUID = 42L;
	protected Wallet wallet;
	
	public ApplicationUser(UserAuthentication auth,Name name, String email,String username,String password) throws InvalidUserException{
		super(auth, name,  email, username, password);
		this.wallet = new Wallet();
	}
	
	protected ApplicationUser() {
		super();
		this.wallet = new Wallet();
	}
	
	public double getBalance() {
		return wallet.getBalance();
	}
	
	public boolean isWithdrawableAmount(double amount) throws WalletException{
		return wallet.isWithdrawable(amount);
	}
	public void depositAmount(double amount) throws WalletException{
		wallet.deposit(amount);
		userStateChanged();
	}
	public void withdrawAmount(double amount) throws WalletException{
		wallet.withdraw(amount);
		userStateChanged();
	}
	public void makePayment(ApplicationUser recipientUser,double amount) throws WalletException{
		wallet.makePayment(recipientUser.wallet, amount);
		userStateChanged();
		recipientUser.userStateChanged();
	}
}

@SuppressWarnings("serial")
class BookingUser extends ApplicationUser {
	
	public BookingUser(UserAuthentication auth, Name name, String email,String username,String password) throws InvalidUserException{
		super(auth,name, email,username, password);
		this.state = BookingUserState.VALID;
	}
	
	public void block() throws StateException{
		if (this.state == BookingUserState.BLOCKED)
			throw new AlreadyBlockedException();
		this.state = BookingUserState.BLOCKED;
		userStateChanged();
	}
	public void validate() throws StateException {
		if (this.state == BookingUserState.VALID)
			throw new AlreadyValidException();
		this.state = BookingUserState.VALID;
		userStateChanged();
	}
	public boolean isValid() {
		return this.state == BookingUserState.VALID;
	}
}

@SuppressWarnings("serial")
class BusOperator extends ApplicationUser{
	
	public BusOperator(UserAuthentication auth,Name name, String email,String username,String password) throws InvalidUserException{
		super(auth, name, email,username, password);
		this.state = BusOperatorState.PENDING;
		super.wallet = new Wallet() {
			public void makePayment(Wallet recipientWallet,double amount) throws WalletException{
				Wallet.validateAmount(amount);
				this.balanceAmount -= amount;
				recipientWallet.deposit(amount);
			}
		};
	}
	public void block()  throws StateException {
		if (this.state == BusOperatorState.BLOCKED)
			throw new AlreadyBlockedException();
		this.state = BusOperatorState.BLOCKED;
		userStateChanged();
	}
	public void validate() throws StateException {
		if (this.state == BusOperatorState.VALID)
			throw new AlreadyValidException();
		this.state = BusOperatorState.VALID;
		userStateChanged();
	}
	public boolean isValid() {
		return this.state == BusOperatorState.VALID;
	}
}


@SuppressWarnings("serial")
class NullUser extends ApplicationUser{
	public NullUser() {
		super();
	}
	
	public void block() {
		this.state = BookingUserState.BLOCKED;
	}
	public void validate() {
		this.state = BookingUserState.BLOCKED;
	}
	public void setName(String name) {
		return;
	}
	public String getName() {
		return "";
	}
	public void setEmail(String email) {
		return;
	}
	public String getEmail() {
		return "";
	}
	public String getUsername() {
		return "";
	}
	public void resetPassword(String password) {
		return;
	}
	public boolean verifyPassword(String password) {
		return false;
	}
	public boolean isNull() {
		return true;
	}
	public void displayDetails(){
		System.out.println("Invalid User");
	}
	public boolean isValid() {
		return false;
	}
	public double getBalance() {
		return 0;
	}
	
	public boolean isWithdrawableAmount(double amount) throws WalletException{
		return false;
	}
	public void depositAmount(double amount) throws WalletException{
		throw new WalletException("No wallet for null user");
	}
	public void withdrawAmount(double amount) throws WalletException{
		throw new WalletException("No wallet for null user");
	}
	public void makePayment(ApplicationUser recipientUser,double amount) throws WalletException{
		throw new WalletException("No wallet for null user");
	}
}

@SuppressWarnings("serial")
class Name implements Serializable{
	private String firstName;
	private String lastName;
	
	public Name(String firstName, String lastName) throws InvalidNameException{
		try {UserDataValidator.validateName(firstName);}
		catch(InvalidNameException exception) {throw new InvalidNameException("Invalid First Name");}
		try {UserDataValidator.validateName(lastName);}
		catch(InvalidNameException exception) {throw new InvalidNameException("Invalid Last Name");}	

		this.firstName = firstName;
		this.lastName = lastName; 
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getName() {
		return this.getFirstName() +" "+ this.getLastName();
	}
}

