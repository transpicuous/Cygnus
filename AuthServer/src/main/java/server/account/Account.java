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
package server.account;

import java.util.Date;

/**
 *
 * @author kaz_v
 */
public class Account {

    private final int nAccountID, nNexonCash, nMaplePoint, nMileage;
    private final boolean bVerified;
    private final String sAccountName, sToken, sEmail, sIP, sSecondPW;
    private final byte nState, nGender, nGradeCode;
    private final Date pCreateDate, pLastLoadDate, pBirthDate;
    private final short nLastWorldID;

    public Account(int nAccountID, int nNexonCash, int nMaplePoint, int nMileage,
            boolean bVerified, String sAccountName, String sToken, String sEmail, String sIP,
            String sSecondPW, byte nGradeCode, byte nState, byte nGender, Date pCreateDate, Date pLastLoadDate,
            Date pBirthDate, short nLastWorldID) {
        this.nAccountID = nAccountID;
        this.nNexonCash = nNexonCash;
        this.nMaplePoint = nMaplePoint;
        this.nMileage = nMileage;
        this.bVerified = bVerified;
        this.sAccountName = sAccountName;
        this.sToken = sToken;
        this.sEmail = sEmail;
        this.sIP = sIP;
        this.sSecondPW = sSecondPW;
        this.nGradeCode = nGradeCode;
        this.nState = nState;
        this.nGender = nGender;
        this.pCreateDate = pCreateDate;
        this.pLastLoadDate = pLastLoadDate;
        this.pBirthDate = pBirthDate;
        this.nLastWorldID = nLastWorldID;
    }

    public int getnAccountID() {
        return nAccountID;
    }

    public int getnGradeCode() {
        return nGradeCode;
    }

    public int getnNexonCash() {
        return nNexonCash;
    }

    public int getnMaplePoint() {
        return nMaplePoint;
    }

    public int getnMileage() {
        return nMileage;
    }

    public boolean getbVerified() {
        return bVerified;
    }

    public String getsAccountName() {
        return sAccountName;
    }

    public String getsToken() {
        return sToken;
    }

    public String getsEmail() {
        return sEmail;
    }

    public String getsIP() {
        return sIP;
    }

    public String getsSecondPW() {
        return sSecondPW;
    }

    public byte getnState() {
        return nState;
    }

    public byte getnGender() {
        return nGender;
    }

    public Date getpnCreateDate() {
        return pCreateDate;
    }

    public Date getpLastLoadDate() {
        return pLastLoadDate;
    }

    public Date getpBirthDate() {
        return pBirthDate;
    }

    public Short getnLastWorldID() {
        return nLastWorldID;
    }

}
