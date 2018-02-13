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
package server.login;

import server.account.Account;
import server.data.Database;
import server.data.Email;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kaz_v
 */
@RestController
public class LoginController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/login")
    public LoginResponse login(
            @RequestParam(value = "name", defaultValue = "null") String sName,
            @RequestParam(value = "password", defaultValue = "password") String sPassword) {

        LoginResponseCode nCode = Database.CheckPassword(sName, sPassword);
        if (nCode == LoginResponseCode.SUCCESS) {
            String sToken = Database.GenerateToken(Database.GetAccountByName(sName));
            Database.SetState(sToken, (byte) 1);
            return new LoginResponse(sName, sToken, nCode);
        }

        if (nCode == LoginResponseCode.UNVERIFIED) {
            Account pAccount = Database.GetAccountByName(sName);
            if (!Database.GetAuthCode(pAccount.getsAccountName()).equals("")) {
                return new LoginResponse("Please use the verification code sent to the account's "
                        + "e-mail address to verify the account.", "-1", nCode);
            } else {
                Database.CreateAuthCode(pAccount.getsEmail());
                try {
                    Email.sendAuthMail(pAccount.getsEmail(), Database.GetAuthCode(pAccount.getsEmail()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return new LoginResponse("New authentication code sent to account email.", "-1", nCode);
            }
        }

        return new LoginResponse("Login failed.", "null", nCode);
    }
}
