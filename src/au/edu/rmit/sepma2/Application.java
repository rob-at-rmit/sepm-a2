package au.edu.rmit.sepma2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
   private final int interfaceWidth = 80;
   private final String interfaceDash = "-";
   private final String lD = interfaceDash + interfaceDash; // leading Dash
   
   private final static String DATE_FORMAT = "dd/MM/yyyy";

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
      int selection, i = 0, j = 5;
      List<Integer> allowedMenuItems = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
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
         System.out.println(lD+" 2. Show My Open Tickets");
         System.out.println(lD+" 3. Show Closed Tickets");
         System.out.println(lD+" 4. Logout");
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         selection = getIntInput("Your Selection: ", allowedMenuItems.toArray(new Integer[0]));
         System.out.println();
         switch (selection)
         {
            case 1:
               createTicket();
               break;
             case 2:
               showOpenTickets();
               break;
            case 3:
               showClosedTickets();
               break;
            case 4:
               // logout
               break;
         }
      } while (selection != 4);

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
      String username;
      boolean exists;
      User user;
      do
      {
         username = getStringInput("Username : ");
         user = findUserByID(username);
         exists = username
                  .equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername());
         if (!exists)
         {
            printErr("That username does not exist.");
         }
      } while (!exists);

      final String contactNum =
               getStringInput("Contact Number (02777 4444): ", "^\\+?[0-9 ]{8,14}$");
      final Date submissionDate = getDateInput("Submission date (DD/MM/YYYY): ");
      final String description = getStringInput("Description: ");
      final Severity severity = Severity.valueOf(
          getStringInput("Severity (LOW/MED/HIGH): ", "^(LOW|MED|HIGH)$").toUpperCase()
      );

      final Ticket t = new Ticket(fName, lName, username, null, contactNum, submissionDate,
                            description, severity);
      t.open();
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
                         t.getSubmitterUserName() + ")");
      System.out.println("  Phone: " + t.getContactNumber());
      System.out.println("  Ticket Submitted: " + t.getSubmissionDate());
      System.out.println("  Ticket Description: \n" + t.getDescription());
      System.out.println();
      System.out.println("  Severity: " + t.getSeverity());

      System.out.println(buildDashes(interfaceDash, interfaceWidth));

      System.out.println("Type \"1\" to return to admin: ");
      System.out.println("Type \"2\" to change severity of ticket: ");
      System.out.println("Type \"3\" to close ticket: ");

      selection = getIntInput("Enter selection: ");
      switch (selection)
      {
         case 1:
             break;
         case 2: 
             changeSeverity(t);
             break;
         case 3:
             t.close();
             break;
      }
   }

   private void showOpenTickets(){
       int selection;
       do
       {
           final List<Ticket> myOpenTickets = new ArrayList<>();
           for (final Ticket ticket : tickets)
           {
               if (ticket.getSubmitterUserName().equalsIgnoreCase(currentUser.getUsername()) && ticket.isOpen())
               {
                   myOpenTickets.add(ticket);
               }
           }
           System.out.println(buildMenu(interfaceDash, interfaceWidth, "My Open Tickets"));
           System.out.println(buildTicketTable(myOpenTickets));
           final Integer allowable[] = IntStream.rangeClosed(0, myOpenTickets.size()).boxed().toArray(Integer[]::new);
           selection = getIntInput("Your selection (type \"0\" to return to admin): ", allowable);
           if (selection != 0) {
               showTicket(myOpenTickets.get(selection - 1));               
           }
           System.out.println();
       } while (selection != 0);
   }

   /**
    * Show all closed tickets in a table. No user actions on closed tickets in this sprint 
    * so the only menu option is to return to admin.
    */
   private void showClosedTickets(){
     int selection;
     final List<Ticket> closedTickets = new ArrayList<>();
     for (final Ticket ticket : tickets)
     {
         if (!ticket.isOpen()) 
         {
             closedTickets.add(ticket);
         }
     }
     System.out.println(buildMenu(interfaceDash, interfaceWidth, "Closed Tickets"));
     System.out.println(buildTicketTable(closedTickets));
     do
     {
       selection = getIntInput("Type \"0\" to return to admin: ", 0);
       System.out.println();
     } while (selection != 0);
   }

   private void changeSeverity(final Ticket t)
   {
      String selection;

      System.out.println("Severity is Currently: " + t.getSeverity());
      System.out.println("What would you like to change the severity to?");
      System.out.println("LOW");
      System.out.println("MED");
      System.out.println("HIGH");

      selection = getStringInput("Enter Selection: ");
  
      if (selection.equalsIgnoreCase(Severity.LOW.name()))
      {
         t.setSeverity(Severity.LOW);
      }
      else if (selection.equalsIgnoreCase(Severity.MED.name()))
      {
         t.setSeverity(Severity.MED);
      }
      else if (selection.equalsIgnoreCase(Severity.HIGH.name()))
      {
         t.setSeverity(Severity.HIGH);
      }
      else
      {
         System.out.println("Enter one of the above options");
      }
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
         printAlert("The password has been reset.");
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
    * Retrieve a date type from the command line using the default parse format.
    * @param label
    * @return
    */
   private Date getDateInput(final String label)
   {
       do 
       {
           final String input = getStringInput(label);
           try {
               return parseDate(input);
           }
           catch (final ParseException e) {
               printErr("Please enter a date in the format " + DATE_FORMAT);
           }
       }
       while (true);
   }
   
   /**
    * Parse a date from a string using the default date format.
    * @param date
    * @return
    * @throws ParseException
    */
   public static Date parseDate(final String date) throws ParseException {
       final SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
       f.setLenient(false);
       return f.parse(date);
   }
   
   /**
    * Format a date as a string using the default date format.
    * @param date
    * @return
    */
   public static String formatDate(final Date date) {
       return new SimpleDateFormat(DATE_FORMAT).format(date);
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

      try 
      {
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", null, "0496323145", parseDate("07/07/2020"),
                                 "This is just some test data 0.", Severity.LOW));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", null, "0496323145", parseDate("07/10/2020"),
                                 "This is just some test data 1.", Severity.LOW));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", null, "0496323145", parseDate("04/10/2020"),
                                 "This is just some test data 2.", Severity.MED));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", null, "0496323145", parseDate("05/10/2020"),
                                 "This is just some test data 3.", Severity.MED));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", null, "0496323145", parseDate("06/10/2020"),
                                 "This is just some test data 4.", Severity.HIGH));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", null, "0496323145", parseDate("08/10/2020"),
                                 "This is just some test data 5.", Severity.HIGH));
      }
      catch (final ParseException e)
      {
          throw new RuntimeException("Error parsing date in initial data setup");
      }
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
   
   
   private String buildTicketTable(final List<Ticket> ticketsToDisplay) 
   {
       if (ticketsToDisplay.isEmpty()) {
           return (
               "No Tickets" + 
                System.lineSeparator() + 
                System.lineSeparator() + 
                buildDashes(interfaceDash, interfaceWidth)
           );
       }
       final List<String> lines = new ArrayList<>();
       final Map<String,Integer> headers = new LinkedHashMap<>();
       headers.put("ID", 5);
       headers.put("Submitter", 8);
       headers.put("F. Name", 7);
       headers.put("L. Name", 7);
       headers.put("Status", 6);
       headers.put("Sev.", 4);
       headers.put("Sub. Date", 10);
       headers.put("Desc.", 10);
       final String header = String.join(" | ", 
           headers.entrySet().stream().map(e -> padColumn(e.getKey(), e.getValue())).collect(Collectors.toList())
       ) + " |";
       lines.add(buildDashes(interfaceDash, header.length()));
       lines.add(header);
       lines.add(buildDashes(interfaceDash, header.length()));
       final int numTickets = ticketsToDisplay.size();
       for (int i = 0; i < numTickets; i++) {
           final Ticket t = ticketsToDisplay.get(i);
           final List<String> cols = new ArrayList<>();
           cols.add(padColumn("-- " + String.valueOf(i + 1) + ".", headers.get("ID")));
           cols.add(padColumn(t.getSubmitterUserName(), headers.get("Submitter")));
           cols.add(padColumn(t.getFirstName(), headers.get("F. Name")));
           cols.add(padColumn(t.getLastName(), headers.get("L. Name")));
           cols.add(padColumn(t.isOpen() ? "Open" : "Closed", headers.get("Status")));
           cols.add(padColumn(t.getSeverity().toString(), headers.get("Sev.")));
           cols.add(padColumn(t.getSubmissionDateFormatted(), headers.get("Sub. Date")));
           cols.add(padColumn(t.getDescription(), headers.get("Desc.")));
           lines.add(String.join(" | ", cols) + " |");
       }
       lines.add(buildDashes(interfaceDash, header.length()));
       return String.join(System.lineSeparator(), lines);
   }
   
   private String padColumn(final String string, final int width) {
       final String s = (string.length() > width) ? string.substring(0, width) : string;
       return String.format("%-" + width + "s", s);
   }

}

enum Role
{
   STAFF, TECHNICIAN_LEVEL1, TECHNICIAN_LEVEL2;
}

enum Severity
{
    LOW, MED, HIGH 
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
   private final String submitterUserName;
   private final String assigneeUserName;
   private final String contactNumber;
   private final Date submissionDate;
   private final String description;
   private boolean open;
   private Severity severity;
   private Date resolutionDate;

   public Ticket(final String firstName, 
                 final String lastName, 
                 final String submitterUserName,
                 final String assigneeUsername,
                 final String contactNumber, 
                 final Date submissionDate, 
                 final String description,
                 final Severity severity)
   {
      this.firstName = firstName;
      this.lastName = lastName;
      this.submitterUserName = submitterUserName;
      this.assigneeUserName = assigneeUsername;
      this.contactNumber = contactNumber;
      this.submissionDate = submissionDate;
      this.description = description;
      this.severity = severity;
   }

   public boolean isOpen()
   {
      return open;
   }
   
   public void open()
   {
       this.open = true;
   }
   
   public void close()
   {
       this.open = false;
       this.resolutionDate = new Date();
   }

   public void setSeverity(Severity severity)
   {
      this.severity = severity;
   }

   public Severity getSeverity()
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

   public String getSubmitterUserName()
   {
      return submitterUserName;
   }
   
   public String getAssigneeUserName() 
   {
       return assigneeUserName;
   }

   public String getContactNumber()
   {
      return contactNumber;
   }

   public Date getSubmissionDate()
   {
      return submissionDate;
   }
   
   public String getSubmissionDateFormatted() {
       return Application.formatDate(submissionDate);
   }

   public String getDescription()
   {
      return description;
   }

}