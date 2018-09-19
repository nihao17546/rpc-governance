package com.rpc.governance.console;

import com.rpc.governance.console.config.PasswordConfig;
import com.rpc.governance.console.config.ZkConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ZkConfig.class, PasswordConfig.class})
public class RpcGovernanceConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcGovernanceConsoleApplication.class, args);
    }
}
