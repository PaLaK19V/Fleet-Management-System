package fleet.functions;

import fleet.users.Authenticatable;
import fleet.users.User;
import java.util.Scanner;

// Handles credential verification
public class LoginManager {
    private User[] users;

    public LoginManager(User[] users) { this.users = users; }

    public User login(Scanner sc) {
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        for (User u : users) {
            if (u.getUsername().equals(user) && u instanceof Authenticatable) {
                if (((Authenticatable) u).checkPassword(pass)) {
                    System.out.println("Access Granted.");
                    return u;
                }
            }
        }
        System.out.println("Invalid Credentials.");
        return null;
    }
}