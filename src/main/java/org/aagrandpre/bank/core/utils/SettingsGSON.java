package org.aagrandpre.bank.core.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SettingsGSON {
    public SettingsGSON() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new InputStreamReader(SettingsGSON.class.getResourceAsStream("/config.json")));
            JSONObject jsonObject = (JSONObject)obj;
            //JSONObject arr = (JSONObject)jsonObject.get(0);
            JSONObject arguments = (JSONObject)jsonObject.get(0);

            for(int i = 0; i < arguments.size(); ++i) {
                JSONObject object = (JSONObject)arguments.get(i);
                System.out.println(object);
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }
}
