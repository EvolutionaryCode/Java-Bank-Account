package org.aagrandpre.bank;
//RethinkDB Imports
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;


/**
 *@author Austin Grandpre
 */
public class Database {
     public static RethinkDB r = RethinkDB.r; static {
     }
    public static void main(String[] args) {
        Connection conn = r.connection().hostname("YourIPAddress").port(28015).connect();
    }
}
