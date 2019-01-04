package org.aagrandpre.bank.core.utils;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class SettingsGSON {

    public static void main(String[] args) throws IOException, InterruptedException {
        JSONParser parser = new JSONParser();
        try {
            // parsing file "JSONExample.json"
            Object obj = new JSONParser().parse(new FileReader("bankconfig.json"));
            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;
            // getting firstName and lastName
            String dbtype = (String) jo.get("dbconnected");
            String address = (String) jo.get("lastName");
            //Print out first/lastname
            System.out.println(dbtype);
            System.out.println(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}