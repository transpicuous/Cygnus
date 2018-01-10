/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.accounts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import server.Configuration;

/**
 *
 * @author Kaz Voeten
 */
public class APIFactory {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static Account GetAccount(int nSessionID, String sToken) {
        try {
            JSONObject account = readJsonFromUrl(Configuration.AUTH_API_URL + "/account?token=" + sToken);
            return new Account(
                    account.getInt("id"), 
                    nSessionID, 
                    account.getString("name"), 
                    account.getString("ip"),
                    account.getString("pic"),
                    (byte) account.getInt("state"),
                    (byte) account.getInt("gender"),
                    new Date(account.getString("history")),
                    new Date(account.getString("birthday")),
                    (byte) account.getInt("admin")
            );
        } catch (Exception ex) {
            return null;
        }
    }
}
