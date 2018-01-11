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

/**
 *
 * @author Kaz Voeten
 */
public class CardDeck {

    private final short id;
    private final short effect;
    private final int skillid;
    private final int[] cards;

    public CardDeck(short id, short effect, int skillid, int[] cards) {
        this.id = id;
        this.effect = effect;
        this.skillid = skillid;
        this.cards = cards;
    }

    public short getId() {
        return this.id;
    }

    public short getEffect() {
        return this.effect;
    }

    public int getSkill() {
        return this.skillid;
    }

    public int[] getCards() {
        return this.cards;
    }
}
