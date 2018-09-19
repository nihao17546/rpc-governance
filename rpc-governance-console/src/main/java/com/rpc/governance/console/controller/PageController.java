package com.rpc.governance.console.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author nihao 2018/9/18
 */
@Controller
public class PageController {

    @RequestMapping("/index.html")
    public String index() {
        return "index";
    }

}
