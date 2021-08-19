package me.isaac.audit.protocol_v3.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common error code.
 */
@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements SQLErrorCode {

    CIRCUIT_BREAK_MODE(1000, "C1000", "Circuit break mode is ON."),

    SCALING_JOB_NOT_EXIST(1201, "C1201", "Scaling job %s does not exist."),

    SCALING_OPERATE_FAILED(1209, "C1209", "Scaling Operate Failed: [%s]"),

    TABLE_LOCK_WAIT_TIMEOUT(1301, "C1301", "The table %s of schema %s lock wait timeout of %s ms exceeded"),

    TABLE_LOCKED(1302, "C1302", "The table %s of schema %s is locked"),

    UNSUPPORTED_COMMAND(1998, "C1998", "Unsupported command: [%s]"),

    UNKNOWN_EXCEPTION(1999, "C1999", "Unknown exception: [%s]");

    private final int errorCode;

    private final String sqlState;

    private final String errorMessage;
}