package fleet.functions;

public class Vehicle {
    private int id;
    private String model;
    private String status;
    private double fuelLevel;
    private double efficiency; // KM per Liter

    // Nested class for vehicle-specific logs
    public class MaintenanceLog {
        String log;
        public MaintenanceLog(String l) { log = l; }
    }

    // Constructor for file loading
    public Vehicle(int id, String model, String status, double fuel, double eff) {
        this.id = id;
        this.model = model;
        this.status = status;
        this.fuelLevel = fuel;
        this.efficiency = eff;
    }

    public void displayDetails() {
        System.out.println("  [Vehicle " + id + "] " + model + " | " + status + " | Fuel: " + fuelLevel + "L | Eff: " + efficiency);
    }

    public double calculateFuelNeeded(double distance) {
        return distance / efficiency;
    }

    public int getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public double getFuelLevel() { return fuelLevel; }
    public double getEfficiency() { return efficiency; }
    public String getModel() { return model; }
}