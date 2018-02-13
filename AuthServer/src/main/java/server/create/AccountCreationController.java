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
package server.create;

import server.data.Database;
import server.data.Email;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.CharEncoding;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kaz_v
 */
@RestController
public class AccountCreationController {

    private final AtomicLong counter = new AtomicLong();

    /**
     * Controls Account creation API requests.
     *
     * @param email Supplied email parameter.
     * @param name Supplied name parameter.
     * @param password Supplied password parameter.
     * @param birthday Supplied birthday parameter.
     * @param gender Supplied gender parameter.
     * @param request Supplied request parameter.
     *
     * @return new Account with supplied parameters as configuration.
     */
    @RequestMapping("/create")
    public AccountCreationResponse create(
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "password", defaultValue = "") String password,
            @RequestParam(value = "birthday", defaultValue = "") String birthday,
            @RequestParam(value = "gender", defaultValue = "") String gender,
            HttpServletRequest request) {

        try {
            (new InternetAddress(email)).validate();
        } catch (AddressException ex) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Invalid e-mail address.");
        }

        if (name.length() < 5 || name.length() > 13) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Username has to be at least 5 and maximum 13 characters long.");
        }

        if (password.length() < 5 || password.length() > 13) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Password has to be at least 5 and maximum 13 characters long.");
        }

        if (!(gender.equals("0") || gender.equals("1"))) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Gender has to be either 0 (male) or 1 (female).");
        }

        if (!Charset.forName(CharEncoding.UTF_8).newEncoder().canEncode(name)) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Username contains invalid characters. Only utf8 characters are supported.");
        }

        if (!Charset.forName(CharEncoding.UTF_8).newEncoder().canEncode(password)) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Password contains invalid characters. Only utf8 characters are supported.");
        }

        try {
            int day = Integer.parseInt(birthday.substring(0, 2));
            int month = Integer.parseInt(birthday.substring(2, 4));
            int year = Integer.parseInt(birthday.substring(4, 8));
            if (day > 31 || day < 1
                    || month > 12 || month < 1
                    || year > 9999 || year < 1900) {
                return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                        "Invalid birthday. Please use format ddmmyyyy");
            }
        } catch (NumberFormatException ex) {
            return new AccountCreationResponse(CreationResponseCode.FAILED.GetValue(),
                    "Invalid birthday. Please use format ddmmyyyy");
        }

        CreationResponseCode nResponse = Database.CheckDuplicatedID(name, email);
        switch (nResponse) {
            case FAILED:
                return new AccountCreationResponse(nResponse.GetValue(),
                        "The provided name or email is already in use.");
            case EXISTS_UNVERIFIED:
                return new AccountCreationResponse(nResponse.GetValue(),
                        "This account already exists but hasn't been verified yet. Please login to this account to initiate "
                        + "the verification process.");
            case SUCCESS:
                CreationResponseCode returned = Database.CreateAccount(email, name, password, birthday, gender);
                String message = "Account created succesfully! Please use to code sent to your e-mail adress to verify the account.";
                if (returned == CreationResponseCode.FAILED) {
                    message = "Failed";
                } else {
                    try {
                        Database.CreateAuthCode(email);
                        Email.sendAuthMail(email, Database.GetAuthCode(name));
                    } catch (MessagingException ex) {
                        Logger.getLogger(AccountCreationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return new AccountCreationResponse(returned.GetValue(), message);
            default:
                return new AccountCreationResponse(nResponse.GetValue(),
                        "Err.");
        }
    }
}
