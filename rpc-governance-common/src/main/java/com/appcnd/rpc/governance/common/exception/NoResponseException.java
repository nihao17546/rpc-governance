package com.appcnd.rpc.governance.common.exception;

/**
 * @author nihao
 * @create 2018/9/15
 **/
public class NoResponseException extends RuntimeException {
    public NoResponseException() {
    }

    public NoResponseException(String message) {
        super(message);
    }

    public NoResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoResponseException(Throwable cause) {
        super(cause);
    }

    public NoResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
