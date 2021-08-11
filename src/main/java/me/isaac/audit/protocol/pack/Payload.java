package me.isaac.audit.protocol.pack;

import lombok.Data;

@Data
public abstract class Payload {
    private PayloadType type;
    private byte[] data;
}
