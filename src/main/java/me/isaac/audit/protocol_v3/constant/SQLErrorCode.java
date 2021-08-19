package me.isaac.audit.protocol_v3.constant;

public interface SQLErrorCode {
    /**
     * Get error code.
     *
     * @return error code
     */
    int getErrorCode();

    /**
     * Get SQL state.
     *
     * @return SQL state
     */
    String getSqlState();

    /**
     * Get error message.
     *
     * @return error message
     */
    String getErrorMessage();
}
