
@SuppressWarnings("serial")
public class AdminUser extends User{

	public AdminUser(UserAuthentication auth,Name name, String email, String username, String password) throws InvalidUserException {
		super(auth,name, email, username, password);
		this.state = AdminState.VALID;
	}

	@Override
	public void block() throws StateException{
		if (this.state == AdminState.BLOCKED)
			throw new AlreadyBlockedException();
		this.state =  AdminState.BLOCKED;
		userStateChanged();
	}
	public void validate() throws StateException{
		if (this.state == AdminState.VALID)
			throw new AlreadyValidException();
		this.state = AdminState.VALID;
		userStateChanged();
	}
	public boolean isValid() {
		return this.state == AdminState.VALID;
	}

}




