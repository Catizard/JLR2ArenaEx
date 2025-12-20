package io.github.catizard.jlr2arenaex.network;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import java.util.Objects;

public class Score implements Cloneable {
    private int poor;
    private int bad;
    private int good;
    private int great;
    private int pGreat;
    private int maxCombo;
    private int score;
    private int currentNotes;

    public Score() {

    }

    public Score(Value value) {
        ArrayValue arr = value.asArrayValue();
        this.poor = arr.get(0).asIntegerValue().toInt();
        this.bad = arr.get(1).asIntegerValue().toInt();
        this.good = arr.get(2).asIntegerValue().toInt();
        this.great = arr.get(3).asIntegerValue().toInt();
        this.pGreat = arr.get(4).asIntegerValue().toInt();
        this.maxCombo = arr.get(5).asIntegerValue().toInt();
        this.score = arr.get(6).asIntegerValue().toInt();
        this.currentNotes = arr.get(7).asIntegerValue().toInt();
    }

    public byte[] pack() {
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            packer.packArrayHeader(8);
            packer.packInt(this.poor);
            packer.packInt(this.bad);
            packer.packInt(this.good);
            packer.packInt(this.great);
            packer.packInt(this.pGreat);
            packer.packInt(this.maxCombo);
            packer.packInt(this.score);
            packer.packInt(this.currentNotes);
            packer.close();
            return packer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int getPoor() {
        return poor;
    }

    public void setPoor(int poor) {
        this.poor = poor;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getGreat() {
        return great;
    }

    public void setGreat(int great) {
        this.great = great;
    }

    public int getpGreat() {
        return pGreat;
    }

    public void setpGreat(int pGreat) {
        this.pGreat = pGreat;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCurrentNotes() {
        return currentNotes;
    }

    public void setCurrentNotes(int currentNotes) {
        this.currentNotes = currentNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Score score1 = (Score) o;
        return poor == score1.poor && bad == score1.bad && good == score1.good && great == score1.great && pGreat == score1.pGreat && maxCombo == score1.maxCombo && score == score1.score && currentNotes == score1.currentNotes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(poor, bad, good, great, pGreat, maxCombo, score, currentNotes);
    }

	@Override
	public Score clone() {
		try {
			Score clone = (Score) super.clone();
			// TODO: copy mutable state here, so the clone can't change the internals of the original
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
