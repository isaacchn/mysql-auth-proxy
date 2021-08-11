package me.isaac.audit.protocol.converter;

import me.isaac.audit.protocol.pack.Payload;

public interface IConverter<T extends Payload> {
    T get(byte[] bytes);
}
