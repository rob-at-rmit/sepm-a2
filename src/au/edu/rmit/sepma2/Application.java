package au.edu.rmit.sepma2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main application class for Software Engineering Project Management Study Period 3
 * 2020, Group 12 Assignment 2
 * 
 * @author Rob Beardow ------> s3641721@student.rmit.edu.au
 * @author Tyson Horsewell --> s3799530@student.rmit.edu.au
 * @author Jordon Edmondson -> s3779499@student.rmit.edu.au
 *
 */
public class Application
{

   private Scanner scanner = new Scanner(System.in);

   private Set<User> users = new HashSet<>();
   private List<Ticket> tickets = new ArrayList<>();
   private User currentUser = null;
   private final int interfaceWidth = 51;
   private final String interfaceDash = "-";
   private final String lD = interfaceDash + interfaceDash; // leading Dash

   /**
    * Main entry point for the IT ticketing system.
    */
   public static void main(final String[] args)
   {
      Application app = new Application();
      app.handleMainMenu();
   }

   public Application()
   {
      initDefaultUserDatabase();
   }

   /**
    * Just shows the first menu level where someone can login, create account and
    * rest their password
    */
   private void handleMainMenu()
   {
      int selection;
      do
      {
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                    "IT Ticketing System"));
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         System.out.println(lD+" 1. Login");
         System.out.println(lD+" 2. Create Account");
         System.out.println(lD+" 3. Reset Password");
         System.out.println(lD+" 4. Exit");
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         selection = getIntInput("Your Selection: ", 1, 2, 3, 4);
         System.out.println();
         switch (selection)
         {
            case 1:
               userLogin();
               currentUser = null;
               break;
            case 2:
               createUser();
               break;
            case 3:
               resetPassword();
               break;
            case 4:
               printAlert("Exiting...");
               break;
         }
      } while (selection != 4);
   }

   /**
    * the login form
    */
   private void userLogin()
   {
      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                 "IT Ticketing System", "User Login"));
      System.out.println(buildDashes(interfaceDash, interfaceWidth));

      String id = getStringInput("ID: ");
      // get the user that has that ID
      User user = findUserByID(id);

      if (id.equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername()))
      {
         // if the username matches get the password
         if (user.getPassword().equals(getStringInput("Password: ")))
         {
            currentUser = user;
            staffloginArea();
         }
         else
         {
            printErr("The Password is incorrect.");
         }
      }
      else
      {
         printErr("There is no user by that username in the system.");
      }
   }

   /**
    * Show the staff login area which is a form that includes their tickets and
    * ability to add a new one
    */
   private void staffloginArea()
   {
      int selection, i = 0, j = 3;
      List<Integer> allowedMenuItems = new ArrayList<>(Arrays.asList(1, 2));
      List<Ticket> myTickets = new ArrayList<>();

      // get tickets
      // add numbers to allowed menu items

      do
      {
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                    "IT Ticketing System",
                                    "Your Tickets - " + currentUser.getFirstName()));
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         System.out.println(lD+" 1. Create a New Ticket");
         System.out.println(lD+" 2. Logout");
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         if (!tickets.isEmpty())
         {
            i = 0;
            for (Ticket ticket : tickets)
            {
               if (ticket.getUserName().equalsIgnoreCase(currentUser.getUsername()))
               {
                  myTickets.add(i, ticket);
                  System.out.println(lD+" "+ (i + j) + ". " + ticket.getSubmissionDate() +
                                     " - " +
                                     ticket.getDescription());
                  allowedMenuItems.add(j + i);
                  i++;
               }
            }
            System.out.println(buildDashes(interfaceDash, interfaceWidth));
         }
         selection = getIntInput("Your Selection: ",
                                 allowedMenuItems.toArray(new Integer[0]));
         System.out.println();
         switch (selection)
         {
            case 1:
               createTicket();
               break;
            case 2:
               // logout
               break;
            default:
               showTicket(myTickets.get(selection - j));
         }
      } while (selection != 2);

   }

   /**
    * Form to create a ticket
    */
   private void createTicket()
   {
      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                 "IT Ticketing System",
                                 "New Ticket"));
      System.out.println(buildDashes(interfaceDash, interfaceWidth));

      String fName = getStringInput("First Name: ");
      String lName = getStringInput("Last Name: ");

      // make sure the user name is unique
      String id;
      boolean exists;
      User user;
      do
      {
         id = getStringInput("ID: ");
         user = findUserByID(id);
         exists = id
                  .equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername());
         if (!exists)
         {
            printErr("That username does not exist.");
         }
      } while (!exists);

      String contactNum =
               getStringInput("Contact Number (02777 4444): ", "^\\+?[0-9 ]{8,14}$");

      String submissionDate = getStringInput("Submission date (YYYY/MM/DD): ",
                                             "^[0-9]{4}/[0-9]{2}/[0-9]{2}$");

      String description = getStringInput("Description: ");

      String severity =
               getStringInput("Severity (LOW/MED/HIGH): ", "^(LOW|MED|HIGH)$");

      Ticket t = new Ticket(fName, lName, id, contactNum, submissionDate,
                            description, severity);
      t.setIsOpen(true);
      if (tickets.add(t))
      {
         printAlert("New Open Ticket has been added to the system.");
      }
   }

   /**
    * show an individual ticket after created to see progress
    */
   private void showTicket(Ticket t)
   {
      int selection;
      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                 "IT Ticketing System",
                                 "Ticket"));
      System.out.println(buildDashes(interfaceDash, interfaceWidth));

      System.out.println("  "+t.getFirstName() + " " + t.getLastName() + " (" +
                         t.getUserName() + ")");
      System.out.println("  Phone: " + t.getContactNumber());
      System.out.println("  Ticket Submitted: " + t.getSubmissionDate());
      System.out.println("  Ticket Description: \n" + t.getDescription());
      System.out.println();
      System.out.println("  Severity: " + t.getSeverity());

      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      do
      {
         selection =
                  getIntInput("Type \"1\" to return to admin: ", 1);
         System.out.println();
      } while (selection != 1);
   }

   /**
    * Form to create a user
    */
   private void createUser()
   {
      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                 "IT Ticketing System",
                                 "New User"));
      System.out.println(buildDashes(interfaceDash, interfaceWidth));

      String fName = getStringInput("First Name: ");
      String lName = getStringInput("Last Name: ");

      // make sure the user name is unique
      String id;
      boolean exists;
      User user;
      do
      {
         id = getStringInput("ID: ");
         user = findUserByID(id);
         exists = id
                  .equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername());
         if (exists)
         {
            printErr("That username is taken try another.");
         }
      } while (exists);

      String p = getStringInput("Password: ");
      Role r = getRoleInput("Role (1 = Staff, 2 = Tech level 1, 3 = Tech level 2): ");

      if (users.add(new User(id, p, fName, lName, r)))
      {
         printAlert("New User has been added to the system.");
      }

   }

   /**
    * Form to reset the password
    */
   private void resetPassword()
   {
      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                 "IT Ticketing System",
                                 "Reset Password"));
      System.out.println(buildDashes(interfaceDash, interfaceWidth));

      String id = getStringInput("ID: ");
      User user = findUserByID(id);

      // if the username exists then allow for the password to be changed
      if (id.equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername()))
      {
         user.setPassword(getStringInput("Password: "));
         printAlert("The password has been reset. When you login next time you will need to use your new password.");
      }
      else
      {
         printErr("There is no user by that username in the system.");
      }
   }

   /**
    * Get a string input from the command-line basically every input method uses this
    * as a base and validates the data before returning it
    * 
    * @param label
    *           the description of what needs to be input
    * @return a String of what was input
    */
   private String getStringInput(String label)
   {
      String s;
      do
      {
         System.out.print(lD +" "+ label);
         s = scanner.nextLine();
         System.out.println();
      } while (Objects.isNull(s) || s.isEmpty());
      return s;
   }

   /**
    * Get a string input from the command-line
    * 
    * @param label
    *           the description of what needs to be input regEx a regular expression
    *           for a match validation
    * @return a String of what was input
    */
   private String getStringInput(String label, String regEx)
   {
      String s;

      Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
      Matcher matcher;
      boolean isMatch;

      do
      {
         s = getStringInput(label);
         matcher = pattern.matcher(s);
         isMatch = matcher.find();
         if (!isMatch)
         {
            printErr("The text is not in the correct format.");
         }
      } while (!isMatch);
      return s;
   }

   /**
    * Set the role
    * 
    * @param label
    *           the description of what needs to be input
    * @return the role that is selected
    * 
    * @see au.edu.rmit.sepma2.Role
    */
   private Role getRoleInput(String label)
   {
      int s;
      Role r = null;
      do
      {
         s = getIntInput(label, 1, 2, 3);
         switch (s)
         {
            case 1:
               r = Role.STAFF;
               break;
            case 2:
               r = Role.TECHNICIAN_LEVEL1;
               break;
            case 3:
               r = Role.TECHNICIAN_LEVEL2;

         }
      } while (Objects.isNull(r));
      return r;
   }

   /**
    * gets an integer input from the user
    * 
    * @param label
    *           the description of what needs to be input
    * @param allowedIntegers
    *           this is an array of integers that is allowed to be validated against
    * @return
    */
   private int getIntInput(String label, Integer... allowedIntegers)
   {
      String s;
      int val;

      if (allowedIntegers.length == 0)
      {
         s = getStringInput(label);
         val = Integer.parseInt(s);
      }
      else
      {
         boolean inArray = false;
         do
         {
            s = getStringInput(label);
            val = Integer.parseInt(s);
            for (int n : allowedIntegers)
            {
               if (n == val)
               {
                  inArray = true;
                  break;
               }
            }
            if (!inArray)
            {
               printErr("That is an invalid selection.");
            }
         } while (!inArray);
      }
      return val;
   }

   /**
    * gets an integer input from the user (we don't care about which)
    * 
    * @param label
    *           the description of what needs to be input
    * @return an int
    */
   private int getIntInput(String label)
   {
      Integer[] p = {};
      return getIntInput(label, p);
   }

   /**
    * find the user in the users collection
    * 
    * @param id
    *           is the username of the user
    * @return the user object
    */
   private User findUserByID(String id)
   {
      for (User user : users)
      {
         if (user.getUsername().equalsIgnoreCase(id))
         {
            return user;
         }
      }
      return null;
   }

   private void initDefaultUserDatabase()
   {
      users.add(new User("hstyles", "password", "Harry", "Styles",
                         Role.TECHNICIAN_LEVEL1));
      users.add(new User("nhoran", "password", "Niall", "Horan",
                         Role.TECHNICIAN_LEVEL1));
      users.add(new User("lpayne", "password", "Liam", "Payne",
                         Role.TECHNICIAN_LEVEL1));
      users.add(new User("ltomlinson", "password", "Louis", "Tomlinson",
                         Role.TECHNICIAN_LEVEL2));
      users.add(new User("zmalik", "password", "Zayne", "Malik",
                         Role.TECHNICIAN_LEVEL2));

      users.add(new User("bob", "pwd", "Bobby", "Bob-Bob",
                         Role.STAFF));

      tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "0496323145", "2020/7/7",
                             "This is just some test data 0.", "LOW"));
      tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "0496323145", "2020/10/7",
                             "This is just some test data 1.", "LOW"));
      tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "0496323145", "2020/10/4",
                             "This is just some test data 2.", "MED"));
      tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "0496323145", "2020/10/5",
                             "This is just some test data 3.", "MED"));
      tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "0496323145", "2020/10/6",
                             "This is just some test data 4.", "HIGH"));
      tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "0496323145", "2020/10/8",
                             "This is just some test data 5.", "HIGH"));
   }

   /**
    * Displays an error message in the correct format
    * 
    * @param message
    *           a string of the error message that needs to be displayed
    */
   private void printErr(String message)
   {
      message = "  **  ERROR: " + message + " **";
      System.out.println("  "+ buildDashes("*", message.length()) +
                         "\n" + message + "\n" +
                         "  "+ buildDashes("*", message.length()));
      System.out.println();
   }
   
   private void printAlert(String message) {
      System.out.println(">> "+ message +" <<");
      System.out.println();
   }
   
   private String buildMenu(String dash, int length, String... titles) {
      String s = "", tLine;
      int tLen;
      for (String title : titles) {
         tLen = title.length();
         tLine = buildDashes(dash, (length - tLen - 2)/ 2);
         tLine += " " + title + " ";
         tLine += buildDashes(dash, length - tLine.length());
         tLine += "\n";
         s += tLine;
      }
      
      return s;
   }
   private String buildDashes(String dash, int length) {
      String s = "";
      for (int i = 0; i < length; i++ ) {
         s += dash;
      }
      return s;
   }

}

enum Role
{
   STAFF, TECHNICIAN_LEVEL1, TECHNICIAN_LEVEL2;
}

/**
 * The User class
 */
class User
{

   private final String username; // this will not change
   private String password;
   private String firstName;
   private String lastName;
   private Role role;

   public User(
               final String username,
               final String password,
               final String firstName,
               final String lastName,
               final Role role)
   {
      super();
      this.username = username;
      this.password = password;
      this.firstName = firstName;
      this.lastName = lastName;
      this.role = role;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUsername()
   {
      return username;
   }

   public String getPassword()
   {
      return password;
   }

   public String getFirstName()
   {
      return firstName;
   }

   public String getLastName()
   {
      return lastName;
   }

   public Role getRole()
   {
      return role;
   }

}

class Ticket
{

   private final String firstName;
   private final String lastName;
   private final String username; // username
   private final String contactNumber;
   private final String submissionDate;
   private final String description;
   private boolean isOpen; // status
   private String severity;

   // Fill in the ticket
   public Ticket(String firstName, String lastName, String id,
                 String contactNumber, String submissionDate, String description,
                 String severity)
   {
      this.firstName = firstName;
      this.lastName = lastName;
      this.username = id; // username
      this.contactNumber = contactNumber;
      this.submissionDate = submissionDate;
      this.description = description;
      this.severity = severity;
      this.isOpen = false;
   }

   public boolean isOpen()
   {
      return isOpen;
   }

   public void setIsOpen(boolean isOpen)
   {
      this.isOpen = isOpen;
   }

   public String getSeverity()
   {
      return severity;
   }

   public String getFirstName()
   {
      return firstName;
   }

   public String getLastName()
   {
      return lastName;
   }

   public String getUserName()
   {
      return username;
   }

   public String getContactNumber()
   {
      return contactNumber;
   }

   public String getSubmissionDate()
   {
      return submissionDate;
   }

   public String getDescription()
   {
      return description;
   }

}
