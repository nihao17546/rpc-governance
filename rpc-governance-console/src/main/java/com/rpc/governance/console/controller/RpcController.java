package com.rpc.governance.console.controller;

import com.rpc.governance.console.config.PasswordConfig;
import com.rpc.governance.console.model.ProviderModel;
import com.rpc.governance.console.model.ServiceModel;
import com.rpc.governance.console.service.RpcConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nihao 2018/9/18
 */
@RestController
public class RpcController {

    @Autowired
    private PasswordConfig passwordConfig;
    @Autowired
    private RpcConsoleService rpcConsoleService;

    @RequestMapping("/list")
    public List<ServiceModel> list() {
        return rpcConsoleService.list();
    }

    @RequestMapping("/changeWeight")
    public ProviderModel changeWeight(@RequestParam String serviceName,
                                      @RequestParam  String providerName,@RequestParam  Integer weight) {
        return rpcConsoleService.changeWeight(serviceName, providerName, weight);
    }

    @RequestMapping("/changeActive")
    public ProviderModel changeActive(@RequestParam String serviceName,
                                      @RequestParam  String providerName,@RequestParam  Boolean active) {
        return rpcConsoleService.changeActive(serviceName, providerName, active);
    }

    @RequestMapping("/login")
    public Map login(@RequestParam String username, @RequestParam String password,
                     HttpServletRequest request) {
        Map<String,Object> result = new HashMap<>();
        result.put("success", false);
        if ("root".equals(username) && password.equals(passwordConfig.getRoot())) {
            request.getSession().setAttribute("currentUser", "root");
            result.put("success", true);
        }
        else if("guest".equals(username) && password.equals(passwordConfig.getGuest())) {
            request.getSession().setAttribute("currentUser", "guest");
            result.put("success", true);
        }
        return result;
    }

    @RequestMapping("/logout")
    public Map logout(HttpServletRequest request) {
        request.getSession().removeAttribute("currentUser");
        Map<String,Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @RequestMapping("/check")
    public Map check(HttpServletRequest request) {
        Map<String,Object> result = new HashMap<>();
        result.put("success", false);
        Object obj = request.getSession().getAttribute("currentUser");
        if (obj != null) {
            result.put("success", true);
            result.put("username", obj);
        }
        return result;
    }
}
