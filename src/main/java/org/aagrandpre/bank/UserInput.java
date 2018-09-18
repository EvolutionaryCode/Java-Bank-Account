//Java Package Name
package org.aagrandpre.bank;
//Import Java Tools
import java.util.Scanner;
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

//Auth
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.util.List;

/**
 * Student Number - 1-7
 * @author Austin Andrew Grandpre
 * Start Date - August 29th, 2018
 * End Date - TBD
 * Period - 1
 * Project Name - Bank Project
 * Class Larson - AP Computer Science
 * 
 * Changelog - 
 * August 31st, 2018 - Added Multi-Scanner Functionality & Login System
 * September 5th 2018 - Added RethinkDB Login System Validation
 * September 6th 2018 - Added Success/Failed Login Logs To RethinkDB
 * September 7th - 9th 2018 - Setup Google Authenticator And Logged Key To RethinkDB
 * September 10th 2018 - Created Cash Suffixes
 * September 11th 2018 - Setup Register System with embeds
 * September 12th 2018 - Setup Individual Bank Accounts
 * September 12th 2018 - Rebuilt Login Around RethinkDB & Google Authenticator
 * September 13th 2018 - Built Withdraw With Google Authenticator for withdraws over 3,000
 * September 13th 2018 - Built deposit function along with logging for deposit and Withdraw
 * September 14th-Onward 2018 - All changes are now stored in GitHub Commits
 * 
 * To-Do List
 * Transfer Funds From One User To Another
 * Admin Panel Change Funds, Delete Users, Lock User Accounts
 * Automatic after 3 failed logins lockout the user for 3 minutes
 * Re-add JSON Embeds
 * Enable Multi Language Support (Already have code written to grab a RethinkDB file just need translations and switch prints)
 * Friendly user UI
 * Encrypt Passwords
 * Re-add Cash Suffixes (K, M, B, T)
 * Re-add numbers function
 * Spit across multiple classes in java
 * Re-add Message If User Doesn't Do Login, Numbers Or Auth
 * 
 * New User Guide:
 * When promoted type Register to create a new account then go through and complete the fields!
 * If you would like to enable 2FA I have used Google Authenticator - Restart the program and then type Auth and then enter the username and password
 * then, when it prompts you with the key store it in either the Google Authenticator App or Google Authenticator Chrome Plugin
 * To Access the bank restart the program and type login and then type username and password and then pick an option!
 * 
 * Project Builder Information
 * IDE - Netbeans V.8.2
 * Required Plugin's - Gradle Support
 * After installing Gradle support pick load project from zip open up the zipped file!
 * 
 * Credits -
 * Database - RethinkDB (Open Source NoSQL Database)
 * Auth - Google Authenticator (Created By Google)
 * Auth - Google Java Authenticator Connect (Created by Warrenstrange)
 * 
 * Contributors-
 * Stek - Formated Code On Stack Overflow!
 * Stack Overflaw - Helped Point Me In The Direction For RethinkDB Data Types To Java On Stack Overflow!
 * Ronz#2224 - Helped With RethinkDB Hashmaps (Data Store Embeds) & some RethinkDB to Java Syntax Questions! (Discord)
 * If you request to Push a change to improve the project feel free and all changes you push will be noted here!
 */



public class UserInput {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        public static void main(String[] args)
        {
            Connection conn = r.connection().hostname("73.21.110.242").port(28015).connect();           
            //Setup Scanner
                Scanner scan = new Scanner(System.in);
                //Setup Timestamp
                OffsetDateTime timestamp = OffsetDateTime.now();
                System.out.println(timestamp);
                //Setup Local Values!
                String version = "1.1";
                String builder = "Java Console";
               
                
                //Decide what the scanner will be looking for
                System.out.println("What action will you be completing?: ");
                String action = scan.nextLine();
               
                //Scans For Action Requests
                
                if (action.equals("Auth")){
                         System.out.println("Username: ");
                         String authuser = scan.nextLine();
                         System.out.println("Password: ");
                         String authpass = scan.nextLine();
                             if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(authuser)
                                 .and(row.g("password").eq(authpass)
                                 .and(row.g("gkey").eq("false")
                                 ))).isEmpty().not().run(conn)){
                                 //Put code here for if they need to hook up their account
                           GoogleAuthenticator gAuth = new GoogleAuthenticator();
                           GoogleAuthenticatorKey key = gAuth.createCredentials();
                           System.out.println("You're Google Authentication Code Is:" + key.getKey());
                           //Inputs The Secret Auth Key To Databate
                           r.db("APSCI").table("BankAccounts").update(
                            r.hashMap("username", (authuser))
                                .with("gkey",(key.getKey()))
                                ).run(conn);
                       }
                             else {
                                 System.out.println("We have decteted that you have either:\n Don't have a valid account or Have already setup authy");
                             }
                }
                             
                    if (action.equals("Login")){
                       //Login Action
                            System.out.println("Username: ");
                            String username = scan.nextLine();
                            System.out.println("Password: ");
                            String password = scan.nextLine();
                    
                      //Login Validation
                      //Do Get Google Authenticator Key From Database     
                      String gkey = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                             .g("gkey").nth(0)
                             .run(conn);
                      //Do Get Name From Username
                      String name = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                              .g("name").nth(0)
                             .run(conn);
                      //Do get Savings bal from Username
                      double getsavings = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                              .g("savingsbal").nth(0)
                             .run(conn);
                      //Do get Checking bal from Username
                      double getchecking = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                              .g("checkingbal").nth(0)
                             .run(conn);
                      
                      double currenfunds = 1000;
                      
                    //Checking if user has a valid username & password
                     if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                             .and(row.g("password").eq(password)))
                             .isEmpty().not()
                             .run(conn)){
                         //Checking if the user has a google Key
                          if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                                 .and(row.g("gkey").eq("false")))
                                  .isEmpty()
                                    .run(conn)){
                            System.out.println("GoogleAuthCode: ");
                            int code = Integer.parseInt(scan.nextLine());
                            
                            GoogleAuthenticator gAuth = new GoogleAuthenticator();
                            if(gAuth.authorize((gkey), (code))){
                         r.db("APSCI").table("BankAccountLogs").insert(
                           (r.array(
                            r.hashMap("username", (username))
                                .with("action", "Login")
                                .with("Version",(version))
                                .with("Timestamp", (timestamp))
                                .with("GCode", (code))
                                ))).run(conn);
                         //What to do if they have a valid account &/Or Google Auth Setup & Validated
                         System.out.println("What action will you be completing?: ");
                                System.out.println("1 - Withdraw Funds");
                                System.out.println("2 - Deposit Funds");
                                System.out.println("3 - Transfer Funds");
                                System.out.println("4 - Settings");
                                System.out.println("5 - Admin Portal");
                                String bankaction1 = scan.nextLine();
                                {
                                    //What to do if they want to withdraw funds
                   if (bankaction1.equals("1")){
                    //What do if the user wants to withdraw funds
                    //Grabs Username From UserInput
                   System.out.println("You're Checkings Account - balance is: $" + (getchecking));
                   System.out.println("You're Savings Account - balance is: $" + (getsavings));
                   System.out.println("What account would you like to withdraw funds from?");
                   System.out.println("1 - Checking Account");
                   System.out.println("2 - Savings Account");
                   String withdrawaction = scan.nextLine();
                   //What to do if the user picks withdraw funds
                   if (withdrawaction.equals("1")){
                       System.out.println("You're checking account ballance is: $" + (getchecking));
                       System.out.println("How much money would you like to withdraw?");    
                        double amount = scan.nextDouble();
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
                        double amount = scan.nextDouble();
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
                        double amount = scan.nextDouble();
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
                        double amount1 = scan.nextDouble();
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
                                    .g("checkingbal").nth(0)
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
                }
                   
                            
                            } //End Of User Input!
                           
               }else {
                    //What to do if they don't pick a valid Gkey
                    System.out.println("Invalid Gkey!");
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
                    }//End Of Login Section
                          
                   
                     
                      
                     
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
}
               



