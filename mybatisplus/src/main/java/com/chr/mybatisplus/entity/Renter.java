package com.chr.mybatisplus.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 借阅用户表
 * </p>
 *
 * @author RAY
 * @since 2019-12-13
 */
public class Renter extends Model<Renter> {

    private static final long serialVersionUID = 1L;

    /**
     * 借阅用户id
     */
    private Integer renterId;

    /**
     * 借阅用户姓名
     */
    private String renterName;

    public Integer getRenterId() {
        return renterId;
    }

    public void setRenterId(Integer renterId) {
        this.renterId = renterId;
    }
    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    @Override
    protected Serializable pkVal() {
        return this.renterId;
    }

    @Override
    public String toString() {
        return "Renter{" +
            "renterId=" + renterId +
            ", renterName=" + renterName +
        "}";
    }
}
