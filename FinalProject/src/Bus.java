// TODO add Types of Busses -AC,NONAC

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


class BusValidator{
	public static void validateBusOerator(BusOperator owner) throws InvalidBusException{
		if (owner == null)
			throw new InvalidBusException("Bus Operator is not assigned");
	}
	public static void validateBusName(String busName) throws InvalidBusException {
		String regex = "^[a-zA-Z]+$";
	     if (!PaternMatcher.doesMatch(regex, busName))
	    	 throw new InvalidBusException("Invalid Bus name.");
	}
	public static void validatePlateNumber(String plateNumber) throws InvalidBusException {
		String regex = "^[a-zA-Z]+"+"[a-zA-Z0-9]*$";
	     if (!PaternMatcher.doesMatch(regex, plateNumber))
	    	 throw new InvalidBusException("Invalid Bus Plate Number.");
	}
	public static void validateBusRoute(LinkedList<BusStop> busRoute) throws InvalidBusException {
		if (busRoute == null)
			throw new InvalidBusException("Bus Route is not assigned");
		Double previousFare = 0.0;
		for (BusStop stop : busRoute) {
			if(stop.getFareFromSource() < previousFare) {
				throw new InvalidBusException("Bus Route Fare from source must be increasing.");
			}	
			previousFare = stop.getFareFromSource();
		}
		
		String[] locations = new String[busRoute.size()];
		int index = 0;
		for (BusStop stop : busRoute) {
			locations[index] = stop.getLocation();
			index++;
		}
		
		for (int i = 0;i<index-1;i++) {
			for (int j = i+1; j<index;j++) {
				if(locations[i].equals(locations[j]))
					throw new InvalidBusException("Bus Route Locations must not repeat.");
			}
		}
		
		SimpleTime previousTime = new SimpleTime(-1,-1);
		for (BusStop stop : busRoute) {
			if((new SimpleTime.TimeComparator()).compare(stop.getTime(),previousTime) <= 0) {
				throw new InvalidBusException("Bus Route Time must be increasing.");
			}	
			previousTime = stop.getTime();
		}
		
		
	}
	public static void validateNumberOfSeats(int numberOfSeats) throws InvalidBusException {
		if (numberOfSeats <= 0)
			throw new InvalidBusException("Number of seats cannot be 0/-ve.");
	}
	public static void validateFare(Double fare) throws InvalidBusException {
		if (fare <= 0.0)
			throw new InvalidBusException("Bus Fare cannot be 0/-ve.");
	}
}


@SuppressWarnings("serial")
class InvalidBusException extends Exception{
	public InvalidBusException(String message) {
		super(message);
	}
}

enum BusType{
	AC,

	NONAC;
}
enum SeatType{
	SL,
	SSL;
}

public class Bus implements Serializable {
	private static final long serialVersionUID = 42L;
	private BusOperator owner;
	private String busName;
	private String plateNumber;
	private LinkedList<BusStop> busRoute;
	private int numberOfSeats;
	private BusType busType;
	private SeatType seatType;
	
	private LinkedList<ScheduledBus> scheduledBuses;

	public Bus(BusOperator owner,String busName, String plateNumber,LinkedList<BusStop> busRoute,int numberOfSeats, BusType busType) throws InvalidBusException{
		BusValidator.validateBusOerator(owner);
		BusValidator.validateBusName(busName) ;
		BusValidator.validatePlateNumber(plateNumber);
		BusValidator.validateBusRoute(busRoute) ;
		BusValidator.validateNumberOfSeats(numberOfSeats);
		
		this.owner = owner;
		this.busName = busName;
		this.plateNumber = plateNumber;
		this.busRoute = busRoute;
		this.numberOfSeats = numberOfSeats;
		this.scheduledBuses = new LinkedList<ScheduledBus>();
		this.busType = busType ;
		this.seatType = seatType;
	}
	
	
	public BusType getBusType() {
		return this.busType;
	}	
	
	public SeatType getSeatType() {
		return this.seatType;
	}
	public BusOperator getOwner() {
		return (BusOperator)UtilityClass.userAuthenticator.getUpdatedUser(this.owner.getUsername());
	}
	public String getBusName() {
		return this.busName;
	}
	public String getPlateNumber() {
		return this.plateNumber;
	}
	public LinkedList<BusStop> getBusRoute(){
		LinkedList<BusStop> busRouteCopy = new LinkedList<BusStop>();
		for (BusStop busStop : busRoute) {
			busRouteCopy.add(new BusStop(busStop));
		}
		return busRouteCopy;
	}
	public LinkedList<String> getBusStopLocations(){
		LinkedList<String> locations = new LinkedList<String>();
		for (BusStop busStop : busRoute) {
			locations.add(busStop.getLocation());
		}
		return locations;
	}
	public int getNumberOfSeats(){
		return this.numberOfSeats;
	}
	public LinkedList<ScheduledBus> getScheduledBuses() {
		return scheduledBuses;
	}
	public ScheduledBus getScheduledBus(SimpleDate date) {
		for (ScheduledBus bus : scheduledBuses) {
			if (bus.getDate().equals(date))
				return bus;
		}
		return null;
	}
	public int getLocationIndex(String location) {
		int locationIndex = -1;
		for (BusStop busStop: busRoute) {
			locationIndex++;
			if(location.equals(busStop.getLocation()))
				return locationIndex;
		}
		return -1;
	}
	public double getFare(String source, String destination) {
		int sourceIndex = getLocationIndex(source);
		int destinationIndex = getLocationIndex(destination);
		
		double fareFromStartToSource = busRoute.get(sourceIndex).getFareFromSource();
		double fareFromStartToDestination = busRoute.get(destinationIndex).getFareFromSource();
		double totalFare = fareFromStartToDestination - fareFromStartToSource;
		return totalFare;
	}
	public SimpleTime getTimeAtLocation(String location) {
		int locationIndex = getLocationIndex(location);
		return busRoute.get(locationIndex).getTime();
	}
	public void updateBus(String busName,LinkedList<BusStop> busRoute) throws InvalidBusException {
		BusValidator.validateBusName(busName) ;
		BusValidator.validateBusRoute(busRoute) ;
		this.busName = busName;
		this.busRoute = busRoute;
	}
	
	public boolean doesScheduledBusExist(SimpleDate dateOfTravel) {
		for(ScheduledBus bus :scheduledBuses) {
			if (bus.getDate().equals(dateOfTravel)) {
				return true;
			}
		}
		return false;
	}
	public void scheduleNewBus(SimpleDate dateOfTravel) throws ScheduleBusException {
		if (doesScheduledBusExist(dateOfTravel))
			throw new ScheduleBusException();
		ScheduledBus scheduledBus = new ScheduledBus(this,dateOfTravel);
		scheduledBuses.add(scheduledBus);
		Collections.sort(scheduledBuses, new SortBusByDate());	
	}

	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof Bus))
            return false; 
        Bus busToCheck = (Bus)o;
        if (! this.owner.equals(busToCheck.owner))
        	return false;
        if (! this.busName.equals(busToCheck.busName))
        	return false;
        if (! this.plateNumber.equals(busToCheck.plateNumber))
        	return false;
        if (! this.busRoute.equals(busToCheck.busRoute))
        	return false;
        if (this.numberOfSeats != busToCheck.numberOfSeats)
        	return false;
        if (! this.scheduledBuses.equals(busToCheck.scheduledBuses))
        	return false;
        return true;
	}
	public boolean isOnRoute(String source, String destination) {
		int sourceIndex = getLocationIndex(source);
		int destinationIndex = getLocationIndex(destination);
		if (sourceIndex>=0 && sourceIndex < destinationIndex)
			return true;
		return false;
	}

	public String toString() {
		return "Name : "+this.busName +" PlateNumber : "+this.plateNumber + " Route,Fare : " +busRoute.toString()+ "NumberSeats : " + Integer.toString(numberOfSeats);
	}
}

@SuppressWarnings("serial")
class ScheduleBusException extends Exception{
	public ScheduleBusException () {
		super("Bus already scheduled for this date.\nChoose another date if required.");
	}
}

class BusStop implements Serializable{
	private static final long serialVersionUID = 42L;
	private String location;
	private double fareFromSource;
	private SimpleTime timeAtLocation;
	
	public BusStop(BusStop busStop) {
		this.location = busStop.location;
		this.fareFromSource = busStop.fareFromSource;
		this.timeAtLocation = busStop.timeAtLocation;
	}
	public BusStop(String location, double fareFromSource,SimpleTime timeAtLocation) throws InvalidBusException {
		BusValidator.validateFare(fareFromSource);
		this.location = location;
		this.fareFromSource = fareFromSource;
		this.timeAtLocation = timeAtLocation;
	}
 	public BusStop(String location,SimpleTime timeAtLocation) {
		this.location = location;
		this.fareFromSource = 0.0;
		this.timeAtLocation = timeAtLocation;
	}
	
 	public String getLocation() {
		return this.location;
	}
	public double getFareFromSource() {
		return this.fareFromSource;
	}
	public SimpleTime getTime() {
		return this.timeAtLocation;
	}
	
 	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof BusStop))
            return false; 
        BusStop busStopToCheck = (BusStop)o;
        if (! this.location.equals(busStopToCheck.location))
        	return false;
        if ( this.fareFromSource != busStopToCheck.fareFromSource)
        	return false;   
        return true;
	}
	public String toString() {
		return (location +" " + Double.toString(fareFromSource));
	}
}


class ScheduledBus implements Serializable{
	private static final long serialVersionUID = 42L;
	private Bus bus;
	private SimpleDate date;
	private Seat[] scheduledBusSeats;

	public ScheduledBus(Bus bus, SimpleDate date) {
		this.bus = bus;
		this.date = date;
		this.scheduledBusSeats = new Seat[bus.getNumberOfSeats()];
		
		LinkedList<String> busStopLocations = bus.getBusStopLocations();
		for (int i = 0; i < bus.getNumberOfSeats();i++) {
			scheduledBusSeats[i] = new Seat(this, i+1,busStopLocations);
		}
	}
	
	public SimpleDate getDate() {
		return this.date;
	}
	public Bus getBus() {
		return this.bus;
	}
	
	public void reserveSeats(String source, String destination, Seat seatToReserve) {
		for(Seat seat: scheduledBusSeats)
			if (seat.equals(seatToReserve))
				seat.reserveSeat(source, destination);
	}
	public void unReserveSeats(String source, String destination, Seat seatToUnreserve) {
		for(Seat seat: scheduledBusSeats)
			if (seat.equals(seatToUnreserve))
				seat.unReserveSeat(source, destination);
	}

	public boolean hasAvailableSeats(String source, String destination) {
		if (this.bus.isOnRoute(source, destination))
			for(Seat seat : scheduledBusSeats) {
				if(!seat.isReserved(source, destination)) {
					return true;
				}
			}
		return false;
	}
	public LinkedList<Seat> getAvailableSeats(String source, String destination){
		if(!hasAvailableSeats(source, destination)) {
			return null;
		}
		LinkedList<Seat> availableSeats = new LinkedList<Seat>();
		for(Seat seat : scheduledBusSeats) {
			if(!seat.isReserved(source, destination)) {
				availableSeats.add(new Seat(seat));
			}
		}
		return availableSeats;
	}
	
	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof ScheduledBus))
            return false; 
        ScheduledBus scheduledBusToCheck = (ScheduledBus)o;
        if (! this.bus.equals(scheduledBusToCheck.bus))
        	return false;
        if (! this.date.equals(scheduledBusToCheck.date))
        	return false;
        if ( this.scheduledBusSeats != scheduledBusToCheck.scheduledBusSeats)
        	return false;   
        return true;
	}
	public String toString() {
		return "Bus : "+bus.toString() + "\n Date : " + date.toString() + ", Seats : "+Arrays.toString(scheduledBusSeats);	
	}
}

class SortBusByDate implements Comparator<ScheduledBus> { 
    public int compare(ScheduledBus a, ScheduledBus b) 
    { 
    	return new SimpleDate.DateComparator().compare(a.getDate(),b.getDate());
    } 
} 

class Seat implements Serializable{
	private static final long serialVersionUID = 42L;
	
	private int seatNumber;
	private ScheduledBus scheduledBus;
	private LinkedList<SeatAtLocation> seatStatusAtLocations;
	
	public Seat(ScheduledBus scheduledBus, int seatNumber,LinkedList<String> locations) {
		this.scheduledBus = scheduledBus;
		this.seatNumber = seatNumber;
		seatStatusAtLocations = new LinkedList<SeatAtLocation>();
		for (String location : locations) {
			seatStatusAtLocations.add(new SeatAtLocation(location));
		}
	}
	public Seat(Seat seatToCopy) {
		this.seatNumber = seatToCopy.seatNumber;
		this.scheduledBus = seatToCopy.scheduledBus;
		
		seatStatusAtLocations = new LinkedList<SeatAtLocation>();
		for (SeatAtLocation seatAtLocation : seatToCopy.seatStatusAtLocations) {
			this.seatStatusAtLocations.add(new SeatAtLocation(seatAtLocation));
		}
	}
 
	public int getSeatNumber() {
		return this.seatNumber;
	}
	public ScheduledBus getScheduledBus() {
		return this.scheduledBus;
	}
	private int getLocationIndex(String location) {
		int locationIndex = -1;
		for (SeatAtLocation seatAtLocation: seatStatusAtLocations) {
			locationIndex++;
			if(location.equals(seatAtLocation.getLocation()))
				return locationIndex;
		}
		return -1;
	}
	
	public void reserveSeat(String source, String destination) {		
		int sourceIndex = getLocationIndex(source);
		int destinationIndex = getLocationIndex(destination);
		for (int i = sourceIndex; i <= destinationIndex; i++) {
			seatStatusAtLocations.get(i).reserveSeatAtLocation();
		}
	}
	public void unReserveSeat(String source, String destination) {
		int sourceIndex = getLocationIndex(source);
		int destinationIndex = getLocationIndex(destination);
		for (int i = sourceIndex; i <= destinationIndex; i++) {
			seatStatusAtLocations.get(i).unReserveSeatAtLocation();
		}
	}
	public boolean isReserved(String source, String destination) {
		int sourceIndex = getLocationIndex(source);
		int destinationIndex = getLocationIndex(destination);
		for (int i = sourceIndex; i <= destinationIndex; i++) {
			if (seatStatusAtLocations.get(i).isSeatAtLocationReserved())
				return true;
		}
		return false;
	}

	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof Seat))
            return false; 
        Seat seatToCheck = (Seat)o;
        if (this.seatNumber != seatToCheck.seatNumber)
        	return false;
        return true;
	}
	public String toString() {
		return Integer.toString(seatNumber) + " - "+seatStatusAtLocations.toString();
	}
}

//Add ticket as an attribute.
class SeatAtLocation implements Serializable{
	private static final long serialVersionUID = 42L;
	public static enum SeatStatusAtLocation{
		RESERVED,
		FREE;
	}
	private String location;
	private SeatStatusAtLocation seatStatus;
	
	public SeatAtLocation(String location) {
		this.location = location;
		this.seatStatus = SeatStatusAtLocation.FREE;
	}
	public SeatAtLocation(SeatAtLocation seatAtLocation) {
		this.location = seatAtLocation.location;
		this.seatStatus = seatAtLocation.seatStatus;
	}
	public String getLocation() {
		return this.location;
	}
	public void reserveSeatAtLocation() {
		this.seatStatus = SeatStatusAtLocation.RESERVED;
	}
	public void unReserveSeatAtLocation() {
		this.seatStatus = SeatStatusAtLocation.FREE;
	}
	public boolean isSeatAtLocationReserved() {
		return this.seatStatus == SeatStatusAtLocation.RESERVED;
	}
	
	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof SeatAtLocation))
            return false; 
        SeatAtLocation seatToCheck = (SeatAtLocation)o;
        if (! this.location.equals(seatToCheck.location))
        	return false;
        if (! this.seatStatus.equals(seatToCheck.seatStatus))
        	return false;
        return true;
	}
	public String toString() {
		return location + " : " + seatStatus.toString();
	}
}


class SimpleDate implements Serializable{
	private static final long serialVersionUID = 42L;
	private int date;
	private int month;
	private int year;
	
	public static class DateComparator implements Comparator<SimpleDate>{
		public int compare(SimpleDate d1, SimpleDate d2) {
			if (d1.year != d2.year) 
				return d1.year - d2.year;
			if (d1.month != d2.month) 
				return d1.month - d2.month;
			if (d1.date != d2.date) 
				return d1.date - d2.date;
			return 0;
			
		}
	}
	
	public SimpleDate(int date, int month, int year) {
		this.date = date;
		this.month = month;
		this.year = year;
	}
	
	public static SimpleDate getCurrentDate() {
		LocalDateTime now = LocalDateTime.now();
		
		int currentDate = Integer.parseInt((DateTimeFormatter.ofPattern("dd").format(now)));
		int currentMonth = Integer.parseInt((DateTimeFormatter.ofPattern("MM").format(now)));
		int currentYear = Integer.parseInt((DateTimeFormatter.ofPattern("yyyy").format(now)));
		
		return new SimpleDate(currentDate,currentMonth,currentYear);
	}
	
	
	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof SimpleDate))
            return false; 
        SimpleDate dateToCheck = (SimpleDate)o;
        if (this.date != dateToCheck.date)
        	return false;
        if (this.month != dateToCheck.month)
        	return false;
        if (this.year != dateToCheck.year)
        	return false;
        return true;
	}
	public String toString() {
		return Integer.toString(date)+"/"+Integer.toString(month)+"/"+Integer.toString(year);
	}
}

class SimpleTime implements Serializable{
	private static final long serialVersionUID = 42L;
	private int hours;
	private int minutes;
	
	public static class TimeComparator implements Comparator<SimpleTime>{
		public int compare(SimpleTime d1, SimpleTime d2) {
			if (d1.hours != d2.hours) 
				return d1.hours - d2.hours;
			if (d1.minutes != d2.minutes) 
				return d1.minutes - d2.minutes;
			return 0;
		}
	}
	
	public SimpleTime(int hours, int minutes) {
		this.hours = hours;
		this.minutes = minutes;
	}
	public int getHours() {
		return this.hours;
	}
	public int getMinutes() {
		return this.minutes;
	}
	public static SimpleTime getCurrentTime() 
	{
		LocalDateTime now = LocalDateTime.now();
		
		int currentHours = Integer.parseInt((DateTimeFormatter.ofPattern("HH").format(now)));
		int currentMinutes = Integer.parseInt((DateTimeFormatter.ofPattern("mm").format(now)));
		return new SimpleTime(currentHours,currentMinutes);
	}
	
	public boolean equals(Object o) {
		if (o == this)
            return true; 
        if (!(o instanceof SimpleTime))
            return false; 
        SimpleTime timeToCheck = (SimpleTime)o;
        if (this.hours != timeToCheck.hours)
        	return false;
        if (this.minutes != timeToCheck.minutes)
        	return false;
        return true;
	}
	public String toString() {
		return Integer.toString(hours)+" : "+Integer.toString(minutes);
	}
}
