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
package wz.etc;

import io.BinaryReader;
import io.BinaryWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import wz.WzFile;
import wz.WzObject;
import wz.common.WzDataTool;
import wz.io.WzMappedInputStream;

/**
 *
 * @author Novak
 */
public class ETCFactory {

    private final List<Card> CharacterCards = new ArrayList<>();
    private final List<Deck> CardDecks = new ArrayList<>();
    private ForbiddenName ForbiddenNames;
    private Curse CurseWords;
    private final byte[] key;
    private final int version;

    public ETCFactory(byte[] key, int version) {
        this.key = key;
        this.version = version;
    }

    public List<Card> getCharacterCards() {
        return this.CharacterCards;
    }

    public List<Deck> getCardDecks() {
        return this.CardDecks;
    }

    public ForbiddenName ForbiddenNames() {
        return this.ForbiddenNames;
    }

    public Curse CurseWords() {
        return this.CurseWords;
    }

    public boolean isLegalName(String id) {
        return !CurseWords.contains(id) && !ForbiddenNames.contains(id);
    }

    public void loadBinary(String wzFolder) {
        BinaryReader input = new BinaryReader(wzFolder + "Etc.bin");

        //Parse data in following order
        parseCards(input);
        parseNames(input);
        parseCurses(input);

    }

    public void dumpBinary(String wzFolder, byte[] key) {
        try {
            BinaryWriter writer = new BinaryWriter(wzFolder + "Etc.bin");
            WzMappedInputStream in = new WzMappedInputStream(Paths.get("wz", "Etc.wz"));
            WzFile Etcwz = new WzFile("Etc.wz", (short) version);
            in.setKey(key);
            in.setHash(version);
            Etcwz.parse(in);

            //Dump data in following order
            dumpCards(Etcwz, writer);
            dumpNames(Etcwz, writer);
            dumpCurses(Etcwz, writer);

            try {
                writer.WriteFile();
            } catch (Exception ex) {
                System.out.println("Couldn't finalize bin file.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void dumpNames(WzFile Etcwz, BinaryWriter writer) {
        WzObject<?, ?> forbiddenNames = Etcwz.getChild("ForbiddenName.img");

        writer.WriteShort((int) forbiddenNames.getChildren().size());
        for (WzObject<?, ?> name : forbiddenNames) {
            writer.WriteString(WzDataTool.getString(forbiddenNames, name.getName(), "admin"));
        }
    }

    private void dumpCurses(WzFile Etcwz, BinaryWriter writer) {
        WzObject<?, ?> curses = Etcwz.getChild("Curse.img").getChild("BlackList");

        writer.WriteShort((int) curses.getChildren().size());
        for (WzObject<?, ?> name : curses) {
            writer.WriteString(WzDataTool.getString(curses, name.getName(), "fuck").toLowerCase().replace("[s]", ""));
        }
    }

    private void dumpCards(WzFile Etcwz, BinaryWriter writer) {
        //Get character cards
        WzObject<?, ?> cardDir = Etcwz.getChild("CharacterCard.img");
        WzObject<?, ?> cards = cardDir.getChild("Card");
        WzObject<?, ?> decks = cardDir.getChild("Deck");

        //cards
        writer.WriteShort((int) cards.getChildren().size());
        for (WzObject<?, ?> card : cards) {
            writer.WriteShort((int) Integer.parseInt(card.getName())); //job id
            writer.WriteInt(WzDataTool.getInteger(card, "skillID", 0)); //card skill
        }

        //decks
        writer.WriteShort(decks.getChildren().size());
        for (WzObject<?, ?> deck : decks) {
            writer.WriteShort(Integer.parseInt(deck.getName())); //deck id

            WzObject<?, ?> requiredCards = deck.getChild("reqCardID");
            if (requiredCards != null && requiredCards.getChildren() != null) {
                writer.WriteShort(requiredCards.getChildren().size());
                for (WzObject<?, ?> card : requiredCards) {
                    writer.WriteInt(WzDataTool.getInteger(requiredCards, card.getName(), 0));
                }
            } else {
                writer.WriteShort(0);
            }

            writer.WriteInt(WzDataTool.getInteger(deck, "skillID", 0));
            writer.WriteShort(WzDataTool.getShort(deck, "UniqueEffect", (short) 0));
        }
    }

    public void parseCards(BinaryReader reader) {
        //read and load cards
        short numcards = reader.ReadShort();
        for (int i = 0; i < numcards; i++) {
            this.CharacterCards.add(new Card(reader.ReadShort(), reader.ReadInt()));
        }

        //read and load decks
        short numDecks = reader.ReadShort();
        for (int i = 0; i < numDecks; i++) {
            short id = reader.ReadShort();
            int[] cards = new int[reader.ReadShort()];
            for (int j = 0; j < cards.length; j++) {
                cards[j] = reader.ReadInt();
            }
            int skillid = reader.ReadInt();
            short effect = reader.ReadShort();
            this.CardDecks.add(new Deck(id, effect, skillid, cards));
        }
    }

    public void parseNames(BinaryReader reader) {
        int size = reader.ReadShort();
        this.ForbiddenNames = new ForbiddenName(size);
        for (int i = 0; i < size; i++) {
            ForbiddenNames.add(reader.ReadString(), i);
        }
    }

    public void parseCurses(BinaryReader reader) {
        int size = reader.ReadShort();
        this.CurseWords = new Curse(size);
        for (int i = 0; i < size; i++) {
            CurseWords.add(reader.ReadString(), i);
        }
    }
}
