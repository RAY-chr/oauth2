package com.chr.para.service.impl;

import com.chr.para.entity.AuthClient;
import com.chr.para.mapper.AuthClientMapper;
import com.chr.para.service.IAuthClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 保存了认证服务器认证的客户端 服务实现类
 * </p>
 *
 * @author RAY
 * @since 2020-02-26
 */
@Service
public class AuthClientServiceImpl extends ServiceImpl<AuthClientMapper, AuthClient> implements IAuthClientService {

}
