package com.appcnd.rpc.governance.common.exception;

/**
 * @author nihao 2018/9/11
 */
public class RpcServiceNotFoundException extends Exception {
    public RpcServiceNotFoundException() {
    }

    public RpcServiceNotFoundException(String message) {
        super(message);
    }

    public RpcServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcServiceNotFoundException(Throwable cause) {
        super(cause);
    }

    public RpcServiceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
