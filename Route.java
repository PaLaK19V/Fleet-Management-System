package fleet.functions;

// Models a fixed travel route (Ridhima's suggestion)
public class Route {
    private String routeId;
    private String name;
    private double distance;

    public Route(String id, String name, double dist) {
        this.routeId = id;
        this.name = name;
        this.distance = dist;
    }

    public void display() {
        System.out.println("  Route " + routeId + ": " + name + " (" + distance + " km)");
    }

    public String getRouteId() { return routeId; }
    public double getDistance() { return distance; }
}