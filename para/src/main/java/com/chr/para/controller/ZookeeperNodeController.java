package com.chr.para.controller;


import com.chr.para.entity.AuthClient;
import com.chr.para.service.IAuthClientService;
import com.chr.para.service.IZookeeperNodeService;
import com.chr.para.utils.RedisUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author RAY
 * @since 2020-01-08
 */
@Controller
//@RequestMapping("/zookeeperNode")
public class ZookeeperNodeController {

    private final Map<String,String> map = new HashMap<>();

    @Autowired
    private IZookeeperNodeService nodeService;

    @Autowired
    private IAuthClientService clientService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 进入授权服务器的登陆界面
     * @param clientId
     * @param redirectUrl
     * @param session
     * @return
     */
    @GetMapping("/login")
    public String toLogin(String clientId, String redirectUrl, HttpSession session) {
        if (clientId != null && redirectUrl != null) {
            //解密clientid
            clientId = new String(Base64.decodeBase64(clientId));
            session.setAttribute("redirectUrl", redirectUrl);
            session.setAttribute("clientId",clientId);
        }
        return "index";
    }

    /**
     * 列出客户端系统列表
     * @param model
     * @return
     */
    @RequestMapping("/client")
    public String getClientList(Model model){
        List<AuthClient> clients = clientService.list();
        model.addAttribute("clients",clients);
        return "emp/list";
    }

    /**
     * 随机生成客户端系统id和密钥
     */
    public void generateClient(){
        String s = UUID.randomUUID().toString();
        map.put("clientId", DigestUtils.md5Hex(s.substring(28).getBytes()));
        map.put("clientSecret",s);
    }

    /**
     * 跳转客户端系统添加页面
     * @param model
     * @return
     */
    @RequestMapping("clientAdd")
    public String addClient(Model model){
        generateClient();
        model.addAttribute("clientId",map.get("clientId"));
        model.addAttribute("clientSecret",map.get("clientSecret"));
        return "emp/add";
    }

    /**
     * 添加客户端系统
     * @param client
     * @return
     */
    @PostMapping("addClient")
    public String add(AuthClient client){
        clientService.save(client);
        redisUtils.set("CLIENT:"+client.getClientId(),client.getClientName());
        return "redirect:/client";
    }

    /**
     * 删除客户端系统
     * @param id
     * @return
     */
    @RequestMapping("client/{id}")
    public String delete(@PathVariable Integer id){
        clientService.removeById(id);
        return "redirect:/client";
    }




}
