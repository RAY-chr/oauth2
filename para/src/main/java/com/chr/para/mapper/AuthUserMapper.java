package com.chr.para.mapper;

import com.chr.para.entity.AuthUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author RAY
 * @since 2020-01-09
 */
public interface AuthUserMapper extends BaseMapper<AuthUser> {

    /**
     * 得到剩余登陆次数
     * @param username
     * @return
     */
    public AuthUser getLoginRest(String username);


    /**
     * 更改剩余登录次数
     * @param user
     */
    public void updateLoginRest(AuthUser user);

}
