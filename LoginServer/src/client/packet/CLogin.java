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
package client.packet;

import client.Client;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import netty.OutPacket;
import netty.Packet;

/**
 *
 * @author Kaz Voeten
 */
public class CLogin {

    public static Packet Ping() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.AliveReq.getValue());
        return oPacket.ToPacket();
    }

    public static Packet LastConnectedWorld(int world) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.LastConnectedWorld.getValue());
        oPacket.EncodeInteger(world);
        return oPacket.ToPacket();
    }

    public static Packet RecommendWorldMessage(int world) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.RecommendWorldMessage.getValue());
        oPacket.Encode(1);
        oPacket.EncodeInteger(world);
        oPacket.EncodeString("The greatest world for starting anew!");
        return oPacket.ToPacket();
    }

    public static Packet UserLimitResult(int status) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.UserLimitResult.getValue());
        oPacket.EncodeShort(status);
        return oPacket.ToPacket();
    }

    public static Packet getLoginFailed(int reason) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.CheckPasswordResult.getValue());
        oPacket.Encode(reason);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        return oPacket.ToPacket();
    }

    public static Packet getBanMessage(int reason, long time) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.CheckPasswordResult.getValue());
        oPacket.Encode(2);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0x00);
        oPacket.Encode(reason);
        oPacket.EncodeLong(time);
        return oPacket.ToPacket();
    }

    public static Packet CheckPasswordResult(Client client) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.CheckPasswordResult.getValue());

        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        //oPacket.EncodeString(client.getAccountName());
        //oPacket.EncodeInteger(client.getAccountId());
        //oPacket.Encode(client.getAccountGender());
        //oPacket.Encode(client.isGM());
        //oPacket.EncodeInteger(client.isGM() ? 0x80 : 0); // admin permissions subcode (incorrect rn)
        oPacket.EncodeInteger(0);
        //oPacket.Encode(client.isGM());
        //oPacket.EncodeString(client.getAccountName());
        oPacket.Encode(3);
        oPacket.Encode(0);
        oPacket.EncodeLong(0); // quiet ban time // 10
        oPacket.EncodeLong(0); // creation time     // 18
        oPacket.EncodeInteger(0x20);

        //JobOrder.Encode(oPacket);
        oPacket.Encode(0);
        oPacket.EncodeInteger(-1);
        oPacket.Encode(1);
        oPacket.Encode(1);
        oPacket.EncodeLong(0); // session id     // 18

        return oPacket.ToPacket();
    }

    /*
    public static Packet WorldInformation(World world) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.WorldInformation.getValue());

        oPacket.Encode(world.getWorldId());
        oPacket.EncodeString(world.getWorldName());
        oPacket.Encode(world.getEventFlag());
        oPacket.EncodeString(world.getEventMessage());

        oPacket.EncodeShort(world.getEventExp());
        oPacket.EncodeShort(world.getEventDrop());
        oPacket.Encode(world.disableCharCreation());

        Collection<GameChannel> channels = world.getChannels();
        oPacket.Encode(channels.size());
        for (GameChannel wc : channels) {
            oPacket.EncodeString(new StringBuilder(world.getWorldName()).append("-").append(wc.ID).toString());
            oPacket.EncodeInteger(wc.LOAD);
            oPacket.Encode(wc.ID);
            oPacket.EncodeShort(wc.ID - 1);
        }

        List<Balloon> balloons = BalloonConstants.getBalloons();
        if (balloons == null) {
            oPacket.EncodeShort(0);
        } else {
            oPacket.EncodeShort(balloons.size());
            for (Balloon balloon : BalloonConstants.getBalloons()) {
                oPacket.EncodeShort(balloon.nX);
                oPacket.EncodeShort(balloon.nY);
                oPacket.EncodeString(balloon.sMessage);
            }
        }

        oPacket.EncodeInteger(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }
     */
    public static Packet getEndOfWorldList() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.WorldInformation.getValue());

        oPacket.Encode(0xFF);
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    /*
    public static Packet SelectWorldResult(Client c, List<AvatarData> avatars, boolean bIsEditedList) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.SelectWorldResult.getValue());

        byte nDay = 0;
        oPacket.Encode(nDay);
        if (nDay == 61) {
            boolean SendOTPForWebLaunching = false;
            oPacket.Encode(SendOTPForWebLaunching);
        }
        oPacket.EncodeString(c.getWorldID() == 45 ? "reboot" : "normal");//topkek
        oPacket.EncodeInteger(c.getWorldID());// worldID?
        oPacket.Encode(false);//burning event blocked

        /**
         * if bigger than 0 write ReservedDeleteCharacter data, which writes
         * which character is gonna be deleted at which time if I ever wanna do
         * scheduled character deletions for limited access to pink bean or
         * someshit
     */
 /*
        oPacket.EncodeInteger(0);

        oPacket.EncodeInteger(0);//hightime
        oPacket.EncodeInteger(0);///lowtime

        oPacket.Encode(bIsEditedList); //bIsEditedList for after you reorganize the charlist
        Collections.sort(avatars, (AvatarData o1, AvatarData o2) -> o1.getCharListPosition() - o2.getCharListPosition());
        if (bIsEditedList) {
            oPacket.EncodeInteger(avatars.size());
            for (AvatarData avatar : avatars) {
                oPacket.EncodeInteger(avatar.getCharacterID());
            }
        } else {
            oPacket.EncodeInteger(0);//0 chars edited
        }

        oPacket.Encode((byte) avatars.size());
        for (AvatarData avatar : avatars) {
            avatar.Encode(oPacket, false);
        }

        oPacket.Encode((byte) 0); //bHasPic
        oPacket.Encode(false); //bQuerrySSNOnCreateNewCharacter LMAO LEZ STEAL THOSE NUMBERS
        oPacket.EncodeInteger(c.getCharacterSlots());
        oPacket.EncodeInteger(0);//amount of chars bought with CS coupons? nBuyCharCount
        oPacket.EncodeInteger(-1);//event new char job (maybe can be used for pinkbean)
        oPacket.EncodeInteger(0);//highTimeStamp
        oPacket.EncodeInteger(0);//lowTimeStamp
        oPacket.Encode((byte) 0); //enables the name change UI. value is count of names allowed to change
        oPacket.Encode(c.getWorldID() == 45); //based on world ID so might be reboot related

        return oPacket.ToPacket();
    }
     */
    public static Packet CharacterBurning(byte nType, int dwCharacterID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.CharacterBurning.getValue());
        oPacket.Encode(nType);
        oPacket.EncodeInteger(dwCharacterID);
        return oPacket.ToPacket();
    }

    public static Packet SelectWorldResult() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.SelectWorldResult.getValue());
        oPacket.Encode(true);
        return oPacket.ToPacket();
    }

    public static Packet DuplicateIDResponse(String name, boolean taken) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.CheckDuplicatedIDResult.getValue());
        oPacket.EncodeString(name);
        oPacket.Encode(!taken);
        return oPacket.ToPacket();
    }

    /*
    public static Packet CreateCharacterResult(AvatarData avatar, boolean success) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.CreateCharacterResult.getValue());

        oPacket.Encode(!success);
        if (success) {
            avatar.Encode(oPacket, false);
        }

        return oPacket.ToPacket();
    }
     */

 /*
    public static Packet DeleteCharacterResult(int cid, int state) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.DeleteCharacterResult.getValue());

        oPacket.EncodeInteger(cid);
        oPacket.Encode(state);

        return oPacket.ToPacket();
    }
     */

 /*
    public static Packet SelectCharacterResult(GameChannel gc, int cid) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.SelectCharacterResult.getValue());

        oPacket.EncodeShort(0x00);
        oPacket.Encode(gc.IP);
        oPacket.EncodeShort(gc.PORT);
        oPacket.EncodeInteger(cid);
        oPacket.Fill(0x00, 5);

        return oPacket.ToPacket();
    }
     */
    public static Packet SecurityPacket() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.SecurityPacket.getValue());
        oPacket.Encode(0x01);
        return oPacket.ToPacket();
    }

    public static Packet ApplyHotFix() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.ApplyHotFix.getValue());
        oPacket.Encode(true);
        return oPacket.ToPacket();
    }

    public static Packet NCMOResult() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.NMCOResult.getValue());
        oPacket.Encode(true);
        return oPacket.ToPacket();
    }

    public static Packet PrivateServerPacket(int dwCurrentThreadID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.PrivateServerPacket.getValue());

        int response = dwCurrentThreadID ^ LP.PrivateServerPacket.getValue();
        oPacket.EncodeInteger(response);

        return oPacket.ToPacket();
    }

    /*
    public static Packet JobOrderPacket() {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(LP.JOB_ORDER.getValue());
        
        JobOrder.Encode(oPacket);
        
        return oPacket.ToPacket();
    }
     */
}
