package com.chr.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chr.mybatisplus.entity.Book;
import com.chr.mybatisplus.mapper.BookMapper;
import com.chr.mybatisplus.service.IBookService;
import com.chr.mybatisplus.service.IRentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 书籍表 服务实现类
 * </p>
 *
 * @author RAY
 * @since 2020-03-06
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

    /*搭配使用确定一个Bean*/
    @Autowired
    @Qualifier("rentLogServiceImpl")
    private IRentLogService logService;
}
