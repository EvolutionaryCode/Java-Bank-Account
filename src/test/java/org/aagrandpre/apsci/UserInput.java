package org.aagrandpre.apsci;
//Import Java Tools
import java.util.Scanner;
//Import RethinkDB Tools
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.gen.exc.ReqlError;
import com.rethinkdb.gen.exc.ReqlQueryLogicError;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import static org.aagrandpre.bank.Database.r;

//Timestamp Imports
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;

//Auth
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.util.List;

/**
 * @author Austin Grandpre
 * August 29th, 2018
 * Period - 1
 * Allows users to input several numbers which are then stored as an array
 * 
 * Changelog - 
 * August 31st, 2019 - Added Multi-Scanner Functionality & Login System
 * September 5th 2019 - Added RethinkDB Login System Validation
 * September 6th 2019 - Added Success/Failed Login Logs To RethinkDB
 */



public class UserInput {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
         public static Double[] grabInput(){
            Connection conn = r.connection().hostname("73.21.110.242").port(28015).connect();            
                Scanner scan = new Scanner(System.in);
                OffsetDateTime timestamp = OffsetDateTime.now();
                System.out.println(timestamp);
                String version = "0.1";
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
                     //To-Do Add Google Authy Checker Whenever They Login!\
                                      
                      String gkey = r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username))
                             .g("gkey").nth(0)
                             .run(conn);
                         
                            System.out.println((gkey));
                    
                     if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                             .and(row.g("password").eq(password)))
                             .isEmpty().not()
                             .run(conn)){
                         
                          if (r.db("APSCI").table("BankAccounts").filter(row ->
                         row.g("username").eq(username)
                                 .and(row.g("gkey").eq("false")))
                                  .isEmpty()
                                    .run(conn)){
                            System.out.println("GoogleAuthCode: ");
                            int code = scan.nextInt();
                            
                            GoogleAuthenticator gAuth = new GoogleAuthenticator();
                            if(gAuth.authorize((gkey), (code))){
                                System.out.println("Welcome Google");
                            }
                            else{
                                System.out.println("Invalid Google Auth Key!");
                                
                            }
                           
                          }
                          else{
                         r.db("APSCI").table("BankAccountLogs").insert(
                           (r.array(
                            r.hashMap("username", (username))
                                .with("action", "Login")
                                .with("Version",(version))
                                .with("Timestamp", (timestamp))
                                ))).run(conn);
                         System.out.println("Welcome\n" + username);
                          }
                      }
                       else {
                         r.db("APSCI").table("BankAccountLogs").insert(
                           (r.array(
                            r.hashMap("attemptedusername", (username))
                                .with("action", "FailedLogin")
                                .with("Version",(version))
                                .with("Timestamp", (timestamp))
                                ))).run(conn);
                         System.out.println("No User Was Found! We have logged your information");
                      }
                      return null;
                    }
                     
                    if (action.equals("Numbers")){
                        //Decide the number of numbers to be used in the array
                        System.out.print("Enter how many numbers you will input: ");
                        Integer numOfNumbers = Integer.parseInt(scan.nextLine());
                        
                        //Create a string array to store the numbers that will be used
                        Double arrayOfNumbers[] = new Double[numOfNumbers];
                        for (int i = 0; i < arrayOfNumbers.length; i++) {
			System.out.println("Enter the number " + (i+1) + " : ");
		        arrayOfNumbers[i] = Double.parseDouble(scan.nextLine());
                    }
		    
                        //Now show your numbers
                        System.out.println("My numbers : ");
                        System.out.println(arrayOfNumbers[0]);
                        for (int i = 1;i < arrayOfNumbers.length;i++) {
                            System.out.print(" , " + arrayOfNumbers[i]);
                        }
                        System.out.println();
		  
                        return arrayOfNumbers;
                        
                
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
                         System.out.println("Please Enter Language (en or es)");
                         String language = scan.nextLine();
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
                             .with("gkey","false")
                             .with("accounts", r.array(
                                 r.hashMap("account", "savings")
                                  .with("bal", "400"),
                                 r.hashMap("account", "checking")
                                  .with("bal", "300")))
                             .with("settings", r.array(
                                 r.hashMap("lang", (language)),
                                 r.hashMap("pin", (pin)),
                                 r.hashMap("balshow", (showbal))
                                 
                        )))).run(conn);
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
                      
                             
              
  
                  }
        return null;
                    }

    
       
                    }
               



