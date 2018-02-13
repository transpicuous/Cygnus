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
package server.blocklist;

import server.data.Database;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kaz_v
 */
@RestController
public class AddBlockController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/block")
    public boolean GetBlockList(
            @RequestParam(value = "nType", defaultValue = "0") int nType,
            @RequestParam(value = "sValue", defaultValue = "0.0.0.0") String sValue,
            @RequestParam(value = "nDuration", defaultValue = "0") long nDuration
            ) {
        return Database.AddBlock(nType, sValue, nDuration);
    }
}
