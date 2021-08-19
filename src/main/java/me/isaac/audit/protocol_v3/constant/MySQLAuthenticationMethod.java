package me.isaac.audit.protocol_v3.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MySQLAuthenticationMethod {
    OLD_PASSWORD_AUTHENTICATION("mysql_old_password"),

    SECURE_PASSWORD_AUTHENTICATION("mysql_native_password"),

    CLEAR_TEXT_AUTHENTICATION("mysql_clear_password"),

    WINDOWS_NATIVE_AUTHENTICATION("authentication_windows_client"),

    SHA256("sha256_password");

    private final String methodName;
}
