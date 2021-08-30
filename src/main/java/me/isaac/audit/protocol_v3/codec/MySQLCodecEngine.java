package me.isaac.audit.protocol_v3.codec;

public class MySQLCodecEngine {
    private static final int PAYLOAD_LENGTH = 3;

    private static final int SEQUENCE_LENGTH = 1;

    public boolean isValidHeader(int readableBytes) {
        return readableBytes >= PAYLOAD_LENGTH + SEQUENCE_LENGTH;
    }
}
