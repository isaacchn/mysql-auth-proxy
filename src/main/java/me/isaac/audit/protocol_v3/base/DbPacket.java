package me.isaac.audit.protocol_v3.base;

public interface DbPacket<T extends DbPayload> {
    /**
     * Write packet to byte buffer.
     *
     * @param payload packet payload to be written
     */
    void write(T payload);
}
