package com.chr.mybatisplus.controller;


import com.chr.mybatisplus.entity.AuthUserCheck;
import com.chr.mybatisplus.entity.UserList;
import com.chr.mybatisplus.utils.JsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author RAY
 * @since 2019-12-13
 */
@Controller
@RequestMapping("/renter")
public class RenterController {

    /**
     * 测试userlist接口
     * @param args
     */
    public static void main(String[] args) {
        UserList userList = new UserList();
        List<AuthUserCheck> list = new ArrayList<>();
        for (int i=0;i<1;i++) {
            AuthUserCheck user = new AuthUserCheck();
            user.setUsername("test");
            user.setPassword("1111");
            user.setClientId("tddd");
            user.setQq("4555");
            user.setLoginRest(10);
            list.add(user);
        }
        userList.setBatchNo("2020_2");
        userList.setAuthUsers(list);
        String json = JsonUtils.objectToJson(userList);
        try {
            String result = HttpRequestUtil.getResult("http://127.0.0.1:8080/userList", json);
            System.out.println(result);
            list.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
