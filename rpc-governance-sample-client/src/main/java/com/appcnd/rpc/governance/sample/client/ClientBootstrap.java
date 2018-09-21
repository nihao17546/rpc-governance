package com.appcnd.rpc.governance.sample.client;

import com.appcnd.rpc.governance.sample.api.ApiServiceA;
import com.appcnd.rpc.governance.sample.api.ApiServiceB;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author nihao 2018/9/14
 */
public class ClientBootstrap {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("client.xml");
        ApiServiceA apiServiceA = context.getBean(ApiServiceA.class);
        ApiServiceB apiServiceB = context.getBean(ApiServiceB.class);
        System.out.println(apiServiceA.test("test111"));
        System.out.println(apiServiceB.test("test222"));
    }
}
