package com.appcnd.rpc.governance.client.balance;

import com.appcnd.rpc.governance.common.bean.RpcProvider;

import java.util.List;
import java.util.Random;

/**
 * 按权重负载均衡
 * @author nihao 2018/9/13
 */
public class RandomRpcBalance implements RpcBalance {
    public static final String name = "random";

    private final Random random = new Random();

    @Override
    public RpcProvider select(List<RpcProvider> rpcProviders) {
        int length = rpcProviders.size();
        int totalWeight = 0;// 总权重
        boolean sameWeight = true;// 权重是否一样
        for (int i = 0; i < length; i ++) {
            RpcProvider rpcProvider = rpcProviders.get(i);
            totalWeight += rpcProvider.getWeight();// 累积权重
            // 判断所有权重是否一样
            if (i > 0 && sameWeight && rpcProvider.getWeight() != rpcProviders.get(i - 1).getWeight()) {
                sameWeight = false;
            }
        }
        // 如果权重不相同且总权重大于0，则按总权重随机
        if (totalWeight > 0 && !sameWeight) {
            int offset = random.nextInt(totalWeight);
            // 确定随机值落在哪个片断上
            for (RpcProvider rpcProvider : rpcProviders) {
                offset -= rpcProvider.getWeight();
                if (offset < 0) {
                    return rpcProvider;
                }
            }
        }
        return rpcProviders.get(random.nextInt(length));
    }

    RandomRpcBalance() {}
}
