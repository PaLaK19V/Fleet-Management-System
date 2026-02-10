package fleet.functions;

// Custom exception for fleet-specific errors
public class FleetException extends Exception {
    public FleetException(String msg) { super(msg); }
}