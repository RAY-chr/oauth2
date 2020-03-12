package com.chr.para.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author RAY
 * @since 2020-01-08
 */
public class ZookeeperNode extends Model<ZookeeperNode> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * zookeeper中的节点名称
     */
    private String nodeName;

    /**
     * 请求token的客户端id
     */
    private String clientId;

    /**
     * 客户端回调的地址
     */
    private String redirectUrl;

    private Integer nodeUser;

    public ZookeeperNode(String nodeName, String clientId, String redirectUrl,Integer nodeUser) {
        this.nodeName = nodeName;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
        this.nodeUser = nodeUser;
    }

    public ZookeeperNode(String nodeName, String clientId) {
        this.nodeName = nodeName;
        this.clientId = clientId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getNodeUser() {
        return nodeUser;
    }

    public void setNodeUser(Integer nodeUser) {
        this.nodeUser = nodeUser;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ZookeeperNode{" +
                "id=" + id +
                ", nodeName='" + nodeName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}
