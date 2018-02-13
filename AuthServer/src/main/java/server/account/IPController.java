/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.account;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.data.Database;

/**
 *
 * @author Kaz Voeten
 */
@RestController
public class IPController {private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/ip")
    public boolean Logout(@RequestParam(value = "sToken", defaultValue = "null") String sToken,
            @RequestParam(value = "sIP", defaultValue = "null") String sIP) {
        return Database.SetIP(sToken, sIP);
    }
    
}
