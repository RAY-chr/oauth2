package com.chr.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chr.mybatisplus.entity.Book;
import com.chr.mybatisplus.entity.RentLog;
import com.chr.mybatisplus.entity.Renter;
import com.chr.mybatisplus.mapper.BookMapper;
import com.chr.mybatisplus.service.IBookService;
import com.chr.mybatisplus.service.IRentLogService;
import com.chr.mybatisplus.service.IRenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * <p>
 * 书籍表 服务实现类
 * </p>
 *
 * @author RAY
 * @since 2020-03-06
 */
@Transactional
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

    /*搭配使用确定一个Bean*/
    @Autowired
    @Qualifier("rentLogServiceImpl")
    private IRentLogService logService;

    @Autowired
    private IRenterService renterService;

    @Override
    public void borrow(Integer bookId, String renterName) {
        this.update(new UpdateWrapper<Book>()
                .eq("book_id",bookId)
                .set("book_state","1"));
        RentLog log = new RentLog();
        log.setBookId(String.valueOf(bookId));
        Renter renter = renterService.getOne(new QueryWrapper<Renter>()
                .eq("renter_name", renterName));
        log.setRenterId(String.valueOf(renter.getRenterId()));
        log.setLogState("0");
        log.setBookDate(LocalDate.now());
        logService.save(log);
    }
}
