package com.chr.para.service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.HttpSession;

/**
 * @author RAY
 * @descriptions
 * @since 2020/1/13
 */
//指定webservice命名空间

@WebService(targetNamespace = "http://service.para.chr.com")
public interface CodeService {

    @WebMethod
    public String getCode(String clientId);

}
