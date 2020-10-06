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
 * @author Rob Beardow      s3641721@student.rmit.edu.au
 * @author Tyson Horsewell  s3799530@student.rmit.edu.au
 * @author Jordon Edmondson s3779499@student.rmit.edu.au
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

      final Application2 app = new Application();
      app.handleMainMenu();

   }

   protected Application()
   {
      initDefaultUserDatabase();
   }

   private void handleMainMenu()
   {
      int selection;
      int[] allowedMenuItems = { 1, 2, 3, 4 };
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
         selection = getIntInput("Your Selection: ", allowedMenuItems);
         System.out.println();
         switch (selection)
         {
            case 1:
               userLogin();
               currentUser = null;
               break;
            case 2:
               System.out.println("You selected create account");
               break;
            case 3:
               System.out.println("You selected reset password");
               break;
            case 4:
               System.out.println("Exiting...");
               break;
         }
      } while (selection != 4);
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


   /**
    *  Form to create a user
    */
   protected void createUser()
   {
      System.out.println("-------------------------------");
      System.out.println("----- IT Ticketing System -----");
      System.out.println("---------- New User -----------");
      System.out.println("-------------------------------");

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
            System.out.println("Error: that username is taken try another.");
         }
      } while (exists);

      String p = getStringInput("Password: ");
      Role r = getRoleInput("Role (1 = Staff, 2 = Tech level 1, 3 = Tech level 2): ");

      if ( users.add(new User(id, p, fName, lName, r)) )
      {
         System.out.println("New User has been added to the system.");
         System.out.println();
      }

   }

   /**
    *  Form to reset the password
    */
   protected void resetPassword()
   {
      System.out.println("-------------------------------");
      System.out.println("----- IT Ticketing System -----");
      System.out.println("------- Reset Password --------");
      System.out.println("-------------------------------");

      String id = getStringInput("ID: ");
      User user = findUserByID(id);

      // if the username exists then allow for the password to be changed
      if (id.equalsIgnoreCase(Objects.isNull(user) ? "" : user.getUsername()))
      {
         user.setPassword(getStringInput("Password: "));
         System.out.println("The password has been reset.");
         System.out.println();
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

   protected Role getRoleInput(String label)
   {
      int s;
      int[] allowedValues = { 1, 2, 3 };
      Role r = null;
      do
      {
         s = getIntInput(label, allowedValues);
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

   protected int getIntInput(String label, int[] allowedIntegers)
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
               System.out
                        .println("Error: That is an invalid selection.\nPlease enter: " +
                                 Arrays.toString(allowedIntegers));
            }
         } while (!inArray);
      }
      return val;
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

      public void setPassword(String password)
      {
         this.password = password;
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
