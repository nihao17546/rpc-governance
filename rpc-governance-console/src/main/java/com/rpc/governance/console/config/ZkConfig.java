package com.rpc.governance.console.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nihao 2018/9/18
 */
@ConfigurationProperties(prefix = "zookeeper")
public class ZkConfig {
    private String address;
    private Integer sessionTimeout;
    private Integer connectionTimeout;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
