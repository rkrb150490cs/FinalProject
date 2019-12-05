
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("serial")
class UsernameAlreadyTakenException extends InvalidUserException{
	public UsernameAlreadyTakenException() {
		super("Username is already Taken");
	}
}

@SuppressWarnings("serial")
public class UserAuthentication implements Serializable{
	private UserStorage storageMechanism;
	
	public UserAuthentication(UserStorage storageMechanism) {
		this.storageMechanism = storageMechanism;
	}
	
	public void signup(User newUser) throws UsernameAlreadyTakenException {
		if (storageMechanism.doesUsernameExist(newUser.getUsername()))
			throw new UsernameAlreadyTakenException();
		storageMechanism.storeUser(newUser);
	}
	
	public void signup(Name name, String email,String username,String password,int type) throws InvalidUserException{
		if (type == 1) 
			signup(new BookingUser(this,name, email,username,password));
		else  
			signup(new BusOperator(this,name, email,username,password));
	}
	public User login(String username, String password) {
			User candidateUser = storageMechanism.loadUser(username);
			if (candidateUser.verifyPassword(password))
				return candidateUser;
			return new NullUser();
	}
	public User getUpdatedUser(String username) {
		return storageMechanism.loadUser(username);
	}
	
	public void modifyUser(User user) {
		storageMechanism.updateUser(user);
	}
	
	public LinkedList<User> getAllUsers(){
		return storageMechanism.loadAllUsers();
	}
	public void displayAllUsers() {
		LinkedList<User> allUsers = storageMechanism.loadAllUsers();
		for (Iterator<User> users = allUsers.iterator(); users.hasNext();) 
			System.out.println(users.next());
	}
}


