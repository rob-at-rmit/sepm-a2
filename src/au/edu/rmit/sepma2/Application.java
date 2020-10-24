package au.edu.rmit.sepma2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiFunction;
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

   private final Random random = new Random();
   private Set<User> users = new HashSet<>();
   private List<Ticket> tickets = new ArrayList<>();
   private User currentUser = null;
   private final int interfaceWidth = 80;
   private final String interfaceDash = "-";
   private final String lD = interfaceDash + interfaceDash; // leading Dash
   
   private final static String DATE_FORMAT = "dd/MM/yyyy";
   private final static boolean DEBUG_OUTPUT = false;

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
            showUserMenu();
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
    * Enum which represents the potential different main user menu options and associated labels.
    * @author rob
    */
   private enum UserMenuOption
   {
       CREATE_TICKET("Create a New Ticket"),
       SHOW_OPEN_TICKETS("Show My Open Tickets"),
       SHOW_CLOSED_TICKETS("Show Closed Tickets"),
       SHOW_TICKET_REPORT("Show Ticket Report"),
       LOGOUT("Logout");
       
       private final String label;
       
       private UserMenuOption(final String label) 
       {
           this.label = label;
       }
       
       private String getLabel()
       {
           return label;
       }
       
   }
   
   /**
    * Show the staff login area which is a form that includes their tickets and
    * ability to add a new one
    */
   private void showUserMenu()
   {
      UserMenuOption selection;
      final Map<Integer,UserMenuOption> availableMenuOptions = getUserMenuOptions();
      do
      {
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                    "IT Ticketing System",
                                    "Your Tickets - " + currentUser.getFirstName()));
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         availableMenuOptions.forEach((i,l) -> System.out.println(lD + i + ". " + l.getLabel()));
         System.out.println(buildDashes(interfaceDash, interfaceWidth));
         selection = availableMenuOptions.get(
             getIntInput("Your Selection: ", availableMenuOptions.keySet().toArray(new Integer[0]))
         );
         System.out.println();
         switch (selection)
         {
            case CREATE_TICKET:
               createTicket();
               break;
             case SHOW_OPEN_TICKETS:
               showOpenTickets();
               break;
            case SHOW_CLOSED_TICKETS:
               showClosedTickets();
               break;
            case SHOW_TICKET_REPORT:
               handleTicketReport();
            case LOGOUT:
               // logout
               break;
         }
      } while (!selection.equals(UserMenuOption.LOGOUT));

   }
   
   /**
    * Determines the appropriate user menu options based on role of current user.
    * @return
    */
   private Map<Integer,UserMenuOption> getUserMenuOptions() 
   {
       final Map<Integer,UserMenuOption> options = new HashMap<>();
       options.put(1, UserMenuOption.CREATE_TICKET);
       options.put(2, UserMenuOption.SHOW_OPEN_TICKETS);
       if (currentUser.getRole().equals(Role.STAFF))
       {
           options.put(3, UserMenuOption.SHOW_TICKET_REPORT);
           options.put(4, UserMenuOption.LOGOUT);
       }
       else
       {
           options.put(3, UserMenuOption.SHOW_CLOSED_TICKETS);
           options.put(4, UserMenuOption.SHOW_TICKET_REPORT);
           options.put(5, UserMenuOption.LOGOUT);
       }
       return options;
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
         username = getStringInput("Username: ");
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
      
      final String assignee = findAssigneeUserName(severity);
      final Ticket t = new Ticket(fName, lName, username, assignee, 
                                  contactNum, submissionDate, description, severity);
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
       TicketMenuOption selection;
       final Map<Integer,TicketMenuOption> availableMenuOptions = getTicketMenuOptions();

      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.print(buildMenu(interfaceDash, interfaceWidth, 
                                 "IT Ticketing System",
                                 "Ticket"));
      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      System.out.println("  "+t.getFirstName() + " " + t.getLastName() + " (" +
                         t.getSubmitterUserName() + ")");
      System.out.println("  Phone: " + t.getContactNumber());
      System.out.println("  Ticket Submitted: " + formatDate(t.getSubmissionDate()));
      System.out.println("  Ticket Description: \n" + t.getDescription());
      System.out.println();
      System.out.println("  Severity: " + t.getSeverity());

      System.out.println(buildDashes(interfaceDash, interfaceWidth));
      availableMenuOptions.forEach((i,l) -> System.out.println(lD + i + ". " + l.getLabel()));
      selection = availableMenuOptions.get(
            getIntInput("Enter selection: ", availableMenuOptions.keySet().toArray(new Integer[0]))
      );
      switch (selection)
      {
         case RETURN_ADMIN:
             break;
         case CHANGE_SEVERITY: 
             changeSeverity(t);
             break;
         case CLOSE_TICKET:
             t.close();
             break;
      }
   }
   
   /**
    * Enum which represents the potential different ticket menu options and associated labels.
    * @author rob
    */
   private enum TicketMenuOption {
       
       RETURN_ADMIN("Return to admin"),
       CHANGE_SEVERITY("Change severity of ticket"),
       CLOSE_TICKET("Close ticket");
       
       private final String label;
       
       private TicketMenuOption(final String label) 
       {
           this.label = label;
       }
       
       private String getLabel()
       {
           return label;
       }
   }
   
   /**
    * Determines the appropriate ticket menu options based on role of current user.
    * @return
    */
   private Map<Integer,TicketMenuOption> getTicketMenuOptions() 
   {
       final Map<Integer,TicketMenuOption> options = new HashMap<>();
       options.put(1, TicketMenuOption.CLOSE_TICKET);
       if (currentUser.getRole().equals(Role.STAFF))
       {
           options.put(2, TicketMenuOption.RETURN_ADMIN);
       }
       else
       {
           options.put(2, TicketMenuOption.CHANGE_SEVERITY);
           options.put(3, TicketMenuOption.RETURN_ADMIN);
       }
       return options;
   }

   /**
    * My tickets shows any open tickets which the current user either submitted or has assigned 
    * to them.
    */
   private void showOpenTickets(){
       int selection;
       do
       {
           final List<Ticket> myOpenTickets = new ArrayList<>();
           for (final Ticket ticket : tickets)
           {
               if (ticket.isOpen() && (
                       currentUser.getUsername().equals(ticket.getSubmitterUserName()) ||
                       currentUser.getUsername().equals(ticket.getAssigneeUserName())
                   ))
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
   
   /**
    * Handle the input parameters of the ticket report.
    */
   private void handleTicketReport()
   {
       do 
       {
           System.out.println(buildDashes(interfaceDash, interfaceWidth));
           System.out.print(buildMenu(interfaceDash, interfaceWidth, "Ticket Report"));
           System.out.println(buildDashes(interfaceDash, interfaceWidth));
           final Date reportStart = getDateInput("Report start period (DD/MM/YYYY): ");
           final Date reportEnd = getDateInput("Report end period (DD/MM/YYYY): ");
           if (reportStart.after(reportEnd))
           {
               printErr("Report start date " + formatDate(reportStart) + 
                        " must be before report end date " + formatDate(reportEnd));
           }
           else
           {
               showTicketReport(reportStart, reportEnd);
               return;
           }
       }
       while (true);
   }
   
   /**
    * Display the details of the ticket report between the two specified dates.
    * @param reportStart
    * @param reportEnd
    */
   private void showTicketReport(final Date start, final Date end)
   {
       final String range = formatDate(start) + " - " + formatDate(end);
       final List<Ticket> allTickets =  getTicketsSubmittedInPeriod(start, end);
       final Map<Boolean,List<Ticket>> openClosedMap = (
            allTickets.stream().collect(Collectors.partitioningBy(t -> t.isOpen()))
       );
       final List<Ticket> openTickets = openClosedMap.get(true);
       final List<Ticket> closedTickets = openClosedMap.get(false);
       
       System.out.println(buildDashes(interfaceDash, interfaceWidth));
       System.out.print(buildMenu(interfaceDash, interfaceWidth, "Ticket Report " + range));
       System.out.println(buildDashes(interfaceDash, interfaceWidth));
       System.out.println(
           "Total tickets: " + allTickets.size() + "           " +
           "Open tickets: " + openTickets.size() + "           " +
           "Closed tickets: " + closedTickets.size()
       );
       System.out.println(buildDashes(interfaceDash, interfaceWidth));
       System.out.print(buildMenu(interfaceDash, interfaceWidth, "Closed Tickets " + range));
       System.out.println(buildClosedTicketReportTable(closedTickets));
       System.out.println(buildDashes(interfaceDash, interfaceWidth));
       System.out.print(buildMenu(interfaceDash, interfaceWidth, "Open Tickets " + range));
       System.out.println(buildOpenTicketReportTable(openTickets));
       System.out.println(buildDashes(interfaceDash, interfaceWidth));
       
       int selection;
       do
       {
         selection = getIntInput("Type \"0\" to return to admin: ", 0);
         System.out.println();
       } 
       while (selection != 0);
   }

   /**
    * Return the list of tickets submitted within the specified range.
    */
   private List<Ticket> getTicketsSubmittedInPeriod(final Date start, final Date end)
   {
       return (
            tickets
            .stream()
            .filter(t -> isDateInRange(t.getSubmissionDate(), start, end))
            .collect(Collectors.toList())
       );
   }
   
   /**
    * Determines whether the specified date is within the specified start/end date range inclusive.
    * @param date Date to test
    * @param start Start date of the range
    * @param end End date of the range
    * @return true if date is within (inclusive) the start and end date range.
    */
   private boolean isDateInRange(final Date date, final Date start, final Date end)
   {
       return (
            date.equals(start) || date.equals(end) || (date.after(start) && date.before(end))
       );
   }
   
   /**
    * Using the specified severity, finds the appropriate assignee username.
    * @param severity The severity of the ticket.
    * @return the username of the assignee this ticket should be assigned to. 
    */
   private String findAssigneeUserName(final Severity severity) 
   {
       debugLog("Finding username for severity " + severity);
       final Role targetRole = (
            severity.equals(Severity.HIGH) ? Role.TECHNICIAN_LEVEL2 : Role.TECHNICIAN_LEVEL1
       );

       debugLog("Target role is " + targetRole);
       final Map<User,Long> candidates = countOpenTicketsInRole(targetRole);
       
       /*
        * If all counts of the candidate users are zero, we do a random allocation.
        */
       final User assignee;
       final Long firstCount = candidates.values().stream().findAny().get();
       if (candidates.values().stream().allMatch(count -> count == firstCount))
       {
           debugLog("All users have " + firstCount + " open tickets, randomising");
           assignee = getRandomUser(new ArrayList<>(candidates.keySet()));
       }
       else 
       {
           debugLog("Finding user with least open tickets");
           assignee = getUserWithLeastTickets(candidates);
       }
   
       debugLog("Assigned user " + assignee.getUsername());
       return assignee.getUsername();
   }
   
   /**
    * Returns a user at a random index from the given list of users.
    * @param values
    * @return
    */
   private User getRandomUser(final List<User> values)
   {
       return values.get(random.nextInt(values.size()));
   }
   
   /**
    * Returns the user with the least number of tickets.
    * @param userTicketMap
    * @return
    */
   private User getUserWithLeastTickets(final Map<User,Long> userTicketMap)
   {
       return (
            userTicketMap
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Entry::getValue))
                .findFirst()
                .get()
                .getKey()
       );
   }

   /**
    * Returns the set of users who are in a particular role.
    * @param role
    * @return
    */
   private Set<User> getUsersInRole(final Role role)
   {
       return users.stream().filter(u -> role.equals(u.getRole())).collect(Collectors.toSet());
   }

   /**
    * Counts the number of open tickets for each user in a given role.
    * @param role
    * @return
    */
   private Map<User,Long> countOpenTicketsInRole(final Role role) 
   {
       final Map<User,Long> result = (
            getUsersInRole(role).stream().collect(
                Collectors.toMap(u -> u, this::countOpenTicketsByAssigneeUser)
            )
       );
       result.forEach((u, c) -> {
           debugLog("User with username " + u.getUsername() + " has " + c + " open tickets.");
       });
       return result;
   }

   /**
    * Counts the number of open tickets that are assigned to the specified username.
    * @param assigneeUserName
    * @return
    */
   private long countOpenTicketsByAssigneeUser(final User assignee)
   {
       return (
            tickets
            .stream()
            .filter(t -> assignee.getUsername().equalsIgnoreCase(t.getAssigneeUserName()))
            .count()
       );
   }

   private void changeSeverity(final Ticket t)
   {

      System.out.println("Severity is Currently: " + t.getSeverity());
      System.out.println("What would you like to change the severity to?");
      System.out.println("LOW");
      System.out.println("MED");
      System.out.println("HIGH");
      
      final Severity original = t.getSeverity();
      final String selection  = getStringInput("Enter Selection: ");
  
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
      
      /*
       * Reassign ticket if the severity has changed.
       */
      if (!t.getSeverity().equals(original))
      {
          t.setAssigneeUserName(findAssigneeUserName(t.getSeverity()));
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
         id = getStringInput("Username: ");
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
       final List<Integer> allowed = Arrays.asList(allowedIntegers);
       do
       {
           final Optional<Integer> parseAttempt = tryParse(getStringInput(label));
           if (parseAttempt.isPresent())
           {
               final Integer selection = parseAttempt.get();
               if (allowed.isEmpty() || allowed.contains(selection))
               {
                   return selection;
               }
               else
               {
                   printErr("Please enter a valid selection.");
               }
           }
           else
           {
               printErr("Please enter a valid number.");
           }
        }
        while (true);
    }
   
   private Optional<Integer> tryParse(final String input) 
   {
       try 
       {
           return Optional.of(Integer.parseInt(input));
       }
       catch (final NumberFormatException e)
       {
           return Optional.empty();
       }
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
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "hstyles", "0496323145", parseDate("07/07/2020"),
                                 "This is just some test data 0.", Severity.LOW, false, parseDate("07/08/2020")));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "nhoran", "0496323145", parseDate("07/10/2020"),
                                 "This is just some test data 1.", Severity.LOW, false, parseDate("08/10/2020")));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "lpayne", "0496323145", parseDate("04/10/2020"),
                                 "This is just some test data 2.", Severity.MED, false, parseDate("11/10/2020")));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "lpayne", "0496323145", parseDate("05/10/2020"),
                                 "This is just some test data 3.", Severity.MED, false, parseDate("10/10/2020")));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "ltomlinson", "0496323145", parseDate("06/10/2020"),
                                 "This is just some test data 4.", Severity.HIGH, false, parseDate("07/10/2020")));
          tickets.add(new Ticket("Bobby", "Bob-Bob", "bob", "zmalik", "0496323145", parseDate("08/10/2020"),
                                 "This is just some test data 5.", Severity.HIGH, false, parseDate("09/10/2020")));
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
       final Map<String,Integer> headers = new LinkedHashMap<>();
       headers.put("ID", 5);
       headers.put("Submit.", 8);
       headers.put("Assignee", 8);
       headers.put("Name", 8);
       headers.put("Status", 6);
       headers.put("Sev.", 4);
       headers.put("Sub. Date", 10);
       headers.put("Desc.", 8);
       return buildTicketTable(ticketsToDisplay, headers, (t, i) -> {
           final List<String> cols = new ArrayList<>();
           cols.add(padColumn("-- " + String.valueOf(i + 1) + ".", headers.get("ID")));
           cols.add(padColumn(t.getSubmitterUserName(), headers.get("Submit.")));
           cols.add(padColumn(t.getAssigneeUserName(), headers.get("Assignee")));
           cols.add(padColumn(t.getFullName(), headers.get("Name")));
           cols.add(padColumn(t.isOpen() ? "Open" : "Closed", headers.get("Status")));
           cols.add(padColumn(t.getSeverity().toString(), headers.get("Sev.")));
           cols.add(padColumn(formatDate(t.getSubmissionDate()), headers.get("Sub. Date")));
           cols.add(padColumn(t.getDescription(), headers.get("Desc.")));
           return cols;
       });
   }
   
   private String buildTicketTable(final List<Ticket> ticketsToDisplay,
                                   final Map<String,Integer> headers,
                                   final BiFunction<Ticket,Integer,List<String>> columnMapper)
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
       final String header = String.join(" | ", 
           headers.entrySet().stream().map(e -> padColumn(e.getKey(), e.getValue())).collect(Collectors.toList())
       ) + " |";
       lines.add(buildDashes(interfaceDash, header.length()));
       lines.add(header);
       lines.add(buildDashes(interfaceDash, header.length()));
       final int numTickets = ticketsToDisplay.size();
       for (int i = 0; i < numTickets; i++) {
           final Ticket t = ticketsToDisplay.get(i);
           final List<String> cols = columnMapper.apply(t, i);
           lines.add(String.join(" | ", cols) + " |");
       }
       lines.add(buildDashes(interfaceDash, header.length()));
       return String.join(System.lineSeparator(), lines);
   }
   
   private String buildOpenTicketReportTable(final List<Ticket> openTickets)
   {
       final Map<String,Integer> headers = new LinkedHashMap<>();
       headers.put("Submitter", 14);
       headers.put("Submission Date", 16);
       headers.put("Severity", 42);
       return buildTicketTable(openTickets, headers, (t, i) -> {
           final List<String> cols = new ArrayList<>();
           cols.add(padColumn(t.getSubmitterUserName(), headers.get("Submitter")));
           cols.add(padColumn(formatDate(t.getSubmissionDate()), headers.get("Submission Date")));
           cols.add(padColumn(t.getSeverity().toString(), headers.get("Severity")));
           return cols;
       });
   }
   
   private String buildClosedTicketReportTable(final List<Ticket> closedTickets)
   {
       final Map<String,Integer> headers = new LinkedHashMap<>();
       headers.put("Submitter", 14);
       headers.put("Submission Date", 16);
       headers.put("Assignee", 14);
       headers.put("Time To Resolution (Days)", 25);
       return buildTicketTable(closedTickets, headers, (t, i) -> {
           final List<String> cols = new ArrayList<>();
           cols.add(padColumn(t.getSubmitterUserName(), headers.get("Submitter")));
           cols.add(padColumn(formatDate(t.getSubmissionDate()), headers.get("Submission Date")));
           cols.add(padColumn(t.getAssigneeUserName(), headers.get("Assignee")));
           cols.add(padColumn(
               String.valueOf(t.getTimeToResolutionInDays()), 
               headers.get("Time To Resolution (Days)"))
           );
           return cols;
       });
   }
   
   private String padColumn(final String string, final int width) {
       final String s = (string.length() > width) ? string.substring(0, width) : string;
       return String.format("%-" + width + "s", s);
   }
   
   private static void debugLog(final String message) 
   {
       if (DEBUG_OUTPUT) 
       {
           System.out.println("[DEBUG] " + message);
       }
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
   private String assigneeUserName;
   private final String contactNumber;
   private final Date submissionDate;
   private final String description;
   private boolean open;
   private Severity severity;
   private Date resolutionDate;

   public Ticket(final String firstName, 
                 final String lastName, 
                 final String submitterUserName,
                 final String assigneeUserName,
                 final String contactNumber, 
                 final Date submissionDate, 
                 final String description,
                 final Severity severity)
   {
      this.firstName = firstName;
      this.lastName = lastName;
      this.submitterUserName = submitterUserName;
      this.assigneeUserName = assigneeUserName;
      this.contactNumber = contactNumber;
      this.submissionDate = submissionDate;
      this.description = description;
      this.severity = severity;
   }
   
   public Ticket(final String firstName, 
           final String lastName, 
           final String submitterUserName,
           final String assigneeUserName,
           final String contactNumber, 
           final Date submissionDate, 
           final String description,
           final Severity severity,
           final boolean open,
           final Date resolutionDate)
    {
        this(
            firstName,
            lastName,
            submitterUserName,
            assigneeUserName,
            contactNumber,
            submissionDate,
            description,
            severity
        );
        this.open = open;
        this.resolutionDate = resolutionDate;
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
   
   public Date getResolutionDate()
   {
       return resolutionDate;
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
   
   public String getFullName() 
   {
       return firstName + " " + lastName;
   }

   public String getSubmitterUserName()
   {
      return submitterUserName;
   }
   
   public String getAssigneeUserName() 
   {
       return assigneeUserName;
   }
   
   public void setAssigneeUserName(final String assigneeUserName)
   {
       this.assigneeUserName = assigneeUserName;
   }

   public String getContactNumber()
   {
      return contactNumber;
   }

   public Date getSubmissionDate()
   {
      return submissionDate;
   }
   
   public Long getTimeToResolutionInDays()
   {    
       if (resolutionDate != null && submissionDate != null) 
       {
           return ChronoUnit.DAYS.between(submissionDate.toInstant(), resolutionDate.toInstant());    
       }
       return 0L;
   }

   public String getDescription()
   {
      return description;
   }

}