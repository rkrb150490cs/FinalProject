
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

public interface TicketStorage{
	public void storeTicket(Ticket ticket);
	public Ticket getTicket(int ticketID);
	public LinkedList<Ticket> getAllTickets();
	public LinkedList<Ticket> getAllTickets(BookingUser user);
	public boolean doesTicketIDExist(int ticketID);
	public int getNextTicketID();
	public void removeTicket(Ticket ticket);
	public void updateTicket(Ticket ticket);
}


class TicketFileStorage implements TicketStorage{
	private static final String ticketFileLocation = ConfigurationFiles.ticketFileLocation;
	
	public void storeTicket(Ticket ticket) {
		FileOutputStream f;
		ObjectOutputStream o;
		try {
			if (new File(ticketFileLocation).isFile()) {
				f = new FileOutputStream(new File(ticketFileLocation),true);
				o = new AppendingObjectOutputStream(f);
			}
			else {
				f = new FileOutputStream(new File(ticketFileLocation));
				o = new ObjectOutputStream(f);
			}
			o.writeObject(ticket);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for storing ticket information.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for storing ticket information.");
		}
	}
	public Ticket getTicket(int ticketID) {
		try {
			FileInputStream fi = new FileInputStream(new File(ticketFileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					Ticket ticket = (Ticket)oi.readObject();
					if(ticket.getTicketId() == ticketID) {
						return ticket;
					}
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for loading ticket information.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for loading ticket information.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public LinkedList<Ticket> getAllTickets(){
		LinkedList<Ticket> allTickets = new LinkedList<Ticket>();
		try {
			FileInputStream fi = new FileInputStream(new File(ticketFileLocation));
			ObjectInputStream oi = new ObjectInputStream(fi);
			while(true) {
				try {
					Ticket ticket = (Ticket)oi.readObject();
					allTickets.add(ticket);
				}catch (EOFException exp) {
					break;
				}
			}
			oi.close();
			fi.close();
		}catch (FileNotFoundException e) {
			System.out.println("File not found for loading ticket information2.");
			Ticket dummyTicket = new NullTicket();
			storeTicket(dummyTicket);
			removeTicket(dummyTicket);
			
		} catch (IOException e) {
			System.out.println("Error initializing stream for loading ticket information.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return allTickets;
	}
	public LinkedList<Ticket> getAllTickets(BookingUser user){
		LinkedList<Ticket> userTickets = new LinkedList<Ticket>();
		for (Ticket storedTicket : getAllTickets()) {
			if (storedTicket.getTicketHolder().equals(user))
				userTickets.add(storedTicket);
		}
		return userTickets;
	}
	public void removeTicket(Ticket ticket) {
		LinkedList<Ticket> allTickets = getAllTickets();
		int index = 0;
		for (Ticket storedTicket : allTickets) {
			if (storedTicket.equals(ticket))
				break;
			index++;
		}
		try {
			allTickets.remove(index);
			modifyStoredTickets(allTickets);
		}catch(IndexOutOfBoundsException e){
			System.out.println("Ticket cannot be removed, does not exist.");
		}
	}
	public void updateTicket(Ticket ticket) {
		LinkedList<Ticket> allTickets = getAllTickets();
		int index = 0;
		for (Ticket storedTicket : allTickets) {
			if (storedTicket.getTicketId() == ticket.getTicketId())
				break;
			index++;
		}
		try {
			allTickets.remove(index);
			allTickets.add(ticket);
			modifyStoredTickets(allTickets);
		}catch(IndexOutOfBoundsException e){
			System.out.println("Ticket cannot be modifies, does not exist.");
		}
	}

	public boolean doesTicketIDExist(int ticketID) {
		LinkedList<Ticket> allTickets = getAllTickets();
		int index = 0;
		for (Ticket storedTicket : allTickets) {
			if (storedTicket.getTicketId() == ticketID)
				break;
			index++;
		}
		if (index < allTickets.size())
			return true;
		else 
			return false;
	}
	public int getNextTicketID(){
		for(int i = 0; i <= Integer.MAX_VALUE;i++)
			if(!doesTicketIDExist(i))
				return i;
		return -1;
	}
	
	private void modifyStoredTickets(LinkedList<Ticket> allTickets) {
		try {
			FileOutputStream f ;
			ObjectOutputStream o ;
			f = new FileOutputStream(new File(ticketFileLocation));
			o = new ObjectOutputStream(f);
			Ticket firstTicket = allTickets.pop();
			o.writeObject(firstTicket);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(ticketFileLocation),true);
			o = new AppendingObjectOutputStream(f);
			for (Ticket ticket : allTickets) {
				o.writeObject(ticket);
			}
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found for modifying ticket details.");
		} catch (IOException e) {
			System.out.println("Error initializing stream for modifying ticket details.");
			e.printStackTrace();
		}catch (NoSuchElementException e) {
		}
	}

}
