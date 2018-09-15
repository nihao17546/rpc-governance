package com.appcnd.rpc.governance.common.bean;

/**
 * @author nihao 2018/9/11
 */
public class RpcProvider {
    private String serviceName;// 服务名
    private String host;// 服务地址
    private int port;// 服务端口
    private int weight;// 权重
    private int active;// 状态（1：有效，0：无效）

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "RpcProvider{" +
                "serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                ", active=" + active +
                '}';
    }

    public RpcProvider(RpcProviderBuilder builder) {
        this.serviceName = builder.serviceName;
        this.host = builder.host;
        this.port = builder.port;
        this.weight = builder.weight;
        this.active = builder.active;
    }

    public static class RpcProviderBuilder{
        private String serviceName;
        private String host;
        private int port;
        private int weight;
        private int active;

        public RpcProviderBuilder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public RpcProviderBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public RpcProviderBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public RpcProviderBuilder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public RpcProviderBuilder setActive(int active) {
            this.active = active;
            return this;
        }

        public RpcProvider build(){
            return new RpcProvider(this);
        }
    }
}
