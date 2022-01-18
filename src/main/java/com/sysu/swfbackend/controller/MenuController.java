package com.sysu.swfbackend.controller;

import com.alibaba.fastjson.JSON;
import com.sysu.swfbackend.bean.Menu;
import com.sysu.swfbackend.dao.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class MenuController {

    @Autowired
    MenuDao menuDao;

    @RequestMapping("/menus")
    public String getAllMenus() {
//        System.out.println("访问成功！");
        HashMap<String, Object> data = new HashMap<>();
        List<Menu> menus = menuDao.getMenus();
        if (menus != null) {
            data.put("menus", menus);
            data.put("flag", 200);
        } else {
            data.put("flag", 404);
        }
        String s = JSON.toJSONString(data);
        return s;
    }

    @RequestMapping("/tenant_menus")
    public String getTenantMenus() {
//        System.out.println("访问成功！");
        HashMap<String, Object> data = new HashMap<>();
        List<Menu> menus = menuDao.getTenantMenus();
        if (menus != null) {
            data.put("menus", menus);
            data.put("flag", 200);
        } else {
            data.put("flag", 404);
        }
        String s = JSON.toJSONString(data);
        return s;
    }
}

