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

/**
 *
 * @author Kaz Voeten
 */
public enum LoopBackPacket {

    CheckPasswordResult(0),
    WorldInformation(1),
    LastConnectedWorld(2),
    RecommendWorldMessage(3),
    SetClientKey(4),
    SetPhysicalWorldID(5),
    SelectWorldResult(6),
    SelectCharacterResult(7),
    AccountInfoResult(8),
    CreateMapleAccountResult(9),
    CheckDuplicatedIDResult(10),
    CreateCharacterResult(11),
    DeleteCharacterResult(12),
    ReservedDeleteCharacterResult(13),
    ReservedDeleteCharactercancelResult(14),
    RenameCharacterResult(15),
    SetCharacterID(16),
    MigrateCommand(17),
    AliveReq(37), //v188
    PingCheckResult_ClientToGame(19),
    AuthenCodeChanged(20),
    AuthenMessage(21),
    SecurityPacket(22),
    PrivateServerPacket(23),
    ChangeSPWResult(31),
    CheckSPWExistResult(32),
    CheckWebLoginEmailID(33),
    CheckCrcResult(34),
    AlbaRequestResult(35),
    ApplyHotFix(40), //v188
    UserLimitResult(38),
    JOB_ORDER(45),
    NMCOResult(47),
    CharacterBurning(346);

    private int value;

    private LoopBackPacket(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }
}
