package com.chr.para.service;

import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.UUID;

/**
 * @author RAY
 * @descriptions
 * @since 2020/1/13
 */
@WebService(serviceName="CodeService",targetNamespace = "http://service.para.chr.com"
        ,endpointInterface="com.chr.para.service.CodeService")
@Component
public class CodeServiceImpl implements CodeService {
    @Override
    public String getCode(String clientId) {
        //生成授权码
        String code = UUID.randomUUID().toString();
        return code;
    }
}
