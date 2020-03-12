package com.chr.para.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 保存了认证服务器认证的客户端
 * </p>
 *
 * @author RAY
 * @since 2020-02-26
 */
public class AuthClient extends Model<AuthClient> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户端名字
     */
    private String clientName;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端的描述
     */
    private String clientDes;

    /**
     * 客户端密钥
     */
    private String clientSecret;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientDes() {
        return clientDes;
    }

    public void setClientDes(String clientDes) {
        this.clientDes = clientDes;
    }
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AuthClient{" +
            "id=" + id +
            ", clientName=" + clientName +
            ", clientId=" + clientId +
            ", clientDes=" + clientDes +
            ", clientSecret=" + clientSecret +
        "}";
    }
}
