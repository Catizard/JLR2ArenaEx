package io.github.catizard.jlr2arenaex.network;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeerList implements EqualsWithoutRandomPort<PeerList> {
    private Map<Address, Peer> list = new HashMap<>();
    private Address host;

    public PeerList() {

    }

    public PeerList(Map<Address, Peer> list, Address host) {
        this.list = list;
        this.host = host;
    }

    public PeerList(Value value) {
        ArrayValue arrayValue = value.asArrayValue();
        Value listValue = arrayValue.get(0);
        Map<Value, Value> map = listValue.asMapValue().map();
        map.forEach((k, v) -> {
            // k -> Address || v -> Peer
            Address address = new Address(k);
            Peer peer = new Peer(v);
            this.list.put(address, peer);
        });
        Value hostValue = arrayValue.get(1);
        this.host = new Address(hostValue);
    }

    public byte[] pack() {
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            packer.packArrayHeader(2);
            packer.packMapHeader(this.list.size());
            for (Map.Entry<Address, Peer> entry : this.list.entrySet()) {
                packer.writePayload(entry.getKey().pack());
                packer.writePayload(entry.getValue().pack());
            }
            packer.writePayload(host.pack());
            packer.close();
            return packer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Map<Address, Peer> getList() {
        return list;
    }

    public void setList(Map<Address, Peer> list) {
        this.list = list;
    }

    public Address getHost() {
        return host;
    }

    public void setHost(Address host) {
        this.host = host;
    }

    @Override
    public boolean equalsWithoutRandomPort(PeerList obj) {
        if (this.list.size() != obj.list.size()) {
            return false;
        }
        if (!this.host.equalsWithoutRandomPort(obj.host)) {
            return false;
        }
        for (Map.Entry<Address, Peer> entry : this.list.entrySet()) {
            boolean matched = false;
            for (Map.Entry<Address, Peer> otherEntry : obj.list.entrySet()) {
                if (entry.getValue().equals(otherEntry.getValue())) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }
}
