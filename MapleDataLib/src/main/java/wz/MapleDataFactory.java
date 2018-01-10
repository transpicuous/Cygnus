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
package wz;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import wz.etc.ETCFactory;
import wz.common.WzTool;
import wz.common.WzVersion;
import wz.io.WzMappedInputStream;
import wz.item.EquipFactory;
import wz.item.ItemFactory;
import wz.util.AES;

/**
 *
 * @author Kaz Voeten
 */
public class MapleDataFactory {

    private static final AES aes = new AES();
    private static byte[] aKey
            = WzTool.generateKey(WzVersion.BMS);
    private final int version = 176;
    Scanner scan = new Scanner(System.in);

    private ETCFactory etcdata;
    private EquipFactory equipdata;
    private ItemFactory itemdata;

    public MapleDataFactory() {
        this.etcdata = new ETCFactory(aKey, version);
        this.equipdata = new EquipFactory(aKey, version);
        this.itemdata = new ItemFactory(aKey, version);
    }

    public ETCFactory GetETC() {
        return etcdata;
    }

    public EquipFactory GetEquip() {
        return equipdata;
    }

    public ItemFactory GetItem() {
        return itemdata;
    }

    public void loadData(int nServerType) {
        if (nServerType == 0) { //Login (just some data for a few checks)
            Long time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Etc.bin data.");
            etcdata.loadBinary(System.getProperty("wz"));
            System.out.println("[Info] Parsed Etc.bin data in " + (System.currentTimeMillis() - time) + "ms.");
            return;
        }

        if (nServerType == 1) { //Center (bit more than login)
            Long time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Etc.bin data.");
            etcdata.loadBinary(System.getProperty("wz"));
            System.out.println("[Info] Parsed Etc.bin data in " + (System.currentTimeMillis() - time) + "ms.");

            time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Character.bin data.");
            equipdata.loadBinaryEquips(System.getProperty("wz"));
            System.out.println("[Info] Parsed " + equipdata.getEquips().size() + " Character.bin data entries in " + (System.currentTimeMillis() - time) + "ms.");

            time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Item.bin data.");
            itemdata.loadBinaryItems(System.getProperty("wz"));
            System.out.println("[Info] Parsed " + itemdata.getItems().size() + " Item.bin data entries in " + (System.currentTimeMillis() - time) + "ms.");
            return;
        }
        
        if (nServerType == 3) { //Game (all data)
            Long time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Etc.bin data.");
            etcdata.loadBinary(System.getProperty("wz"));
            System.out.println("[Info] Parsed Etc.bin data in " + (System.currentTimeMillis() - time) + "ms.");

            time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Character.bin data.");
            equipdata.loadBinaryEquips(System.getProperty("wz"));
            System.out.println("[Info] Parsed " + equipdata.getEquips().size() + " Character.bin data entries in " + (System.currentTimeMillis() - time) + "ms.");

            time = System.currentTimeMillis();
            System.out.println("[Info] Parsing binary Item.bin data.");
            itemdata.loadBinaryItems(System.getProperty("wz"));
            System.out.println("[Info] Parsed " + itemdata.getItems().size() + " Item.bin data entries in " + (System.currentTimeMillis() - time) + "ms.");
            return;
        }

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Please type in 1 and press enter to dump binary data from WZ. Press 2 to parse binary data from binary files. Press 3 to validate Data.wz");
        MapleDataFactory factory = (new MapleDataFactory());
        int mode = factory.scan.nextInt();

        if (mode == 1) {
            Long time = System.currentTimeMillis();
            System.out.println("[Info] Dumping binary Etc.wz data.");
            factory.etcdata.dumpBinary(System.getProperty("wz"), aKey);
            System.out.println("[Info] Dumped Etc.wz data in " + (System.currentTimeMillis() - time) + "ms.");

            time = System.currentTimeMillis();
            System.out.println("[Info] Dumping binary Character.wz data.");
            factory.equipdata.dumpBinaryEquips(System.getProperty("wz"), aKey);
            System.out.println("[Info] Dumped Character.wz data in " + (System.currentTimeMillis() - time) + "ms.");

            time = System.currentTimeMillis();
            System.out.println("[Info] Dumping binary Item.wz data.");
            factory.itemdata.dumpBinaryItems(System.getProperty("wz"), aKey);
            System.out.println("[Info] Dumped Item.wz data in " + (System.currentTimeMillis() - time) + "ms.");
        } else if (mode == 2) {
            factory.loadData(2);
        } else if (mode == 3) {
            WzMappedInputStream in = new WzMappedInputStream(Paths.get("wz", "Data.wz"));
            in.setKey(aKey);

            WzImage img = new WzImage("Data.wz", in);
            img.forceUnknownHash();
            in.setHash(WzFile.getVersionHash(175));
            img.parse(in);

            System.out.println("Node Count: " + img.getChildren().size());

            System.out.println("Reading nodes:");
            img.getChildren().forEach((name, object) -> {
                System.out.println("-------------------------------------------------------------");
                System.out.println(name);
                System.out.println(object.getFullPath());
                object.getChildren().forEach((node, property) -> {
                    System.out.println(node);
                    System.out.println(property.getFullPath());
                });
                System.out.println("End of node.");
            });
            System.out.println("End of nodes.");
        }

    }

}
