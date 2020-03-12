package com.chr.code.service;

import org.springframework.stereotype.Component;

import javax.jws.WebService;

/**
 * @author RAY
 * @descriptions
 * @since 2020/1/13
 */
@WebService(serviceName="CodeService",targetNamespace = "http://service.code.chr.com"
        ,endpointInterface="com.chr.code.service.CodeService")
@Component
public class CodeServiceImpl implements CodeService {
    @Override
    public String getCode() {

        return "";
    }
}
