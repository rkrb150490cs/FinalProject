import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class LocationManager {
	private LocationStorage storage;
	

	public LocationManager(LocationStorage storage) {
		this.storage = storage;
	}
	public void addLocation(String location) throws InvalidLocationStringException{
		validateLocation(location);
		String formattedLocation = (location.substring(0, 1)).toUpperCase() + (location.substring(1)).toLowerCase();
		storage.addLocation(formattedLocation);
	}
	public void removeLocation(String location) throws InvalidLocationStringException{
		validateLocation(location);
		storage.removeLocation(location);
	}
	public  LinkedList<String> getAllLocations() {
		return storage.getAllLocations();
	}
	
	private void validateLocation(String location) throws InvalidLocationStringException{
		if (location == null)
			throw new EmptyLocationStringException();
		if (location.equals(""))
			throw new EmptyLocationStringException();
			
		String regex = "^[a-zA-Z]+$";
	     if (!PaternMatcher.doesMatch(regex, location))
	    	 throw new InvalidLocationForamtException();
	}
		
}


@SuppressWarnings("serial")
class InvalidLocationStringException extends Exception{
	public InvalidLocationStringException(String message) {
		super(message);
	}	
}
@SuppressWarnings("serial")
class EmptyLocationStringException extends InvalidLocationStringException{
	public EmptyLocationStringException() {
		super("Invalid Empty Location entered.");
	}	
}
@SuppressWarnings("serial")
class InvalidLocationForamtException extends InvalidLocationStringException{
	public InvalidLocationForamtException() {
		super("Invalid Location format entered, should contain only alphabets.");
	}	
}



interface LocationStorage{
	public void addLocation(String location);
	public void removeLocation(String location);
	public  LinkedList<String> getAllLocations();
	public boolean doesLocationExist(String location);
}


class LocationFileStorage implements LocationStorage{
	private static final String busRoutesLocation = ConfigurationFiles.busRoutesFileLocation;
	
	public void addLocation(String location) {
		FileOutputStream f;
		ObjectOutputStream o;
		try {
			if (new File(busRoutesLocation).isFile()) {
				f = new FileOutputStream(new File(busRoutesLocation),true);
				o = new AppendingObjectOutputStream(f);
			}
			else {
				f = new FileOutputStream(new File(busRoutesLocation));
				o = new ObjectOutputStream(f);
			}
			o.writeObject(location);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for storing bus route locations.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for bus route location storing.");
			e.printStackTrace();
		}
	}
	public void removeLocation(String location) {
		LinkedList<String> allLocations = getAllLocations();
		int index = 0;
		for (String storedLocation : allLocations) {
			if (storedLocation.equals(location))
				break;
			index++;
		}
		try {
		allLocations.remove(index);
		modifyStoredLocations(allLocations);
		}catch(IndexOutOfBoundsException e){
			System.out.println("Location cannot be removed, does not exist.");
		}
	}
	public LinkedList<String> getAllLocations(){
		LinkedList<String> allLocations = new LinkedList<String>();
		try {
			FileInputStream fi = new FileInputStream(new File(busRoutesLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					String location = (String)oi.readObject();
					allLocations.add(location);
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for loading bus route locations.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for loading bus route locations");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		allLocations.sort(null);
		return allLocations;
		}

	private void modifyStoredLocations(LinkedList<String> allLocations) {
		try {
			FileOutputStream f ;
			ObjectOutputStream o ;
			f = new FileOutputStream(new File(busRoutesLocation));
			o = new ObjectOutputStream(f);
			String firstlocation = allLocations.pop();
			o.writeObject(firstlocation);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(busRoutesLocation),true);
			o = new AppendingObjectOutputStream(f);
			for (String location : allLocations) {
				o.writeObject(location);
			}
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for modifying bus route locations.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for modifying bus route locations.");
			e.printStackTrace();
		}catch (NoSuchElementException e) {
		}
	}
	
	public boolean doesLocationExist(String location) {
		LinkedList<String> allLocations = getAllLocations();
		int index = 0;
		for (String storedLocation : allLocations) {
			if (storedLocation.equals(location))
				break;
			index++;
		}
		if (index < allLocations.size())
			return true;
		else 
			return false;
	}
}