/*
    This file is part of AuthAPI by Kaz Voeten.

    AuthAPI is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AuthAPI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AuthAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.data;

import server.create.CreationResponseCode;
import server.account.Account;
import server.crypto.BCrypt;
import server.crypto.TokenFactory;
import server.login.LoginResponseCode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author kaz_v
 */
public class Database {

    private static final HikariConfig pConfig; //Hikari database config.
    private static final HikariDataSource pDataSource; //Hikari datasource based on config.
    private static HashMap<String, Pair<String, Date>> authCodes = new HashMap<>(); //Map of account verification codes sorted by email.

    static {
        //Check if file exists, if not: create and use default file.
        File properties = new File("database.properties");
        if (!properties.exists()) {
            try (FileOutputStream fout = new FileOutputStream(properties)) {
                PrintStream out = new PrintStream(fout);
                out.println("dataSourceClassName=org.mariadb.jdbc.MariaDbDataSource");
                out.println("dataSource.user=root");
                out.println("dataSource.password=");
                out.println("dataSource.databaseName=service");
                out.println("dataSource.portNumber=3306");
                out.println("dataSource.serverName=localhost");
                fout.flush();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("No database.properties file found. A default one has been generated.");
        }
        pConfig = new HikariConfig("database.properties");
        pDataSource = new HikariDataSource(pConfig);
    }

    public static Account GetAccountByToken(String sToken) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE sToken = ?");
            ps.setString(1, sToken);
            ResultSet rs = ps.executeQuery();
            Account pRet = null;
            if (rs.first()) {
                pRet = new Account(
                        rs.getInt("nAccountID"),
                        rs.getInt("nNexonCash"),
                        rs.getInt("nMaplePoint"),
                        rs.getInt("nMileage"),
                        rs.getBoolean("bVerified"),
                        rs.getString("sAccountName"),
                        rs.getString("sToken"),
                        rs.getString("sEmail"),
                        rs.getString("sIP"),
                        rs.getString("sSecondPW"),
                        rs.getByte("nGradeCode"),
                        rs.getByte("nState"),
                        rs.getByte("nGender"),
                        rs.getDate("pCreateDate"),
                        rs.getDate("pLastLoadDate"),
                        rs.getDate("pBirthDate"),
                        rs.getShort("nLastWorldID")
                );
            }
            rs.close();
            ps.close();
            return pRet;
        } catch (Exception ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Account GetAccountByName(String sAccountName) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE sAccountName = ? OR sEmail = ?");
            ps.setString(1, sAccountName);
            ps.setString(2, sAccountName);
            ResultSet rs = ps.executeQuery();
            Account pRet = null;
            if (rs.first()) {
                pRet = new Account(
                        rs.getInt("nAccountID"),
                        rs.getInt("nNexonCash"),
                        rs.getInt("nMaplePoint"),
                        rs.getInt("nMileage"),
                        rs.getBoolean("bVerified"),
                        rs.getString("sAccountName"),
                        rs.getString("sToken"),
                        rs.getString("sEmail"),
                        rs.getString("sIP"),
                        rs.getString("sSecondPW"),
                        rs.getByte("nGradeCode"),
                        rs.getByte("nState"),
                        rs.getByte("nGender"),
                        rs.getDate("pCreateDate"),
                        rs.getDate("pLastLoadDate"),
                        rs.getDate("pBirthDate"),
                        rs.getShort("nLastWorldID")
                );
            }
            rs.close();
            ps.close();
            return pRet;
        } catch (Exception ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static CreationResponseCode CheckDuplicatedID(String sAccountName, String sEmail) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE sAccountName = ? OR sEmail = ?");
            ps.setString(1, sAccountName);
            ps.setString(2, sEmail);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                if (rs.getBoolean("bVerified")) {
                    return CreationResponseCode.FAILED;
                }
                return CreationResponseCode.EXISTS_UNVERIFIED;
            }
            return CreationResponseCode.SUCCESS;
        } catch (Exception ex) {
            return CreationResponseCode.FAILED;
        }
    }

    public static LoginResponseCode CheckPassword(String sAccount, String sPassword) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE sAccountName = ? OR sEmail = ?");
            ps.setString(1, sAccount);
            ps.setString(2, sAccount);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                if (rs.getByte("nBanned") > 0) {
                    return LoginResponseCode.BANNED;
                }
                if (rs.getByte("nState") > 0) {
                    return LoginResponseCode.BLOCKED;
                }
                if (BCrypt.checkpw(sPassword, rs.getString("sPassword"))) {
                    return LoginResponseCode.SUCCESS;
                } else {
                    return LoginResponseCode.WRONG_INFO;
                }
            } else {
                return LoginResponseCode.WRONG_INFO;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return LoginResponseCode.SERVICE_UNAVAILABLE;
        }
    }

    public static String GenerateToken(Account pAccount) {
        String sToken = TokenFactory.genToken(pAccount.getnAccountID(), pAccount.getsAccountName(), new Date());
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET sToken=? WHERE sAccountName=?");
            ps.setString(1, sToken);
            ps.setString(2, pAccount.getsAccountName());
            ps.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ERROR";
        }
        return sToken;
    }

    public static CreationResponseCode CreateAccount(String sEmail, String sAccountName,
            String sPassword, String pBirthDate, String nGender) {

        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(""
                    + "INSERT INTO accounts (sAccountName, sEmail, sPassword, pBirthDate, pCreateDate, pLastLoadDate, nGender) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, sAccountName);
            ps.setString(2, sEmail);
            ps.setString(3, BCrypt.hashpw(sPassword, BCrypt.gensalt()));
            ps.setDate(4, new java.sql.Date(
                    new DateTime(
                            Integer.parseInt(pBirthDate.substring(4, 8)), //y
                            Integer.parseInt(pBirthDate.substring(2, 4)), //m
                            Integer.parseInt(pBirthDate.substring(0, 2)), //d
                            0, 0, DateTimeZone.getDefault()
                    ).getMillis())
            );
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            ps.setByte(7, (byte) Integer.parseInt(nGender));
            ps.execute();
            return CreationResponseCode.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return CreationResponseCode.FAILED;
        }
    }

    public static String GetAuthCode(String sEmail) {
        if (authCodes.containsKey(sEmail)) {
            return authCodes.get(sEmail).getKey();
        }
        return "";
    }

    public static void CreateAuthCode(String sEmail) {
        authCodes.put(sEmail, new Pair<>(TokenFactory.genAuthenCode(), new Date()));
    }

    public static boolean SetAccountVerified(String sEmail) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET bVerified=true WHERE sEmail=?");
            ps.setString(1, sEmail);
            ps.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean SetSecondPW(String sToken, String sSecondPW) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET sSecondPW=? WHERE sToken=?");
            ps.setString(1, sSecondPW);
            ps.setString(2, sToken);
            ps.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean SetIP(String sToken, String sIP) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET sIP=? WHERE sToken=?");
            ps.setString(1, sIP);
            ps.setString(2, sToken);
            ps.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean SetState(String sToken, byte nState) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET nState=? WHERE sToken=?");
            ps.setByte(1, nState);
            ps.setString(2, sToken);
            ps.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean Ban(String sToken) {
        try (Connection connection = pDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET nBanned=1 WHERE sToken=?");
            ps.setString(1, sToken);
            ps.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static JSONObject GetBlockList() {
        JSONObject pBlockListJSON = new JSONObject();
        try (Connection connection = pDataSource.getConnection()) {

            //IP
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ipban");
            ResultSet rs = ps.executeQuery();
            JSONArray aBlockedIP = new JSONArray();
            while (rs.next()) {
                JSONObject pBlock = new JSONObject();
                pBlock.put("sIP", rs.getString("sIP"));
                pBlock.put("pBanDate", rs.getDate("pBanDate"));
                pBlock.put("pBanEndDate", rs.getDate("pBanEndDate"));
                aBlockedIP.put(pBlock);
            }

            //HWID
            ps = connection.prepareStatement("SELECT * FROM hwidban");
            rs = ps.executeQuery();
            JSONArray aBlockedHWID = new JSONArray();
            while (rs.next()) {
                JSONObject pBlock = new JSONObject();
                pBlock.put("sHWID", rs.getString("sHWID"));
                pBlock.put("pBanDate", rs.getDate("pBanDate"));
                pBlock.put("pBanEndDate", rs.getDate("pBanEndDate"));
                aBlockedHWID.put(pBlock);
            }

            //MAC
            ps = connection.prepareStatement("SELECT * FROM macban");
            rs = ps.executeQuery();
            JSONArray aBlockedMAC = new JSONArray();
            while (rs.next()) {
                JSONObject pBlock = new JSONObject();
                pBlock.put("sMAC", rs.getString("sMAC"));
                pBlock.put("pBanDate", rs.getDate("pBanDate"));
                pBlock.put("pBanEndDate", rs.getDate("pBanEndDate"));
                aBlockedMAC.put(pBlock);
            }

            //Finalize
            pBlockListJSON.put("ip", aBlockedIP);
            pBlockListJSON.put("hwid", aBlockedHWID);
            pBlockListJSON.put("mac", aBlockedMAC);
            return pBlockListJSON;
        } catch (Exception ex) {
            ex.printStackTrace();
            return pBlockListJSON;
        }
    }

    public static boolean AddBlock(int nType, String sValue, long nDuration) {
        try (Connection connection = pDataSource.getConnection()) {
        PreparedStatement ps;
            switch (nType) {
                case 1:
                    ps = connection.prepareStatement("INSERT INTO ipban (sIP, pBanDate, pBanEndDate) VALUES (?, ?, ?)");
                    break;
                case 2:
                    ps = connection.prepareStatement("INSERT INTO hwidban (sHWID, pBanDate, pBanEndDate) VALUES (?, ?, ?)");
                    break;
                case 3:
                    ps = connection.prepareStatement("INSERT INTO macban (sMAC, pBanDate, pBanEndDate) VALUES (?, ?, ?)");
                    break;
                default:
                    return false;
            }
            ps.setString(1, sValue);
            ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis() + nDuration));
            ps.executeUpdate();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
