package me.isaac.audit.protocol_v3.packet.handshake;

import lombok.Getter;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class MySQLRandomGenerator {
    @Getter
    private static final MySQLRandomGenerator INSTANCE = new MySQLRandomGenerator();

    private static final byte[] SEED = {
            'a', 'b', 'e', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',};

    private final Random random = ThreadLocalRandom.current();

    /**
     * Generate random bytes.
     *
     * @param length length for generated bytes.
     * @return generated bytes
     */
    public byte[] generateRandomBytes(final int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = SEED[random.nextInt(SEED.length)];
        }
        return result;
    }
}
