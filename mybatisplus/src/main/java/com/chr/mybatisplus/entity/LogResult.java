package com.chr.mybatisplus.entity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author RAY
 */
public class LogResult {

    private Integer rentId;
    private String bookName;
    private String renterName;
    private LocalDate rentDate;
    private String batchNo;

    public LogResult(String renterName, String batchNo) {
        this.renterName = renterName;
        this.batchNo = batchNo;
    }

    public Integer getRentId() {
        return rentId;
    }

    public void setRentId(Integer rentId) {
        this.rentId = rentId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @Override
    public String toString() {
        return "LogResult{" +
                "rentId=" + rentId +
                ", bookName='" + bookName + '\'' +
                ", renterName='" + renterName + '\'' +
                ", rentDate=" + rentDate +
                ", batchNo='" + batchNo + '\'' +
                '}';
    }
}
