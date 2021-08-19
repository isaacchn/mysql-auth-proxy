package me.isaac.audit.protocol_v3.base;

public interface MySQLPacket extends DbPacket<MySQLPayload> {
    /**
     * Get sequence ID.
     *
     * @return sequence ID
     */
    int getSequenceId();
}
