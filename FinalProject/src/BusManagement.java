

import java.util.LinkedList;


public class BusManagement {
	private BusStorage storageMechanism;

	public BusManagement(BusStorage storageMechanism) {
		this.storageMechanism = new BusFileStorage();
	}
	
	public void addNewBus(BusOperator operator, String name, String plateNumber,LinkedList<BusStop> route,int maxNumberOfSeats, BusType busType) throws InvalidBusException {
		Bus newBus = new Bus(operator, name, plateNumber, route, maxNumberOfSeats, busType);
		storageMechanism.storeBus(newBus);
	}
	public void addNewBus(Bus newBus) {
		storageMechanism.storeBus(newBus);
	}
	public void updateBus(Bus newBus) {
		storageMechanism.updateBus(newBus);
	}
	
	public LinkedList<Bus> getAllBusses(){
		return storageMechanism.getAllBuses();
	}
	public LinkedList<Bus> getAllBusses(BusOperator operator){
		return storageMechanism.getAllBuses(operator);
	}
	public Bus getUpdatedBus(Bus oldBus) {
		return storageMechanism.getBus(oldBus.getPlateNumber());
	}
	public LinkedList<ScheduledBus> getAllScheduledBusses(String source, String destination, SimpleDate dateOfTravel) {
		LinkedList<ScheduledBus> scheduledBusses = new LinkedList<ScheduledBus>();
		for (Bus bus : getAllBusses()) {
			if (bus.isOnRoute(source, destination)){
				for(ScheduledBus scheduledBus : bus.getScheduledBuses()) {
					if (dateOfTravel.equals(scheduledBus.getDate())){
						scheduledBusses.add(scheduledBus);							
					}
				}
			}
		}
		return scheduledBusses;
	}
	public LinkedList<ScheduledBus> getAvailableScheduledBusses(String source, String destination, SimpleDate dateOfTravel) {
		LinkedList<ScheduledBus> availablescheduledBusses = new LinkedList<ScheduledBus>();
		for (ScheduledBus scheduledBus : getAllScheduledBusses(source, destination, dateOfTravel)) {
			if (scheduledBus.hasAvailableSeats(source, destination)){
				availablescheduledBusses.add(scheduledBus);	
			}
		}
		return availablescheduledBusses;
	}
	
	public String toString() {
		String output = "";
		for (Bus bus : getAllBusses()) {
			output = output + bus.toString() + "\n";
		}
		return output;
	}
	
//	public void cancelBus(Bus bus) {
//	int index = this.busses.indexOf(bus);
//	(this.busses.get(index)).cancelBus();
//}
}