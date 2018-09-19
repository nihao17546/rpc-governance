package com.rpc.governance.console.model;

import java.util.List;

/**
 * @author nihao 2018/9/18
 */
public class ServiceModel {
    private String name;
    private List<ProviderModel> providers;
    private Long createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProviderModel> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderModel> providers) {
        this.providers = providers;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
