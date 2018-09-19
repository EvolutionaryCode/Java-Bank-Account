//Java Package Name
package org.aagrandpre.bank;
//Import Java Tools
import java.util.Scanner;
import java.util.List;
//Import RethinkDB Tools
import static org.aagrandpre.bank.Database.r;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.gen.exc.ReqlError;
import com.rethinkdb.gen.exc.ReqlQueryLogicError;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

//Timestamp Imports
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.sql.Time;

//Auth
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;


/**
 * Student Number - 1-7
 * @author Austin Andrew Grandpre
 * Start Date - August 29th, 2018
 * End Date - TBD
 * Period - 1
 * Project Name - Bank Project
 * Class Larson - AP Computer Science
 * 
 *Github - https://github.com/EvolutionaryCode/Java-Bank-Account
 */



public class UserInput {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private static final Connection conn = r.connection().hostname("putyouriphere").port(28015).connect();
    
    public static void main(String[] args)
        {         
            //Setup Scanner
                Scanner scan = new Scanner(System.in);
                //Setup Timestamp
                OffsetDateTime timestamp = OffsetDateTime.now();
                //Setup Variables
                 String version = "1.2";
                 String builder = "Java Console";
                //Shows Current Version
                System.out.println(version);
                
                //Decide what the scanner will be looking for
                System.out.println("What action will you be completing?: \n1 - Setup Google Authenticator\n2 - Login To The Bank\n3 - Register A New Account");
                String action = scan.nextLine();
        
                //Client For Auth
                if (action.equals("1")){
                         System.out.println("Username: ");
                         String username = scan.nextLine();
                         System.out.println("Password: ");
                         String password = scan.nextLine();
                          if(checkLogin(username, password)){
                          //What to do if they have a valid username & password
                          if(checkGkeySetup(username)){
                              //What to do if the user has no Gkey & Google Authenicator Isn't Setup
                           GoogleAuthenticator gAuth = new GoogleAuthenticator();
                           GoogleAuthenticatorKey key = gAuth.createCredentials();
                           String gkey = key.getKey();
                           System.out.println("You're Google Authentication Code Is:" + (gkey));
                           //Ask them to enter in the code and then check it
                           System.out.println("Google Authentication Setup\nIn order to verify you have set it up correctly\nEnter the Six Diget code");
                           int code = Integer.parseInt(scan.nextLine());
                            if(checkGkey(gkey, code)){
                              //What to do if the Gkey is properly setup
                              System.out.println("Google Authentication Setup Complete\nGoogle Authenticator (2fa) has been setup, you understand that by enabling this:\nYou realize that it is your job to rember your 2FA setup\nAlong with we provide no support for lost Auth Keys");
                              //Store Gkey under username in RethinkDB
                              storeGkey(username, gkey);
                              //Log the Gkey under logs for that username
                              logGkeySetup(username, gkey, version, timestamp);
                          } else {
                             //What to do if the Gkey isn't properly setup
                             System.out.println("Google Authentication Setup Failed\nGoogle Authenticator failed given you didn't have the secret key setup properly");
                          }
                           
                          }else {
                              //What to do if the user already has a Gkey & Google Authenticator Setup
                              System.out.println("Authentication Setup Failed!\nYour username already has a valid GoogleKey our records show!");
                                  }
                          }else {
                              //What to do if their username & password isn't valid
                              System.out.println("Login Failed!\nYour username or password did not match our records");
                          }
                          //End of the Auth Section
                } else if (action.equals("2")){
                    //What to do if the user picks to Login into the bank!
                     System.out.println("Username: ");
                         String username = scan.nextLine();
                         System.out.println("Password: ");
                         String password = scan.nextLine();
                          if(checkLogin(username, password)){
                          //What to do if they have a valid username & password
                           if(checkGkeySetup(username)){
                               //What to do if they don't have 2FA Setup/Enabled!
                               System.out.println("One Second, Given We Are Finding You're Information Wait A Momment");
                               //Grabing User Information
                               String name = (grabName(username));
                               double checkingbal = (grabCheckingbal(username));
                               double savingsbal = (grabSavingsbal(username));
                               
                               System.out.println("What action will you be completing?\n1 - Withdraw Funds\n2 - Deposit Funds\n3 - Transfer Funds(Comming Soon!)\n4 - Settings(Comming Soon!)\n5 - Admin Portal(Comming Soon!)");
                                String bankaction = scan.nextLine();
                                
                                if (bankaction.equals("1")){
                                    //What to do if they want to withdraw funds
                                     System.out.println("You're Checkings Account - balance is: $" + (checkingbal));
                                     System.out.println("You're Savings Account - balance is: $" + (savingsbal));
                                     //Setup What account they want to pick
                                     System.out.println("What account would you like to withdraw funds from?\n1 - Checking Account\n2 - Savings Account");
                                     //Create What Account Scanner
                                     String withdrawaction = scan.nextLine();
                                     
                                } //End of Withdraw Funds Action
                                else if (bankaction.equals("2")) {
                                    //What to do if they want to deposit funds
                                }//End of Deposit Funds Action 
                                else if (bankaction.equals("3")) {
                                    //What to do if they want to transfer funds
                                }
                                else if (bankaction.equals("4")) {
                                    //What to do if they want to change settings
                                }
                                else if (bankaction.equals("5")) {
                                    //What to do if they want to access the admin panel side
                                }
                                else{
                                    //What to do if the user picks anything other then the options we have given them
                                }
                           
                               
                           }else {
                               //What to do if the user has 2FA Enabled
                               System.out.println("One Second, Given We Are Finding You're Gkey Wait A Momment");
                               //Grabs Gkey From RethinkDB
                               String gkey = (grabGkey(username));
                               //Request 2FA Code/Store It
                               System.out.println("Given That This Account Has 2FA Enabled\n Please enter in the 2FA Code!");
                               int code = Integer.parseInt(scan.nextLine()); 
                               //Send To checkGkey
                               if(checkGkey(gkey, code)){
                              //What to do if the Gkey and the code match
                              System.out.println("Java Bank Welcomes You!");
                              
                          }else {
                             //What to do if the Gkey doesn't match the code!
                             System.out.println("Error 403\nEror - Not Correct Code To Go With Google Key!");
                          }
                               
                           }
                           }else {
                              //What to do if they don't have a valid username & password
                          }
                } //End of Login Section
                else if (action.equals("3")){
                    //What to do if the user wants to Register for a New account
                    System.out.println("Welcome To Java Bank Registration!\nWe need to first make sure another account doesn't exist with the username you would like!\nSo, enter in you're requested username...");
                    System.out.println("Username: ");
                         String username = scan.nextLine();
                         if(checkUsername(username)){
                             //What to do if there is no user account with that name
                             
                         }else {
                             //What to do if there is already a user account with that name
                             
                             System.out.println("Java Bank Registration - Error 403\nWe have detcted that you have tried to create an account with the same username as someone else!\nWe have logged this attempt and information and have not created you an account!");
                         }
                }
                //End of Register Section
        } //end action method/main method
    
    //Bank Account Actions
    
    //Checking Stored Information - Boolean
    //Checking User Entered Cradenciaals Vs Database
     private static boolean checkLogin(String username, String password) {
        //What to do if we need to check their login
        r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                             .and(row.g("password").eq(password)))
                             .isEmpty().not()
                             .run(conn);
                return true;
                }
     //Checking For Requested Username Vs Database
     private static boolean checkUsername(String username) {
        //What to do if the user wants to register a new account
        
                return true;
                }
     //Checking if the User has Gkey Setup
     private static boolean checkGkeySetup(String username) {
        //What to do if we need to check their login
        r.db("APSCI").table("BankAccounts").filter(row ->
                row.g("username").eq(username)
                                .and(row.g("gkey").eq("false")
                                )).isEmpty().not().run(conn);
        return true;
     }
     //Checking the users G-Code Against The Gkey
     private static boolean checkGkey(String gkey, int code) {
        //G-Code Checker Vs. G-Key
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
              if (gAuth.authorize(gkey, code));
              //What to do if the gkey = code
              return true;
     }
     //Checking if the user wants their bal to be shown when on the home screen
     private static boolean checkShowbal(String username) {
        //Checking if thet want thier bal to be shown
        r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                             .and(row.g("balshow").eq(true)))
                             .isEmpty().not()
                             .run(conn);
                return true;
                }
     
     //Grabing User Data/Key
     //Grabing the Users Gkey
     private static String grabGkey(String username) {
        //What to do if we need to get the users Gkey
        String gkey = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                             .g("gkey").nth(0)
                             .run(conn);
        //Returns The Gkey From The Database as a String
        return gkey; 
     }
     //Grabing the Users Name
     private static String grabName(String username) {
        //What to do if we need to grab their name from username
        String accname = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                             .g("name").nth(0)
                             .run(conn);
        //Returns The Gkey From The Database as a String
        return accname; 
     }
     //Grabing the Checking Bal
     private static double grabCheckingbal(String username) {
        //What to do if we need to grab their checking balance from username
        double checkingbal = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                             .g("checkingbal").nth(0)
                             .run(conn);
        //Returns The Checking Balance From The Database as a double
        return checkingbal;
     }
     //Grabing the Savings Bal
     private static double grabSavingsbal(String username) {
        //What to do if we need to grab their savings balance from username
        double savingsbal = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                             .g("savingsbal").nth(0)
                             .run(conn);
        //Returns The Savings Balance From The Database as a double
        return savingsbal;
     }
     
     
     //Storing Data
     //Storing the Users GKey into RethinkDB
     private static boolean storeGkey(String username, String gkey) {
        //What to do if we need to store a Gkey
         r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                         .update(
                            r.hashMap("username", (username))
                                .with("gkey",(gkey)
                                )).run(conn);
        return true;
     }
     //Storing a new user into RethinkDB
     private static boolean newLogin(String username, String language, String password, String showbal) {
        //What to do if we need to store a new account
       
        return true;
     }
     
     //Logging Data
     //Google Authentication Setup Log
     private static boolean logGkeySetup(String username, String gkey, String version, sdf timestamp) {
        //What to do if we need to store a Google Auth Setup Log
           r.db("APSCI").table("BankAccountLogs").insert(
                           (r.array(
                            r.hashMap("username", (username))
                                .with("action", "2FA-Enabled")
                                .with("Version",(version))
                                .with("Timestamp", (timestamp))
                                .with("GKey", (gkey))
                                ))).run(conn);
        return true;
     }
     
     
     
        
         
        
         

                
                          
                    
                     
                     
                      
                      
                      
                    
                            
                          
                         
                                    
                   System.out.println("What account would you like to withdraw funds from?");
                   System.out.println("1 - Checking Account");
                   System.out.println("2 - Savings Account");
                   String withdrawaction = scan.nextLine();
                   //What to do if the user picks withdraw funds
                   if (withdrawaction.equals("1")){
                       System.out.println("You're checking account ballance is: $" + (getchecking));
                       System.out.println("How much money would you like to withdraw?");    
                        int amount = Integer.parseInt(scan.nextLine());
                       System.out.println("Your withdrawal amount: $" + amount);
                        if((getchecking) >= (amount)){
                            if((amount) >= 3000){ 
                                     if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                                 .and(row.g("gkey").eq("false")))
                                  .isEmpty()
                                    .run(conn)){
                            System.out.println("GoogleAuthCode: ");
                            int code1 = Integer.parseInt(scan.nextLine());
                            if(gAuth.authorize((gkey), (code1))){
                                //What to do if they have the correct key and withdraw more then 3000
                                r.db("APSCI").table("BankAccounts").update(
                                        r.hashMap("username", (username))
                                            .with("checkingbal",(getchecking)-(amount))
                                            ).run(conn);
                                r.db("APSCI").table("BankAccountLogs").insert(
                                        (r.array(
                                            r.hashMap("username", (username))
                                                .with("account", "checking")
                                                .with("action", "withdraw")
                                                .with("withdraw", (amount))
                                                .with("leftammount", (getchecking)-(amount))
                                                .with("Version",(version))
                                                .with("Timestamp", (timestamp))
                                                ))).run(conn);
                                double getchecking1 = r.db("APSCI").table("BankAccounts").filter(row ->
                                row.g("username").eq(username))
                                    .g("checkingbal").nth(0)
                                    .run(conn);
                            System.out.println("Your new balance is: $"+ getchecking1);
                            }
                                     }else{
                                        System.out.println("Urgent Notice!\nYoure Withdraw Couldn't Be Comepleted Due To:\nYou tried to withdraw more then 3000$ without 2FA\nIf you would like to setup 2FA type Auth back on the main screen!");
                                     }
                            }else {
                                         //What to do if it's under 3,000
                                          r.db("APSCI").table("BankAccounts").update(
                                        r.hashMap("username", (username))
                                            .with("checkingbal",(getchecking)-(amount))
                                            ).run(conn);
                                r.db("APSCI").table("BankAccountLogs").insert(
                                        (r.array(
                                            r.hashMap("username", (username))
                                                .with("account", "checking")
                                                .with("action", "withdraw")
                                                .with("withdraw", (amount))
                                                .with("leftammount", (getchecking)-(amount))
                                                .with("Version",(version))
                                                .with("Timestamp", (timestamp))
                                                ))).run(conn);
                                double getchecking1 = r.db("APSCI").table("BankAccounts").filter(row ->
                                row.g("username").eq(username))
                                    .g("checkingbal").nth(0)
                                    .run(conn);
                            System.out.println("Your new balance is: $"+ getchecking1);
                            }
                                     }else{
                           System.out.println("What do you think this is?\n a infiti bank?");
                   }
                   }
                         if (withdrawaction.equals("2")){
                            System.out.println("You're savings account ballance is: $" + (getsavings));
                       System.out.println("How much money would you like to withdraw?");    
                         int amount = Integer.parseInt(scan.nextLine());
                       System.out.println("Your withdrawal amount: $" + amount);
                        if((getsavings) >= (amount)){
                            if((amount) >= 3000){ 
                                     if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                                 .and(row.g("gkey").eq("false")))
                                  .isEmpty()
                                    .run(conn)){
                            System.out.println("GoogleAuthCode: ");
                            int code1 = Integer.parseInt(scan.nextLine());
                            
                            GoogleAuthenticator gAuth1 = new GoogleAuthenticator();
                            if(gAuth1.authorize((gkey), (code1))){
                                //What to do if they have the correct key and withdraw more then 3000
                                r.db("APSCI").table("BankAccounts").update(
                                        r.hashMap("username", (username))
                                            .with("savingsbal",(getsavings)-(amount))
                                            ).run(conn);
                                r.db("APSCI").table("BankAccountLogs").insert(
                                        (r.array(
                                            r.hashMap("username", (username))
                                                .with("account", "savings")
                                                .with("action", "withdraw")
                                                .with("withdraw", (amount))
                                                .with("leftammount", (getsavings)-(amount))
                                                .with("Version",(version))
                                                .with("Timestamp", (timestamp))
                                                ))).run(conn);
                                double getsavings1 = r.db("APSCI").table("BankAccounts").filter(row ->
                                row.g("username").eq(username))
                                    .g("savingsbal").nth(0)
                                    .run(conn);
                            System.out.println("Your new balance is: $"+ getsavings1);
                            }
                                     }else{
                                         //What to do if no 2FA and Want to withdraw more then 3,000$
                                         System.out.println("Urgent Notice!\nYoure Withdraw Couldn't Be Comepleted Due To:\nYou tried to withdraw more then 3000$ without 2FA\nIf you would like to setup 2FA type Auth back on the main screen!");
                                     }
                            }else {
                                         //What to do if it's under 3,000
                                          r.db("APSCI").table("BankAccounts").update(
                                        r.hashMap("username", (username))
                                            .with("checkingbal",(getsavings)-(amount))
                                            ).run(conn);
                                r.db("APSCI").table("BankAccountLogs").insert(
                                        (r.array(
                                            r.hashMap("username", (username))
                                                .with("account", "checking")
                                                .with("action", "withdraw")
                                                .with("withdraw", (amount))
                                                .with("leftammount", (getsavings)-(amount))
                                                .with("Version",(version))
                                                .with("Timestamp", (timestamp))
                                                ))).run(conn);
                                double getsavings1 = r.db("APSCI").table("BankAccounts").filter(row ->
                                row.g("username").eq(username))
                                    .g("savingsbal").nth(0)
                                    .run(conn);
                            System.out.println("Your new balance is: $"+ getsavings1);
                            }
                                     }else{
                            //What to do if the user wants more then what they have in their account!
                           System.out.println("Notice, You're Withdraw Request Failed!\nLikely due to you're request was more then your balance!");
                   }
                         }
                   }
                if (bankaction1.equals("2")){
                       System.out.println("You're Checkings Account - balance is:" + (getchecking));
                   System.out.println("You're Savings Account - balance is:" + (getsavings));
                   System.out.println("What account would you like to deposit funds from?");
                   System.out.println("1 - Checking Account");
                   System.out.println("2 - Savings Account");
                   String depositaction = scan.nextLine();
                   //What to do if the user picks withdraw funds
                   if (depositaction.equals("1")){
                       System.out.println("You're checking account ballance is: $" + (getchecking));
                       System.out.println("How much money would you like to deposit?");    
                        int amount = Integer.parseInt(scan.nextLine());
                       System.out.println("Your deposit amount: $" + amount);
                        if((currenfunds) >= (amount)){
                            r.db("APSCI").table("BankAccountLogs").insert(
                                        (r.array(
                                            r.hashMap("username", (username))
                                                .with("account", "checking")
                                                .with("action", "deposit")
                                                .with("withdraw", (amount))
                                                .with("leftammount", (amount)+(getchecking))
                                                .with("Version",(version))
                                                .with("Timestamp", (timestamp))
                                                ))).run(conn);
                                r.db("APSCI").table("BankAccounts").update(
                                        r.hashMap("username", (username))
                                            .with("checkingbal",(amount)+(getchecking))
                                            ).run(conn);
                                 double getchecking2 = r.db("APSCI").table("BankAccounts").filter(row ->
                                row.g("username").eq(username))
                                    .g("checkingbal").nth(0)
                                    .run(conn);
                            System.out.println("Your new balance is: $"+ getchecking2);
                        } else {
                            //What to do if they don't have enough money in their currentbal
                           System.out.println("You don't have unlimited money");
                   }
                   }
                   
                          if (depositaction.equals("2")){
                             System.out.println("You're savings account ballance is: $" + (getsavings));
                       System.out.println("How much money would you like to deposit?");    
                        int amount1 = Integer.parseInt(scan.nextLine());
                       System.out.println("Your deposit amount: $" + amount1);
                        if((currenfunds) >= (amount1)){
                            r.db("APSCI").table("BankAccounts").update(
                                        r.hashMap("username", (username))
                                            .with("savingsbal",(amount1)+(getsavings))
                                            ).run(conn);
                            r.db("APSCI").table("BankAccountLogs").insert(
                                        (r.array(
                                            r.hashMap("username", (username))
                                                .with("account", "savings")
                                                .with("action", "deposit")
                                                .with("withdraw", (amount1))
                                                .with("leftammount", (amount1)+(getsavings))
                                                .with("Version",(version))
                                                .with("Timestamp", (timestamp))
                                                ))).run(conn);
                                 double getsavings2 = r.db("APSCI").table("BankAccounts").filter(row ->
                                row.g("username").eq(username))
                                    .g("savingsbal").nth(0)
                                    .run(conn);
                            System.out.println("Your new balance is: $"+ getsavings2);
                        } else {
                            //What to do if they don't have enough money in their currentbal
                           System.out.println("You don't have unlimited money");
                   }
                }
                }
                if (bankaction1.equals("3")){
                    //What to do if the user wants to Transfer Funds
                    System.out.println("We have not yet setup transfer funds");
                }
                if (bankaction1.equals("4")){
                    //What to do if the user wants to change their account settings
                    System.out.println("We have not yet setup settings");
                }
                if (bankaction1.equals("5")){
                    //What do if the user wants to access the Admin Pannel
                    System.out.println("We have not yet setup the admin pannel");
                }
                   
                            
                            } //End Of User Input!
                           
               }else {
                    //What to do if they don't pick a valid Gkey
                    System.out.println("Invalid Google Code!");
                }
                          }
                    }else {
                         //What to do if username doesn't equal or password to DB
                         r.db("APSCI").table("BankAccountLogs").insert(
                           (r.array(
                            r.hashMap("attemptedusername", (username))
                                .with("action", "FailedLogin")
                                .with("Version",(version))
                                .with("Timestamp", (timestamp))
                                ))).run(conn);
                         System.out.println("No User Was Found! We have logged your information\n Or, you entered you're password inccorectly!");
                     }
                    }//End Of Login Section//End Of Login Section
                          
                   
                     
                      
                     
                    if (action.equals("Numbers")){
                        
                        System.out.println("Numbers is not yet setup!");
                
                    }
             
                    if (action.equals("Register")){
                         System.out.println("Username: ");
                            String username = scan.nextLine();
                            
                    
                       if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)).isEmpty().run(conn)){ 
                         System.out.println("Please Enter Your Name: ");
                         String name = scan.nextLine();
                         System.out.println("Please Enter Your Requested Password: ");
                         String password = scan.nextLine();
                         System.out.println("Enter Pin");
                         String pin = scan.nextLine();
                         System.out.println("Show Ballance");
                         String showbal = scan.nextLine();
                         
                        //User Data Insert Format For Register 
                    r.db("APSCI").table("BankAccounts").insert(r.array(
                            r.hashMap("name", (name))
                             .with("username", (username))
                             .with("password", (password))
                             .with("level", "user")
                             .with("created", (timestamp))     
                             .with("createdwith", (builder))
                             .with("savingsbal", 4000000.25)
                             .with("checkingbal", 100.50)
                             .with("language", "en")
                             .with("pin", (pin))
                             .with("balshow", (showbal))
                             .with("gkey","false")
                              /**
                               * Removed 13th Due to issues will be re-added soon
                             .with("accounts", r.array(
                                 r.hashMap("account", "savings")
                                  .with("bal", "400"),
                                 r.hashMap("account", "checking")
                                  .with("bal", "300")))
                                  
                             .with("settings", r.array(
                                 r.hashMap("lang", (language)),
                                 r.hashMap("pin", (pin)),
                                 r.hashMap("balshow", (showbal))
                                 */
                        )).run(conn);
                       }
                         
                    else {
                           r.db("APSCI").table("BankAccountLogs").insert(
                           (r.array(
                            r.hashMap("attemptedusername", (username))
                                .with("action", "CreateExistingAccount")
                                .with("Version",(version))
                                .with("Timestamp", (timestamp))
                                ))).run(conn);
                           System.out.println("There Is Already An Account with a usename of:" + (username) + "\nWe have logged you're attempt!");        
                    }  
                  
 
                    
                    conn.close();
                    }
        }
        
        //New Methods For Checking For Login & Such
        
         private static void checklogin() {
        //What to do if we need to check their login
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
         
        private static void grabgkey(int gkey, String username) {
        //What to do if we need to grab their Google Jet From RethinkDB 
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
         
        private static void checkauth(int gkey) {
        //What to do if we need to check their auth code 
      
    } 
        private static void setupauth(String password, String username) {
        //What to do if we need to check their auth code 
            
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
        System.out.println("Username: ");
                         String authuser = scan.nextLine();
                         System.out.println("Password: ");
                         String authpass = scan.nextLine();
    }

}
               



