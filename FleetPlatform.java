package fleet.functions;

import fleet.users.*;
import fleet.filereaders.*;
import fleet.filewriters.DataWriter;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

// The central controller for the Fleet Management System
public class FleetPlatform {

    // In-memory databases
    static User[] users;
    static Vehicle[] vehicles;
    static Trip[] trips;
    static Route[] routes;

    // File path constants
    static final String USERS_FILE = "users.txt";
    static final String VEHICLES_FILE = "vehicles.txt";
    static final String ROUTES_FILE = "routes.txt";
    static final String REPORTS_FILE = "reports.txt";
    static final String TRIPS_FILE = "trips.txt";

    // Static block to load all data before the app starts
    static {
        users = new UserReader().readUsers(USERS_FILE);
        vehicles = new VehicleReader().readVehicles(VEHICLES_FILE);
        routes = new RouteReader().readRoutes(ROUTES_FILE);
        trips = new TripReader().readTrips(TRIPS_FILE);
        System.out.println("System Loaded: " + users.length + " Users, " + vehicles.length + " Vehicles.");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LoginManager loginMgr = new LoginManager(users);
        
        while (true) {
            System.out.println("\n=== Fleet Management System ===");
            User currentUser = loginMgr.login(sc);
            
            if (currentUser != null) {
                if (currentUser instanceof Driver) {
                    driverMenu((Driver) currentUser, sc);
                } else if (currentUser instanceof Dispatcher) {
                    dispatcherMenu(sc);
                } else if (currentUser instanceof Admin) {
                    adminMenu(sc);
                }
                System.out.println("Logging out...");
            }
        }
    }

    // --- Role-Based Menus ---

    private static void driverMenu(Driver driver, Scanner sc) {
        while (true) {
            driver.displayMenu();
            System.out.println("1. View My Trips");
            System.out.println("2. Report Issue");
            System.out.println("3. View All Reports");
            System.out.println("4. Logout");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();

            if (ch == 1) {
                System.out.println("--- My Trip History ---");
                boolean found = false;
                for (Trip t : trips) {
                    if (t.getDriverName().equalsIgnoreCase(driver.getName())) {
                        t.displayDetails();
                        found = true;
                    }
                }
                if (!found) System.out.println("No trips found.");
            } else if (ch == 2) {
                System.out.print("Describe the issue: ");
                String issue = sc.nextLine();
                DataWriter.appendReport(REPORTS_FILE, "REPORT [" + driver.getName() + "]: " + issue);
                System.out.println("Report saved to system.");
            } else if (ch == 3) {
                readAndPrintFile(REPORTS_FILE);
            } else if (ch == 4) break;
        }
    }

    private static void dispatcherMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Dispatcher Actions ---");
            System.out.println("1. View Routes");
            System.out.println("2. Predict Fuel & Alerts");
            System.out.println("3. Assign Trip");
            System.out.println("4. Logout");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();

            if (ch == 1) {
                for (Route r : routes) r.display();
            } else if (ch == 2) {
                predictFuel(sc);
            } else if (ch == 3) {
                try { assignNewTrip(sc); } 
                catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
            } else if (ch == 4) break;
        }
    }

    private static void adminMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. View All Users");
            System.out.println("2. View Fleet Status");
            System.out.println("3. View Reports");
            System.out.println("4. Logout");
            int ch = sc.nextInt(); sc.nextLine();
            
            if (ch == 1) for(User u : users) System.out.println(u.getName() + " [" + u.getRole() + "]");
            else if (ch == 2) for(Vehicle v : vehicles) v.displayDetails();
            else if (ch == 3) readAndPrintFile(REPORTS_FILE);
            else if (ch == 4) break;
        }
    }

    // --- Logic Functions ---

    private static void assignNewTrip(Scanner sc) throws Exception {
        System.out.print("Enter Vehicle ID: ");
        int vid = sc.nextInt(); sc.nextLine();
        System.out.print("Enter Driver Username: ");
        String dUser = sc.nextLine();
        System.out.print("Enter Distance (km): ");
        double dist = sc.nextDouble();

        Vehicle v = null;
        for(Vehicle veh : vehicles) if(veh.getId() == vid) v = veh;

        Driver d = null;
        for(User u : users) if(u.getUsername().equals(dUser) && u instanceof Driver) d = (Driver)u;

        if(v == null || d == null) throw new FleetException("Invalid Vehicle or Driver");
        if(!v.getStatus().equals("IDLE")) throw new FleetException("Vehicle is " + v.getStatus());
        if(!d.getStatus().equals("IDLE")) throw new FleetException("Driver is " + d.getStatus());

        // Create Trip
        Trip t = new Trip(trips.length + 1, vid, d.getName(), dist);
        
        // Update Statuses
        v.setStatus("ON_TRIP");
        d.setStatus("ON_TRIP");

        // Save to Memory
        trips = Arrays.copyOf(trips, trips.length + 1);
        trips[trips.length - 1] = t;

        // Save to Files
        DataWriter.writeTrips(TRIPS_FILE, trips);
        DataWriter.writeVehicles(VEHICLES_FILE, vehicles);
        DataWriter.writeUsers(USERS_FILE, users);

        System.out.println("Trip Assigned. Statuses updated and saved.");
    }

    private static void predictFuel(Scanner sc) {
        System.out.print("Enter Route ID (e.g., R1): ");
        String rid = sc.nextLine();
        System.out.print("Enter Vehicle ID: ");
        int vid = sc.nextInt();

        Route r = null;
        for(Route rt : routes) if(rt.getRouteId().equals(rid)) r = rt;

        Vehicle v = null;
        for(Vehicle veh : vehicles) if(veh.getId() == vid) v = veh;

        if(r != null && v != null) {
            double required = v.calculateFuelNeeded(r.getDistance());
            System.out.println("Fuel Required: " + required + " L");
            System.out.println("Current Fuel: " + v.getFuelLevel() + " L");

            if(v.getFuelLevel() < required) {
                System.out.println("!!! ALERT: INSUFFICIENT FUEL !!!");
            } else {
                System.out.println("Status: Good to go.");
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private static void readAndPrintFile(String filename) {
        try (Scanner fSc = new Scanner(new File(filename))) {
            System.out.println("--- Report Log ---");
            while (fSc.hasNextLine()) System.out.println(fSc.nextLine());
        } catch (Exception e) { System.out.println("File empty or missing."); }
    }
}