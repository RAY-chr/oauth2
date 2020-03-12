package com.chr.para.service;

import com.chr.para.entity.AuthUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RAY
 * @since 2020-01-09
 */
public interface IAuthUserService extends IService<AuthUser> {

    /**
     * 更改剩余登录次数
     * @param user
     */
    public void updateLoginRest(AuthUser user);

    /**
     * 得到剩余登陆次数
     * @param username
     * @return
     */
    public AuthUser getLoginRest(String username);

    /**
     * 把token存入zookeeper
     * @param nodeName
     * @param token
     */
    public void storeToken(String code,String token);

    /**
     * 校验令牌返回自定义实体类
     * @param token
     * @param beanType
     * @param <T>
     * @return
     */
    public <T> T getByToken(String token, Class<T> beanType);
}
