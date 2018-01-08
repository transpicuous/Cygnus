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
public enum ClientPacket {
    DummyCode(100),
    BeginSocket(101),
    SecurityPacket(102),
    PermissionRequest(103),
    LoginBasicInfo(104),
    CheckLoginAuthInfo(105),
    SelectWorld(106),
    CheckSPWRequest(107),
    SelectCharacter(108),
    CheckSPWExistRequest(109),
    MigrateIn(110),
    WorldInfoLogoutRequest(114),
    WorldInfoForShiningRequest(115),
    CheckDuplicatedID(116),
    LogoutWorld(117),
    PermissionRequest_Fake(118),
    CheckLoginAuthInfo_Fake(119),
    CreateMapleAccount_Fake(120),
    SelectAccount_Fake(121),
    SelectWorld_Fake(122),
    SelectCharacter_Fake(123),
    CreateNewCharacter_Fake(124),
    CreateNewCharacter(125),
    CreateNewCharacterInCS(126),
    CreateNewCharacter_PremiumAdventurer(127),
    DeleteCharacter(128),
    ReservedDeleteCharacterConfirm(129),
    ReservedDeleteCharacterCancel(130),
    RenameCharacter(131),
    AliveAck_Fake(132),
    ExceptionLog(157), //v188 unsure
    PrivateServerPacket(134),
    ResetLoginStateOnCheckOTP(135),
    AlbaRequest(142),
    UpdateCharacterCard(143),
    CheckCenterAndGameAreConnected(144),
    ResponseToCheckAliveAck_Fake(145),
    CreateMapleAccount(146),
    AliveAck(152), // v188
    ResponseToCheckAliveAck(148),
    ClientDumpLog(149),
    CrcErrorLog(150),
    PerformanceInfoProvidedConsent(151),
    CheckHotfix(156), // v188
    ClientLoadingState(158), // v188
    UnknownSpam(154),
    UserLimitRequest(180), //temp bs
    WorldInfoRequest(162),
    SetSPW(166),
    ChangeSPWRequest(170),
    NMCORequest(171),
    EndSocket(173),
    CharacterBurning(537), //needed but officially not in this scope lol
    ;

    private int value;

    private ClientPacket(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }
}
