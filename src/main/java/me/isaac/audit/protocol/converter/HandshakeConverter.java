package me.isaac.audit.protocol.converter;

import me.isaac.audit.protocol.pack.HandshakePayload;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class HandshakeConverter implements IConverter<HandshakePayload> {
    private int cursor = 0;
    private byte[] bytes;

    @Override
    public HandshakePayload get(byte[] bytes) {
        this.bytes = bytes;
        HandshakePayload payload = new HandshakePayload();
        payload.setProtocolVersion(readUB1());
        payload.setServerVersion(readStringWithNull());
        payload.setConnectionId(readUB4());
        payload.setAuthDataPart1(readString(8));
        payload.setFiller1(readUB1());
        payload.setCapabilityLow(readUB2());
        payload.setCharset(readUB1());
        payload.setServerStatus(readUB2());
        payload.setCapabilityHigh(readUB2());
        payload.setAuthPluginLen(readUB1());
        payload.setFiller2(readUB1());
        move(10);
        payload.setAuthDataPart2(readStringWithNull());
        payload.setAuthPluginName(readString(payload.getAuthPluginLen()));

        return payload;
    }

    private void move(int length) {
        cursor += length;
    }

    private int readUB1() {
        int i = bytes[cursor++] & 0xff;
        return i;
    }

    private int readUB2() {
        int i = bytes[cursor++] & 0xff;
        i |= (bytes[cursor++]) << 8;
        return i;
    }

    private long readUB4() {
        long l = (long) (bytes[cursor++] & 0xff);
        l |= (long) (bytes[cursor++] & 0xff) << 8;
        l |= (long) (bytes[cursor++] & 0xff) << 16;
        l |= (long) (bytes[cursor++] & 0xff) << 24;
        return l;
    }

    /**
     * 读取字符串直至读取到NULL
     */
    private String readStringWithNull() {
        List<Byte> list = new LinkedList<>();
        byte b;
        while ((b = bytes[cursor]) != 0) {
            cursor++;
            list.add(b);
        }
        cursor++;
        return list.stream().map(aByte -> (char) (aByte & 0xff)).map(Object::toString).collect(Collectors.joining());
    }

    /**
     * 读取定长字符串
     *
     * @param length
     * @return
     */
    private String readString(int length) {
        List<Byte> list = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            list.add(bytes[cursor]);
            cursor++;
        }
        return list.stream().map(aByte -> (char) (aByte & 0xff)).map(Object::toString).collect(Collectors.joining());
    }
}
