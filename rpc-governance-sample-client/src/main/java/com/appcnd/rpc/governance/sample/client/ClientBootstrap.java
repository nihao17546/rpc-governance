package com.appcnd.rpc.governance.sample.client;

import com.appcnd.rpc.governance.sample.api.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author nihao 2018/9/14
 */
public class ClientBootstrap {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("client.xml");
        UserService userService = context.getBean(UserService.class);
        String s = userService.hello("988989");
        System.out.println(s);
        int c = userService.add(Integer.MAX_VALUE,99);
        System.out.println(c);
    }
}
