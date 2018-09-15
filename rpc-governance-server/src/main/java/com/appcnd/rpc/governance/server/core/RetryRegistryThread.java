package com.appcnd.rpc.governance.server.core;

import com.appcnd.rpc.governance.common.bean.RpcProvider;
import com.appcnd.rpc.governance.common.utils.RpcServiceProviderUtil;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author nihao 2018/9/12
 */
public class RetryRegistryThread implements Callable<Boolean> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ZkClient zkClient;
    private final CountDownLatch countDownLatch;
    private final String providerPath;
    private final RpcProvider rpcProvider;

    protected RetryRegistryThread(final ZkClient zkClient, final String providerPath, final RpcProvider rpcProvider,
                                  CountDownLatch countDownLatch){
        this.zkClient = zkClient;
        this.providerPath = providerPath;
        this.rpcProvider = rpcProvider;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            return handler();
        } catch (Exception e) {
            throw e;
        } finally {
            countDownLatch.countDown();
        }
    }

    private boolean handler() throws InterruptedException {
        for (int tryNumber = 0;;tryNumber ++) {
            if (!zkClient.exists(providerPath)) {
                logger.debug("retry create provider node: {}.", providerPath);
                zkClient.createEphemeral(providerPath, RpcServiceProviderUtil.getZkData(rpcProvider));
                return true;
            }
            if (tryNumber < 10) {
                logger.debug(providerPath + " exists,check again later.");
                Thread.sleep(1000);
            }
            else {
                break;
            }
        }
        logger.debug(providerPath + " did not drop.");
        return false;
    }
}
