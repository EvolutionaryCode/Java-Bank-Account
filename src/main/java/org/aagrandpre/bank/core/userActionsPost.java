package org.aagrandpre.bank.core;

public class userActionsPost {
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
    private static boolean newLogin(String name, String username, String password, String language, Boolean showbal) {
        //What to do if we need to store a new account
        r.db("APSCI").table("BankAccounts").insert(r.array(
                r.hashMap("name", (name))
                        .with("username", (username))
                        .with("password", (password))
                        .with("level", "user")
                        .with("created", (timestamp))
                        .with("createdwith", (builder))
                        .with("savingsbal", 4000000.25)
                        .with("checkingbal", 100.50)
                        .with("language", (language))
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
}
