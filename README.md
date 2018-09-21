## RPC-GOVERNANCE
基于Zookeeper，Netty的RPC
### 模块说明
- rpc-governance-common  
公共基础包
- rpc-governance-server  
服务端依赖
- rpc-governance-client  
客户端依赖
- rpc-governance-console  
管理控制台后端
- rpc-governance-console-web  
管理控制台前端
- rpc-governance-sample  
测试用例API包
- rpc-governance-sample-client  
客户端测试用例
- rpc-governance-sample-server  
服务端测试用例  
### 使用说明
- api接口
~~~Java
package com.appcnd.rpc.governance.sample.api;
/**
 * 抽象类API
 * @author nihao 2018/9/21
 */
public abstract class ApiServiceA {
    public abstract String test(String a);
}
~~~
~~~Java
package com.appcnd.rpc.governance.sample.api;
/**
 * 接口API
 * @author nihao 2018/9/21
 */
public interface ApiServiceB {
    String test(String a);
}
~~~
- 服务端引入依赖
~~~xml
<dependency>
    <groupId>com.appcnd</groupId>
    <artifactId>rpc-governance-server</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
~~~
- 服务端接口实现
~~~Java
package com.appcnd.rpc.governance.sample.server;

import com.appcnd.rpc.governance.sample.api.ApiServiceA;
import com.appcnd.rpc.governance.server.annotation.RpcService;

/**
 * 使用RpcService注解
 * value：api类
 * version：版本号，默认“”
 * @author nihao 2018/9/21
 */
@RpcService(value = ApiServiceA.class)
public class ApiServiceAServer extends ApiServiceA {
    @Override
    public String test(String a) {
        return "ApiServiceA：hello " + a;
    }
}
~~~
~~~Java
package com.appcnd.rpc.governance.sample.server;

import com.appcnd.rpc.governance.sample.api.ApiServiceB;
import com.appcnd.rpc.governance.server.annotation.RpcService;

/**
 * 使用RpcService注解
 * value：api类
 * version：版本号
 * @author nihao 2018/9/21
 */
@RpcService(value = ApiServiceB.class, version = "2.0")
public class ApiServiceBServer implements ApiServiceB {
    @Override
    public String test(String a) {
        return "ApiServiceB：hello " + a;
    }
}

~~~
- 服务端spring配置
~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rpc-server="http://www.appcnd.com/schema/rpc-server"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.appcnd.com/schema/rpc-server
       http://www.appcnd.com/schema/rpc-server.xsd">

    <!-- 扫描ApiServiceAServer和ApiServiceBServer -->
    <context:component-scan base-package="com.appcnd.rpc.governance.sample.server"/>

    <!--
    host：服务绑定ip，默认InetAddress.getLocalHost()
    port：服务端口
    zkAddress：zookeeper集群地址
    zkConnectionTimeout：zookeeper 连接超时时间
    zkSessionTimeout：zookeeper session超时时间
    -->
    <rpc-server:server id="server" port="9091"
                       zkAddress="127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181" 
                       zkConnectionTimeout="5000" zkSessionTimeout="5000"/>
</beans>
~~~
- 客户端引入依赖
~~~xml
<dependency>
    <groupId>com.appcnd</groupId>
    <artifactId>rpc-governance-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
~~~
- 客户端spring配置
~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rpc-client="http://www.appcnd.com/schema/rpc-client"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.appcnd.com/schema/rpc-client
       http://www.appcnd.com/schema/rpc-client.xsd">

    <!-- 
    rpc-client:name： api接口类型
    rpc-client:version：版本号 
    -->
    <rpc-client:client id="client" zkAddress="127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181" 
                       zkConnectionTimeout="5000" zkSessionTimeout="5000">
        <rpc-client:service>
            <rpc-client:name>com.appcnd.rpc.governance.sample.api.ApiServiceA</rpc-client:name>
        </rpc-client:service>
        <rpc-client:service>
            <rpc-client:name>com.appcnd.rpc.governance.sample.api.ApiServiceB</rpc-client:name>
            <rpc-client:version>2.0</rpc-client:version>
        </rpc-client:service>
    </rpc-client:client>
</beans>
~~~
- 客户端使用
~~~Java
package com.appcnd.rpc.governance.sample.client;

import com.appcnd.rpc.governance.client.annotation.RpcAutowired;
import com.appcnd.rpc.governance.sample.api.ApiServiceA;
import com.appcnd.rpc.governance.sample.api.ApiServiceB;
import org.springframework.stereotype.Service;

/**
 * 使用RpcAutowired注入
 * @author nihao 2018/9/21
 */
@Service
public class ClientSample {
    @RpcAutowired
    private ApiServiceA apiServiceA;
    @RpcAutowired
    private ApiServiceB apiServiceB;
    
    public void test() {
        System.out.println(apiServiceA.test("testA"));
        System.out.println(apiServiceB.test("testB"));
    }
}
~~~
### 管理控制台使用
![sa](https://raw.githubusercontent.com/nihao17546/file/master/rpc-console.png)
- 后端配置说明  
>1.rpc-governance-console/src/main/resources/application-rpc.yml  
~~~yaml
zookeeper:
  address: 127.0.0.1:2181 // zookeeper地址
  sessionTimeout: 5000
  connectionTimeout: 3000
password:
  root: root // 控制台root用户密码
  guest: guest // 控制台guest用户密码
~~~
>2.rpc-governance-console/src/main/resources/log4j.properties  
log4j日志配置
- 后端打包启动
~~~
mvn -Dmaven.test.skip=true package
nohup java -jar rpc-governance-console.jar &
~~~
- 前端配置说明  
>1.rpc-governance-console-web/src/components/Global.vue  
~~~html
<script>
  const httpUrlPrefix = '//127.0.0.1:8080'; // 后端服务接口前缀
  export default {
    httpUrlPrefix
  }
</script>
~~~
- 前端安装启动  
~~~
npm install
npm run dev
~~~
