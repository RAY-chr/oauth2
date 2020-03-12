package com.chr.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 借阅记录表
 * </p>
 *
 * @author RAY
 * @since 2020-03-05
 */
public class RentLog extends Model<RentLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 借阅记录id
     */
    @TableId(value = "rent_id", type = IdType.AUTO)
    private Integer rentId;

    /**
     * 借阅用户id
     */
    private String renterId;

    /**
     * 书籍id
     */
    private String bookId;

    /**
     * 书籍编号
     */
    private String bookNo;

    /**
     * 借书时间
     */
    private LocalDate bookDate;

    /**
     * 批量借阅编号，同一批借阅的batch_no相同
     */
    private String batchNo;

    /**
     * 1为完成，即用户已还书，0为正在借阅中
     */
    private String logState;

    public Integer getRentId() {
        return rentId;
    }

    public void setRentId(Integer rentId) {
        this.rentId = rentId;
    }
    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    public LocalDate getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDate bookDate) {
        this.bookDate = bookDate;
    }
    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public String getLogState() {
        return logState;
    }

    public void setLogState(String logState) {
        this.logState = logState;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    @Override
    protected Serializable pkVal() {
        return this.rentId;
    }

    @Override
    public String toString() {
        return "RentLog{" +
            "rentId=" + rentId +
            ", renterId=" + renterId +
            ", bookId=" + bookId +
            ", bookDate=" + bookDate +
            ", batchNo=" + batchNo +
            ", logState=" + logState +
        "}";
    }
}
