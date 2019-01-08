package org.aagrandpre.bank.core;

import com.warrenstrange.googleauth.GoogleAuthenticator;

public class userActionsRead {


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
}
