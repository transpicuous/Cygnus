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
package account;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.LoginServerSocket;
import login.packet.LoopBackPacket;
import net.OutPacket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import server.Configuration;
import character.AvatarData;
import java.util.Date;
import org.json.JSONArray;

/**
 *
 * @author Kaz Voeten
 */
public class APIFactory {

    private static APIFactory instance;
    private final OkHttpClient client = new OkHttpClient();

    public void RequestAccount(LoginServerSocket pSocket, long nSessionID, String sToken) {
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
                        throw new IOException("[APIFactory] Unexpected response " + response);
                    }
                    JSONObject account = new JSONObject(responseBody.string());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Account pAccount = new Account(
                            account.getInt("nAccountID"),
                            nSessionID,
                            account.getString("sAccountName"),
                            account.getString("sIP"),
                            account.getString("sSecondPW"),
                            (byte) account.getInt("nState"),
                            (byte) account.getInt("nGender"),
                            sdf.parse(account.getString("pLastLoadDate")),
                            sdf.parse(account.getString("pBirthDate")),
                            sdf.parse(account.getString("pnCreateDate")),
                            (byte) account.getInt("nGradeCode"),
                            sToken,
                            (short) account.getInt("nLastWorldID"),
                            account.getInt("nNexonCash"),
                            account.getInt("nMaplePoint"),
                            account.getInt("nMileage")
                    );

                    pSocket.mAccountStorage.put(nSessionID, pAccount);

                    OutPacket oPacket = new OutPacket(LoopBackPacket.AccountInformation);
                    oPacket.EncodeLong(pAccount.nSessionID);
                    pAccount.Encode(oPacket);
                    List<AvatarData> avatars = pAccount.GetAvatars(pAccount.nAccountID, true);
                    oPacket.EncodeByte(avatars.size());
                    avatars.forEach((pAvatar) -> {
                        oPacket.EncodeInt(pAvatar.nCharlistPos);
                        pAvatar.Encode(oPacket, false);
                    });
                    pSocket.SendPacket(oPacket);
                } catch (ParseException ex) {
                    Logger.getLogger(APIFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void UpdateSecondPW(LoginServerSocket pSocket, long nSessionID, String sToken, String sSecondPW) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/secondpw?sToken=" + sToken + "&sSecondPW=" + sSecondPW)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OutPacket oPacket = new OutPacket(LoopBackPacket.SetSPWResult);
                oPacket.EncodeLong(nSessionID);
                oPacket.EncodeBool(false);
                pSocket.SendPacket(oPacket);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("[APIFactory] Unexpected response " + response);
                    }

                    String result = responseBody.string();
                    if (result.equals("true")) {
                        Account pAccount = pSocket.mAccountStorage.get(nSessionID);
                        if (pAccount != null) {
                            pAccount.sSecondPW = sSecondPW;
                        }
                        OutPacket oPacket = new OutPacket(LoopBackPacket.SetSPWResult);
                        oPacket.EncodeLong(nSessionID);
                        oPacket.EncodeBool(true);
                        pSocket.SendPacket(oPacket);

                    } else {
                        OutPacket oPacket = new OutPacket(LoopBackPacket.SetSPWResult);
                        oPacket.EncodeLong(nSessionID);
                        oPacket.EncodeBool(false);
                        pSocket.SendPacket(oPacket);
                    }
                } catch (Exception ex) {
                    OutPacket oPacket = new OutPacket(LoopBackPacket.SetSPWResult);
                    oPacket.EncodeLong(nSessionID);
                    oPacket.EncodeBool(false);
                    pSocket.SendPacket(oPacket);
                    Logger.getLogger(APIFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void Logout(String sToken) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/logout?sToken=" + sToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response rspns) throws IOException {

            }
        });
    }

    public void Block(int nType, String sValue, long nDuration) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/block?nType=" + nType + "&sValue=" + sValue + "&nDuration=" + nDuration)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response rspns) throws IOException {

            }
        });
    }

    public void Ban(String sToken) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/ban?sToken=" + sToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response rspns) throws IOException {

            }
        });
    }

    public void SetIP(String sToken, String sIP) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/ip?sToken=" + sToken + "&sIP=" + sIP)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response rspns) throws IOException {

            }
        });
    }

    public void GetBlockList(LoginServerSocket pSocket) {
        Request request = new Request.Builder()
                .url(Configuration.AUTH_API_URL + "/blocklist")
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
                        throw new IOException("[APIFactory] Unexpected response " + response);
                    }
                    JSONObject pBlockList = new JSONObject(responseBody.string());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    JSONArray pIPBlock = pBlockList.getJSONArray("ip");
                    JSONArray pHWIDBlock = pBlockList.getJSONArray("hwid");
                    JSONArray pMACBlock = pBlockList.getJSONArray("mac");

                    OutPacket oPacket = new OutPacket(LoopBackPacket.BlockList);

                    //IP
                    oPacket.EncodeInt(pIPBlock.length());
                    pIPBlock.forEach((pBlock) -> {
                        JSONObject pJSONBlock = (JSONObject) pBlock;
                        try {
                            if ((sdf.parse(pJSONBlock.getString("pBanEndDate"))).before(new Date())) {
                                oPacket.EncodeString(pJSONBlock.getString("sIP"));
                            } else {
                                oPacket.EncodeString("");
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(APIFactory.class.getName()).log(Level.SEVERE, null, ex);
                            oPacket.EncodeString("");
                        }
                    });

                    //HWID
                    oPacket.EncodeInt(pHWIDBlock.length());
                    pHWIDBlock.forEach((pBlock) -> {
                        JSONObject pJSONBlock = (JSONObject) pBlock;
                        try {
                            if ((sdf.parse(pJSONBlock.getString("pBanEndDate"))).before(new Date())) {
                                oPacket.EncodeString(pJSONBlock.getString("sHWID"));
                            } else {
                                oPacket.EncodeString("");
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(APIFactory.class.getName()).log(Level.SEVERE, null, ex);
                            oPacket.EncodeString("");
                        }
                    });

                    //MAC
                    oPacket.EncodeInt(pMACBlock.length());
                    pMACBlock.forEach((pBlock) -> {
                        JSONObject pJSONBlock = (JSONObject) pBlock;
                        try {
                            if ((sdf.parse(pJSONBlock.getString("pBanEndDate"))).before(new Date())) {
                                oPacket.EncodeString(pJSONBlock.getString("sHWID"));
                            } else {
                                oPacket.EncodeString("");
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(APIFactory.class.getName()).log(Level.SEVERE, null, ex);
                            oPacket.EncodeString("");
                        }
                    });

                    pSocket.SendPacket(oPacket);
                } catch (Exception ex) {
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
