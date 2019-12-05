
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedList;


class AppendingObjectOutputStream extends ObjectOutputStream {
	  public AppendingObjectOutputStream(OutputStream out) throws IOException {
	    super(out);
	  }
	  @Override
	  protected void writeStreamHeader() throws IOException {
	    // do not write a header, but reset:
	    reset();
	  }
	}

public interface UserStorage{
	public boolean doesUsernameExist(String username);
	public void storeUser(User user);
	public User loadUser(String username);
	public LinkedList<User> loadAllUsers();
	public void updateUser(User user);
}


class DummyStorage implements UserStorage{
	public boolean doesUsernameExist(String username) {
		return true;
	}
	public void storeUser(User user) {
		System.out.println(user);
	}
	public User loadUser(String username) {
		return new NullUser();
	}
	public LinkedList<User> loadAllUsers(){
		return new LinkedList<User>();
	}
	public void updateUser(User user) {
		System.out.println("Modify called");
	}
}	

@SuppressWarnings("serial")
class UserFileStorage implements UserStorage,Serializable{
	private BusOperatorFileStorage busOperatorStorage;
	private BookingUserFileStorage bookingUserStorage;
	private AdminUserFileStorage adminUserStorage;
	
	public boolean doesUsernameExist(String username) {
		return !loadUser(username).isNull();
	}
	public UserFileStorage() {
		busOperatorStorage = new BusOperatorFileStorage();
		bookingUserStorage = new BookingUserFileStorage();
		adminUserStorage = new AdminUserFileStorage();
	}
	
	public void storeUser(User user) {
		if (user instanceof BookingUser)
			bookingUserStorage.store((BookingUser)user);
		if (user instanceof BusOperator)
			busOperatorStorage.store((BusOperator)user);
		if (user instanceof AdminUser)
			adminUserStorage.store((AdminUser)user);
	}
	public User loadUser(String username) {
		User user = bookingUserStorage.loadBookingUser(username);
		if(! user.isNull())
			return user;
		user =  busOperatorStorage.loadBusOperator(username);
		if(! user.isNull())
			return user;
		user =  adminUserStorage.loadAdminUser(username);
		return user;
	}
	public LinkedList<User> loadAllUsers(){
		LinkedList<User> allUsers = new LinkedList<User>();
		allUsers.addAll(bookingUserStorage.loadAllBookingUsers());
		allUsers.addAll(busOperatorStorage.loadAllBusOperators());
		allUsers.addAll(adminUserStorage.loadAllAdminUsers());
		return allUsers;
	}
	public void updateUser(User user) {
		if (user instanceof BookingUser)
			bookingUserStorage.updateUser((BookingUser)user);
		if (user instanceof BusOperator)
			busOperatorStorage.updateUser((BusOperator)user);
		if (user instanceof AdminUser)
			adminUserStorage.updateUser((AdminUser)user);
	}
}

@SuppressWarnings("serial")
class BookingUserFileStorage implements Serializable{
	private static final String bookingUsersfileLocation = ConfigurationFiles.bookingUsersfileLocation;
	
	public void store(BookingUser user) {
		FileOutputStream f;
		ObjectOutputStream o;
		try {
			if (new File(bookingUsersfileLocation).isFile()) {
				f = new FileOutputStream(new File(bookingUsersfileLocation),true);
				o = new AppendingObjectOutputStream(f);
			}
			else {
				f = new FileOutputStream(new File(bookingUsersfileLocation));
				o = new ObjectOutputStream(f);
			}
			o.writeObject(user);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for BookingUser storing");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BookingUser storing");
			e.printStackTrace();
		}	
	}
	public User loadBookingUser(String username) {
		try {
			FileInputStream fi = new FileInputStream(new File(bookingUsersfileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					User pr1 = (BookingUser)oi.readObject();
					if(pr1.getUsername().equals(username)) {
						return pr1;
					}
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for BookingUser loading");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BookingUser loading");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (User)(new NullUser());
	}
	public LinkedList<BookingUser> loadAllBookingUsers(){
		LinkedList<BookingUser> allUsers = new LinkedList<BookingUser>();
		try {
			FileInputStream fi = new FileInputStream(new File(bookingUsersfileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					BookingUser user = (BookingUser)oi.readObject();
					allUsers.add(user);
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for BookingUser loading");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BookingUser loading");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			
		return allUsers;
		}
	public void updateUser(BookingUser user) {
		LinkedList<BookingUser> allUsers = loadAllBookingUsers();
		int index = 0;
		for (BookingUser candidateUser : allUsers) {
			if (candidateUser.getUsername().equals(user.getUsername()))
				break;
			index++;
		}
		allUsers.remove(index);
		allUsers.add(user);
		modifyStoredBookingUsers(allUsers);
	}
	private void modifyStoredBookingUsers(LinkedList<BookingUser> allUsers) {
		try {
			FileOutputStream f ;
			ObjectOutputStream o ;
			f = new FileOutputStream(new File(bookingUsersfileLocation));
			o = new ObjectOutputStream(f);
			BookingUser firstUser = allUsers.pop();
			o.writeObject(firstUser);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(bookingUsersfileLocation),true);
			o = new AppendingObjectOutputStream(f);
			for (BookingUser user : allUsers) {
				o.writeObject(user);
			}
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for BookingUser storing");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BookingUser storing");
			e.printStackTrace();
		}
	}

}

@SuppressWarnings("serial")
class BusOperatorFileStorage implements Serializable{
	private static final String busOperatorsfileLocation = ConfigurationFiles.busOperatorsfileLocation;
	
	public void store(BusOperator user) {
		
		FileOutputStream f;
		ObjectOutputStream o;
		try {
			if (new File(busOperatorsfileLocation).isFile()) {
				f = new FileOutputStream(new File(busOperatorsfileLocation),true);
				o = new AppendingObjectOutputStream(f);
			}
			else {
				f = new FileOutputStream(new File(busOperatorsfileLocation));
				o = new ObjectOutputStream(f);
			}
			o.writeObject(user);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for BusOperator storing");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BusOperator storing");
			e.printStackTrace();
		}
	}
	public User loadBusOperator(String username) {
		try {
			FileInputStream fi = new FileInputStream(new File(busOperatorsfileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					User pr1 = (BusOperator)oi.readObject();
					if(pr1.getUsername().equals(username)) {
						return pr1;
					}
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for BusOperator loading");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BusOperator loading");
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (User)(new NullUser());
	}
	public LinkedList<BusOperator> loadAllBusOperators(){
		LinkedList<BusOperator> allUsers = new LinkedList<BusOperator>();
		try {
			FileInputStream fi = new FileInputStream(new File(busOperatorsfileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					BusOperator user = (BusOperator)oi.readObject();
					allUsers.add(user);
//					user.displayDetails();
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for BusOperator loading");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BusOperator loading");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			
		return allUsers;
		}
	public void updateUser(BusOperator user) {
		LinkedList<BusOperator> allUsers = loadAllBusOperators();
		int index = 0;
		for (BusOperator candidateUser : allUsers) {
			if (candidateUser.getUsername().equals(user.getUsername()))
				break;
			index++;
		}
		allUsers.remove(index);
		allUsers.add(user);
		modifyStoredBusOperators(allUsers);
	}
	private void modifyStoredBusOperators(LinkedList<BusOperator> allUsers) {
		try {
			FileOutputStream f ;
			ObjectOutputStream o ;
			f = new FileOutputStream(new File(busOperatorsfileLocation));
			o = new ObjectOutputStream(f);
			BusOperator firstUser = allUsers.pop();
			o.writeObject(firstUser);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(busOperatorsfileLocation),true);
			o = new AppendingObjectOutputStream(f);
			for (BusOperator user : allUsers) {
				o.writeObject(user);
			}
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for BusOperator storing");
		} catch (IOException e) {
			System.out.println("Error initializing stream for BusOperator storing");
			e.printStackTrace();
		}

	}
}


@SuppressWarnings("serial")
class AdminUserFileStorage implements Serializable{
	private static final String adminUsersfileLocation = ConfigurationFiles.adminUsersfileLocation;
	
	public void store(AdminUser user) {
		FileOutputStream f;
		ObjectOutputStream o;
		try {
			if (new File(adminUsersfileLocation).isFile()) {
				f = new FileOutputStream(new File(adminUsersfileLocation),true);
				o = new AppendingObjectOutputStream(f);
			}
			else {
				f = new FileOutputStream(new File(adminUsersfileLocation));
				o = new ObjectOutputStream(f);
			}
			o.writeObject(user);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for AdminUser storing");
		} catch (IOException e) {
			System.out.println("Error initializing stream for AdminUser storing");
			e.printStackTrace();
		}
	}
	public User loadAdminUser(String username) {
		try {
			FileInputStream fi = new FileInputStream(new File(adminUsersfileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					User pr1 = (AdminUser)oi.readObject();
					if(pr1.getUsername().equals(username)) {
						return pr1;
					}
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for AdminUser loading");
		} catch (IOException e) {
			System.out.println("Error initializing stream for AdminUser loading");
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (User)(new NullUser());
	}
	public LinkedList<AdminUser> loadAllAdminUsers(){
		LinkedList<AdminUser> allUsers = new LinkedList<AdminUser>();
		try {
			FileInputStream fi = new FileInputStream(new File(adminUsersfileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					AdminUser user = (AdminUser)oi.readObject();
					allUsers.add(user);
//					user.displayDetails();
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for AdminUser loading");
		} catch (IOException e) {
			System.out.println("Error initializing stream for AdminUser loading");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			
		return allUsers;
		}
	public void updateUser(AdminUser user) {
		LinkedList<AdminUser> allUsers = loadAllAdminUsers();
		int index = 0;
		for (AdminUser candidateUser : allUsers) {
			if (candidateUser.getUsername().equals(user.getUsername()))
				break;
			index++;
		}
		allUsers.remove(index);
		allUsers.add(user);
		modifyStoredAdminUsers(allUsers);
	}
	private void modifyStoredAdminUsers(LinkedList<AdminUser> allUsers) {
		try {
			FileOutputStream f ;
			ObjectOutputStream o ;
			f = new FileOutputStream(new File(adminUsersfileLocation));
			o = new ObjectOutputStream(f);
			AdminUser firstUser = allUsers.pop();
			o.writeObject(firstUser);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(adminUsersfileLocation),true);
			o = new AppendingObjectOutputStream(f);
			for (AdminUser user : allUsers) {
				o.writeObject(user);
			}
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for AdminUser storing");
		} catch (IOException e) {
			System.out.println("Error initializing stream for AdminUser storing");
			e.printStackTrace();
		}

	}
}





