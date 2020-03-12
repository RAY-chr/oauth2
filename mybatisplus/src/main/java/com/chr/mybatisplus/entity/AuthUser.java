package com.chr.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 第三方认证服务器的用户表
 * </p>
 *
 * @author RAY
 * @since 2020-01-09
 */
public class AuthUser extends Model<AuthUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    private String password;

    private String qq;

    //private String address;

    private String clientId;

    private Integer loginRest;

    public AuthUser() {
    }

    public AuthUser(Integer id, String username, String password, String clientId, Integer loginRest) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.loginRest = loginRest;
    }

    public AuthUser(Integer id, String username, String password, String qq, String clientId, Integer loginRest) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.qq = qq;
        this.clientId = clientId;
        this.loginRest = loginRest;
    }

    public AuthUser(Integer id, Integer loginRest) {
        this.id = id;
        this.loginRest = loginRest;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
   /* public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }*/

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getLoginRest() {
        return loginRest;
    }

    public void setLoginRest(Integer loginRest) {
        this.loginRest = loginRest;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
            "id=" + id +
            ", username=" + username +
            ", password=" + password +
            ", qq=" + qq +
            /*", address=" + address +*/
        "}";
    }
}
