package com.appcnd.rpc.governance.client.balance;

/**
 * @author nihao 2018/9/14
 */
public final class RpcBalanceManager {
    private RpcBalanceManager() {}

    public static final RpcBalance create(String balance) {
        if (RandomRpcBalance.name.equals(balance)) {
            return new RandomRpcBalance();
        }
        throw new IllegalArgumentException("balance type does not support " + balance);
    }
}
