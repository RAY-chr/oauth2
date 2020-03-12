package com.chr.para.entity;

import java.util.List;

/**
 * 上传用户的数据列表
 * @author RAY
 * @descriptions
 * @since 2020/2/28
 */
public class UserList {

    private String batchNo;
    private List<AuthUserCheck> authUsers;

    public UserList() {
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public List<AuthUserCheck> getAuthUsers() {
        return authUsers;
    }

    public void setAuthUsers(List<AuthUserCheck> authUsers) {
        this.authUsers = authUsers;
    }
}
