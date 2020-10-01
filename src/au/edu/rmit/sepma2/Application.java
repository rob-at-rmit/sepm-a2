package au.edu.rmit.sepma2;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Main application class for Software Engineering Project Management
 * Study Period 3 2020, Group 12
 * Assignment 2
 * 
 * @author Rob Beardow s3641721@student.rmit.edu.au
 * @author Tyson Horsewell ...
 * @author Jordon Edmondson ... 
 *
 */
public class Application {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    private final Set<User> users = new HashSet<>();

    /**
     * Main entry point for the IT ticketing system.
     */
    public static void main(final String[] args) {
        
        final Application app = new Application();
        app.handleMainMenu();
        
    }
    
    protected Application() {
        initDefaultUserDatabase();
    }
    
    private void handleMainMenu() {
        String selection;
        do {
            System.out.println("----- IT Ticketing System -----");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("3. Reset Password");
            System.out.println("4. Exit");
            System.out.println();
            selection = scanner.nextLine();
            System.out.println();
            if (selection.length() != 1) {
                System.err.println("Error - invalid selection");
            }
            else {
                switch (selection) {
                    case "1": 
                        System.out.println("You selected login");
                        break;
                    case "2": 
                        System.out.println("You selected create account");
                        break;
                    case "3": 
                        System.out.println("You selected reset password");
                        break;
                    case "4": 
                        System.out.println("Exiting...");
                        break;
                }
            }
        }
        while (!selection.contentEquals("4"));
    }
    
    private void initDefaultUserDatabase() {
        users.add(new User("hstyles", "password", "Harry", "Styles", Role.TECHNICIAN_LEVEL1));
        users.add(new User("nhoran", "password", "Niall", "Horan", Role.TECHNICIAN_LEVEL1));
        users.add(new User("lpayne", "password", "Liam", "Payne", Role.TECHNICIAN_LEVEL1));
        users.add(new User("ltomlinson", "password", "Louis", "Tomlinson", Role.TECHNICIAN_LEVEL2));
        users.add(new User("zmalik", "password", "Zayne", "Malik", Role.TECHNICIAN_LEVEL2));
    }
    
    private enum Role {
        STAFF, 
        TECHNICIAN_LEVEL1,
        TECHNICIAN_LEVEL2
    }
    
    protected static class User {

        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private Role role;

        protected User(
            final String username, 
            final String password, 
            final String firstName, 
            final String lastName, 
            final Role role
        ) {
            super();
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public Role getRole() {
            return role;
        }

    }
    
    
}