package com.hwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 黄雯婷
 * @version 1.0
 * @description: TODO
 * @date 2020-08-17 10:15:19
 **/

@Controller
@RequestMapping("/api/demo")
public class demoController {
    @RequestMapping("/test")
    @ResponseBody
    public String Test() {
        System.out.println("----------------test");
        return "test==============test ";
    }
}
