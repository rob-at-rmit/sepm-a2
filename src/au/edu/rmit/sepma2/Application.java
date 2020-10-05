package au.edu.rmit.sepma2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

/**
 * Main application class for Software Engineering Project Management Study Period 3
 * 2020, Group 12 Assignment 2
 * 
 * @author Rob Beardow s3641721@student.rmit.edu.au
 * @author Tyson Horsewell s3799530@student.rmit.edu.au
 * @author Jordon Edmondson ...
 *
 */
public class Application
{

   private static final Scanner scanner = new Scanner(System.in);

   private final Set<User> users = new HashSet<>();
   private User currentUser = null;

   /**
    * Main entry point for the IT ticketing system.
    */
   public static void main(final String[] args)
   {

      final Application app = new Application();
      app.handleMainMenu();

   }

   protected Application() {
        initDefaultUserDatabase();
    }

   private void handleMainMenu()
   {
      String selection;
      do
      {
         System.out.println("-------------------------------");
         System.out.println("----- IT Ticketing System -----");
         System.out.println("-------------------------------");
         System.out.println("-- 1. Login");
         System.out.println("-- 2. Create Account");
         System.out.println("-- 3. Reset Password");
         System.out.println("-- 4. Exit");
         System.out.println("-------------------------------");
         selection = scanner.nextLine();
         System.out.println();
         if (selection.length() != 1)
         {
            System.err.println("Error - invalid selection");
         }
         else
         {
            switch (selection)
            {
               case "1":
                  userLogin();
                  currentUser = null;
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
      } while (!selection.contentEquals("4"));
   }

   protected void userLogin()
   {
      System.out.println("-------------------------------");
      System.out.println("----- IT Ticketing System -----");
      System.out.println("--------- User Login ----------");
      System.out.println("-------------------------------");

      // ask for ID
      String id = getStringInput("ID: ");
      User user = findUserByID(id);

      if (id.equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername()))
      {
         if (user.getPassword().equals(getStringInput("Password: ")))
         {
            currentUser = user;
         }
         else
         {
            System.out.println("Error: The Password is incorrect.");
         }
      }
      else
      {
         System.out
                  .println("Error: There is no user by that username in the system.");
      }
   }

   protected String getStringInput(String label)
   {
      String s;
      do
      {
         System.out.print(label);
         s = scanner.nextLine();
         System.out.println();
      } while (Objects.isNull(s) || s.isEmpty());
      return s;
   }

   protected User findUserByID(String id)
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
   }

   private enum Role
   {
      STAFF, TECHNICIAN_LEVEL1, TECHNICIAN_LEVEL2
   }

   protected static class User
   {

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
                     final Role role)
      {
         super();
         this.username = username;
         this.password = password;
         this.firstName = firstName;
         this.lastName = lastName;
         this.role = role;
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

}
