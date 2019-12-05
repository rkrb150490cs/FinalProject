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

public interface BusStorage{
	public void storeBus(Bus bus);
	public Bus getBus(String busPlateNumber);
	public LinkedList<Bus> getAllBuses();
	public LinkedList<Bus> getAllBuses(BusOperator operator);
	public void removeBus(Bus bus);
	public void updateBus(Bus bus);
	public boolean doesBusPlateNumberExist(String plateNumber);
}


class BusFileStorage implements BusStorage{
	private static final String busFileLocation = ConfigurationFiles.busFileLocation;
	
	public void storeBus(Bus bus) {
		FileOutputStream f;
		ObjectOutputStream o;
		try {
			if (new File(busFileLocation).isFile()) {
				f = new FileOutputStream(new File(busFileLocation),true);
				o = new AppendingObjectOutputStream(f);
			}
			else {
				f = new FileOutputStream(new File(busFileLocation));
				o = new ObjectOutputStream(f);
			}
			o.writeObject(bus);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for storing bus information.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for storing bus information.");
			e.printStackTrace();
		}
	}
	public Bus getBus(String busPlateNumber) {
		try {
			FileInputStream fi = new FileInputStream(new File(busFileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					Bus bus = (Bus)oi.readObject();
					if(bus.getPlateNumber().equals(busPlateNumber)) {
						return bus;
					}
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for loading bus information.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for loading bus information.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public LinkedList<Bus> getAllBuses(){
		LinkedList<Bus> allBuses = new LinkedList<Bus>();
		try {
			FileInputStream fi = new FileInputStream(new File(busFileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					Bus bus = (Bus)oi.readObject();
					allBuses.add(bus);
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for loading bus information.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for loading bus information.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return allBuses;
	}
	public LinkedList<Bus> getAllBuses(BusOperator operator){
		LinkedList<Bus> userBuses = new LinkedList<Bus>();
		for (Bus storedBus : getAllBuses()) {
			if (storedBus.getOwner().equals(operator))
				userBuses.add(storedBus);
		}
		return userBuses;
	}
	
	public void removeBus(Bus bus) {
		LinkedList<Bus> allBuses = getAllBuses();
		int index = 0;
		for (Bus storedBus : allBuses) {
			if (storedBus.equals(bus))
				break;
			index++;
		}
		try {
		allBuses.remove(index);
		modifyStoredBuses(allBuses);
		}catch(IndexOutOfBoundsException e){
			System.out.println("Bus cannot be removed, does not exist.");
		}
	}
	public void updateBus(Bus bus) {
		LinkedList<Bus> allBuses = getAllBuses();
		int index = 0;
		for (Bus storedBus : allBuses) {
			if (storedBus.getPlateNumber().equals(bus.getPlateNumber()))
				break;
			index++;
		}
		try {
		allBuses.remove(index);
		allBuses.add(bus);
		modifyStoredBuses(allBuses);
		}catch(IndexOutOfBoundsException e){
			System.out.println("Bus cannot be mdified, does not exist.");
		}
	}

	public boolean doesBusPlateNumberExist(String plateNumber) {
		LinkedList<Bus> allBuses = getAllBuses();
		int index = 0;
		for (Bus storedBus : allBuses) {
			if (storedBus.getPlateNumber().equals(plateNumber))
				break;
			index++;
		}
		if (index < allBuses.size())
			return true;
		else 
			return false;
	}
	
	private void modifyStoredBuses(LinkedList<Bus> allBuses) {
		try {
			FileOutputStream f ;
			ObjectOutputStream o ;
			f = new FileOutputStream(new File(busFileLocation));
			o = new ObjectOutputStream(f);
			Bus firstlocation = allBuses.pop();
			o.writeObject(firstlocation);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(busFileLocation),true);
			o = new AppendingObjectOutputStream(f);
			for (Bus location : allBuses) {
				o.writeObject(location);
			}
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for modifying bus details.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for modifying bus details.");
			e.printStackTrace();
		}catch (NoSuchElementException e) {
		}
	}

}
