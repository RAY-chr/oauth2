package com.chr.para.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chr.para.curator.CuratorApi;
import com.chr.para.entity.AuthUser;
import com.chr.para.mapper.AuthUserMapper;
import com.chr.para.service.IAuthUserService;
import com.chr.para.utils.JsonUtils;
import com.chr.para.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author RAY
 * @since 2020-01-09
 */
@Service
public class AuthUserServiceImpl extends ServiceImpl<AuthUserMapper, AuthUser> implements IAuthUserService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AuthUserMapper authUserMapper;

    /**
     * 得到剩余登陆次数
     * @param username
     * @return
     */
    @Override
    public AuthUser getLoginRest(String username) {
        return authUserMapper.getLoginRest(username);
    }

    /**
     * 更改剩余登录次数
     * @param user
     */
    @Override
    public void updateLoginRest(AuthUser user) {
        authUserMapper.updateLoginRest(user);
    }

    /**
     * 存令牌
     * @param nodeName
     * @param token
     */
    @Override
    public void storeToken(String nodeName, String token) {
        try {
            CuratorApi.INSTANCE.createNode(nodeName,token);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("写入失败");
        }
    }

    /**
     * 校验令牌返回自定义实体类
     * @param token
     * @param beanType
     * @param <T>
     * @return
     */
    @Override
    public <T> T getByToken(String token, Class<T> beanType) {
        String s = redisUtils.get("UserToken:" + token);
        if (s == null) {
            return null;
        }
        T t = JsonUtils.jsonToPojo(s, beanType);
        return t;
    }
}
