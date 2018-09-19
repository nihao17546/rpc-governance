package com.rpc.governance.console.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nihao 2018/9/19
 */
@ConfigurationProperties(prefix = "password")
public class PasswordConfig {
    private String root;
    private String guest;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }
}
