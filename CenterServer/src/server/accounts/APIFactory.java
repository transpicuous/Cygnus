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

import com.zaxxer.hikari.HikariDataSource;
import database.Database;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.CLoginServerSocket;
import login.packet.LoopBackPacket;
import netty.OutPacket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import server.Configuration;
import user.AvatarData;

/**
 *
 * @author Kaz Voeten
 */
public class APIFactory {

    private static APIFactory instance;
    private final OkHttpClient client = new OkHttpClient();

    public void RequestAccount(CLoginServerSocket pSocket, int nSessionID, String sToken) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/account?token=" + sToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("[APIFactory] Unexpected code " + response);
                    }
                    JSONObject account = new JSONObject(responseBody.string());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Account pAccount = new Account(
                            account.getInt("id"),
                            nSessionID,
                            account.getString("name"),
                            account.getString("ip"),
                            account.getString("pic"),
                            (byte) account.getInt("state"),
                            (byte) account.getInt("gender"),
                            sdf.parse(account.getString("history")),
                            sdf.parse(account.getString("birthday")),
                            (byte) account.getInt("admin")
                    );

                    OutPacket oPacket = new OutPacket();
                    oPacket.EncodeShort(LoopBackPacket.AccountInformation.getValue());
                    oPacket.EncodeInteger(pAccount.nSessionID);
                    pAccount.Encode(oPacket);
                    List<AvatarData> avatars = pAccount.GetAvatars(pAccount.nAccountID, Database.GetConnection(), true);
                    oPacket.Encode(avatars.size());
                    avatars.forEach((pAvatar) -> {
                        oPacket.EncodeInteger(pAvatar.nCharlistPos);
                        pAvatar.Encode(oPacket, false);
                    });
                    pSocket.SendPacket(oPacket.ToPacket());
                } catch (ParseException ex) {
                    Logger.getLogger(APIFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public static APIFactory GetInstance() {
        if (instance == null) {
            instance = new APIFactory();
        }
        return instance;
    }
}
