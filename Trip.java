package fleet.functions;

// Data model for a completed or active trip
public class Trip {
    private int tripId;
    private int vehicleId;
    private String driverName;
    private double distance;

    public Trip(int id, int vid, String driver, double dist) {
        this.tripId = id;
        this.vehicleId = vid;
        this.driverName = driver;
        this.distance = dist;
    }

    public void displayDetails() {
        System.out.println("  Trip #" + tripId + " | Driver: " + driverName + " | Vehicle: " + vehicleId + " | " + distance + "km");
    }

    public String getDriverName() { return driverName; }
    public int getTripId() { return tripId; }
    public int getVehicleId() { return vehicleId; }
    public double getDistance() { return distance; }
}