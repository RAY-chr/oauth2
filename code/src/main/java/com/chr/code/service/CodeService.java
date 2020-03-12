package com.chr.code.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author RAY
 * @descriptions
 * @since 2020/1/13
 */
//指定webservice命名空间

@WebService(targetNamespace = "http://service.code.chr.com")
public interface CodeService {

    @WebMethod
    public String getCode();

}
