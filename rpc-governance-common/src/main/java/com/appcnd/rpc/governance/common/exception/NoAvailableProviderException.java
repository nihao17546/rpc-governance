package com.appcnd.rpc.governance.common.exception;

/**
 * @author nihao
 * @create 2018/9/15
 **/
public class NoAvailableProviderException extends RuntimeException {
    public NoAvailableProviderException() {
    }

    public NoAvailableProviderException(String message) {
        super(message);
    }

    public NoAvailableProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAvailableProviderException(Throwable cause) {
        super(cause);
    }

    public NoAvailableProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
