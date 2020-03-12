package com.chr.para.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author RAY
 * @since 2020-02-28
 */
public class AuthUserCheck extends Model<AuthUserCheck> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String batchNo;

    private String username;

    private String password;

    private String qq;

    private String clientId;

    private Integer loginRest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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
        return "AuthUserCheck{" +
            "id=" + id +
            ", batchNo=" + batchNo +
            ", username=" + username +
            ", password=" + password +
            ", qq=" + qq +
            ", clientId=" + clientId +
            ", loginRest=" + loginRest +
        "}";
    }
}
