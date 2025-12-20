package io.github.catizard.jlr2arenaex.network;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import java.io.IOException;

/**
 * Server -> Client message
 */
public class Message {
    private String message;
    private Address player;
    private boolean systemMessage;

    public Message() {

    }

    public Message(String message, Address player, boolean systemMessage) {
        this.message = message;
        this.player = player;
        this.systemMessage = systemMessage;
    }

    public Message(Value value) {
        ArrayValue arr = value.asArrayValue();
        this.message = arr.get(0).asStringValue().asString();
        this.player = new Address(arr.get(1));
        this.systemMessage = arr.get(2).asBooleanValue().getBoolean();
    }

    public byte[] pack() {
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            packer.packArrayHeader(3);
            packer.packString(this.message);
            packer.writePayload(this.player.pack());
            packer.packBoolean(systemMessage);
            packer.close();
            return packer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Address getPlayer() {
        return player;
    }

    public void setPlayer(Address player) {
        this.player = player;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(boolean systemMessage) {
        this.systemMessage = systemMessage;
    }
}
