// TODO cancel TIcket.
// TODO pick and reserve seats.
// TODO make payments.


import java.io.Serializable;
import java.util.*;


public class TicketManagement {
	private TicketStorage storageMechanism;
	
	public TicketManagement(TicketStorage storageMechanism) {
		this.storageMechanism = storageMechanism;
	}

	public void generateNewTicket(BookingUser user, String SourceLocation, String DestinationLocation, Seat reservedSeat) throws WalletException {
		ScheduledBus scheduledBus = reservedSeat.getScheduledBus();
		double fare = (scheduledBus.getBus()).getFare(SourceLocation, DestinationLocation);
		int ticketId = storageMechanism.getNextTicketID();
		BusOperator busOwner = scheduledBus.getBus().getOwner();

		user.makePayment(busOwner, fare);
		scheduledBus.reserveSeats(SourceLocation, DestinationLocation, reservedSeat);
		UtilityClass.busManagement.updateBus(scheduledBus.getBus());

		Ticket newTicket = new Ticket(ticketId, user, SourceLocation, DestinationLocation,reservedSeat, fare);
		storageMechanism.storeTicket(newTicket);
	}
	
	public void cancelTicket(Ticket ticket) throws WalletException, CancellationException {
		ticket.cancelTicket();
		storageMechanism.updateTicket(ticket);
		
		ScheduledBus scheduledBus = ticket.getBookedBus();	
		scheduledBus.unReserveSeats(ticket.getSourceLocation(), ticket.getDestinationLocation(), ticket.getReservedSeat());
		UtilityClass.busManagement.updateBus(scheduledBus.getBus());
		
		ticket.getBookedBus().getBus().getOwner().makePayment(ticket.getTicketHolder(), ticket.getFare());
		
	}
	public LinkedList<Ticket> getAllTickets(){
		LinkedList<Ticket> allTickets = storageMechanism.getAllTickets();
		Collections.sort(allTickets, new SortTicketByDate());	
		return allTickets ;
	}
	public LinkedList<Ticket> getAllTickets(BookingUser user){
		return storageMechanism.getAllTickets(user);
	}
	public String toString() {
		String output = "";
		for (Ticket ticket : getAllTickets()) {
			output = output + ticket.toString() + "\n";
		}
		return output;
	}
}

enum TicketStatus{
	VALID,
	CANCELLED;
}

class Ticket implements Serializable{
	private static final long serialVersionUID = 42L;
	private int ticketId;
	private TicketStatus ticketStatus;
	private ScheduledBus scheduledBus;
	private Seat reservedSeat; 
	private BookingUser bookedByUser;
	private String SourceLocation;
	private String DestinationLocation;
	private SimpleDate dateOfTravel;
	private double fare;
	
 	public Ticket(int ticketId,BookingUser user, String SourceLocation, String DestinationLocation, Seat reservedSeat, double fare){
 		this.ticketId = ticketId; 
		this.ticketStatus = TicketStatus.VALID;
		this.reservedSeat = reservedSeat;
		this.scheduledBus = reservedSeat.getScheduledBus();
		this.bookedByUser = user;
		this.SourceLocation = SourceLocation;
		this.DestinationLocation = DestinationLocation;
		this.dateOfTravel = scheduledBus.getDate();
		this.fare = fare;
	}
 	protected Ticket() {
 		this.ticketId = 0; 
		this.ticketStatus = TicketStatus.CANCELLED;
		this.scheduledBus = null;
		this.bookedByUser = null;
		this.SourceLocation = null;
		this.DestinationLocation = null;
		this.dateOfTravel = null;
		this.fare = 0.0;
 	}
	
 	public int getTicketId() {
		return this.ticketId;
	}
 	public TicketStatus getTicketStatus() {
 		return this.ticketStatus;
 	}
	public ScheduledBus getBookedBus() {
		Bus oldBus = scheduledBus.getBus();
		Bus updatedBus = UtilityClass.busManagement.getUpdatedBus(oldBus);
		return updatedBus.getScheduledBus(scheduledBus.getDate());
	}
	public Seat getReservedSeat() {
		return this.reservedSeat;
	}
	public BookingUser getTicketHolder() {
		return (BookingUser)UtilityClass.userAuthenticator.getUpdatedUser(this.bookedByUser.getUsername());
	}
	public String getSourceLocation() {
		return this.SourceLocation;
	}
	public String getDestinationLocation() {
		return this.DestinationLocation;
	}
	public SimpleDate getDateOfTravel() {
		return this.dateOfTravel;
	}
	public double getFare() {
		return this.fare;
	}

	public void cancelTicket() throws CancellationException{
		if (this.ticketStatus == TicketStatus.CANCELLED)
			throw new AlreadyCancelledException();
		
		if (new SimpleDate.DateComparator().compare(this.dateOfTravel, SimpleDate.getCurrentDate()) <= 0)
			throw new CancellationDateException();
		
		this.ticketStatus = TicketStatus.CANCELLED;
	}
	
	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof Ticket))
            return false; 
        Ticket ticketToCheck = (Ticket)o;
        if ( this.ticketId != ticketToCheck.ticketId)
        	return false;
        if (! (this.ticketStatus).equals(ticketToCheck.ticketStatus))
        	return false;
        if (! (this.scheduledBus).equals(ticketToCheck.scheduledBus))
        	return false;
        if (! (this.bookedByUser).equals(ticketToCheck.bookedByUser))
        	return false;
        if (! (this.SourceLocation).equals(ticketToCheck.SourceLocation))
        	return false;
        if (! (this.DestinationLocation).equals(ticketToCheck.DestinationLocation))
        	return false;
        if ( this.fare != ticketToCheck.fare)
        	return false;
        return true;
	}
}

@SuppressWarnings("serial")
class CancellationException extends Exception{
	public CancellationException(String message) {
		super(message);
	}
}
@SuppressWarnings("serial")
class AlreadyCancelledException extends CancellationException{
	public AlreadyCancelledException() {
		super("Ticket already cancelled.");
	}
}
@SuppressWarnings("serial")
class CancellationDateException extends CancellationException{
	public CancellationDateException() {
		super("Tickets cannot be cancelled on Date of travel.");
	}
}


class NullTicket extends Ticket{
	private static final long serialVersionUID = 42L;
	public NullTicket() {
		super();
	}
	public boolean equals(Object o) {
		if (o instanceof NullTicket)
            return true;
		return false;
	}
}

class SortTicketByDate implements Comparator<Ticket> { 
    public int compare(Ticket a, Ticket b) 
    { 
    	return new SimpleDate.DateComparator().compare(a.getDateOfTravel(),b.getDateOfTravel());
    } 
} 