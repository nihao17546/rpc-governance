package com.appcnd.rpc.governance.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author nihao 2018/9/14
 */
public class ServerBootstrap {
    public static void main(String[] args) throws Exception {
        new ClassPathXmlApplicationContext("server.xml");
    }
}
