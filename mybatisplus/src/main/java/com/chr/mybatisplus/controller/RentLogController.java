package com.chr.mybatisplus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chr.mybatisplus.entity.Book;
import com.chr.mybatisplus.entity.RentLog;
import com.chr.mybatisplus.entity.Renter;
import com.chr.mybatisplus.service.IBookService;
import com.chr.mybatisplus.service.IRentLogService;
import com.chr.mybatisplus.service.IRenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 借阅记录表 前端控制器
 * </p>
 *
 * @author RAY
 * @since 2020-03-05
 */
@Controller
//@RequestMapping("/rentLog")
public class RentLogController {

    @Autowired
    private IRentLogService logService;

    @Autowired
    private IBookService bookService;

    @Autowired
    private IRenterService renterService;

    /**
     * 返回自定义集合
     * @param list
     * @param fun
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> getData(List<T> list, Function<T,R> fun){
        List<R> collect = list.stream().map(t -> fun.apply(t)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 我的借阅
     * @param model
     * @return
     */
    @RequestMapping("borrowed")
    public String borrowed(Model model, HttpSession session){
        Renter renter = renterService.getOne(new QueryWrapper<Renter>()
                .eq("renter_name", session.getAttribute("user")));
        List<RentLog> rentLogs = logService.list(new QueryWrapper<RentLog>()
                .eq("renter_id", renter.getRenterId())
                .eq("log_state","0"));
        rentLogs= getData(rentLogs, x -> {
            Book book = bookService.getOne(new QueryWrapper<Book>().eq("book_id",x.getBookId()));
            x.setBookId(book.getBookName());
            x.setBookNo(book.getBookNo());
            return x;
        });
        System.out.println(rentLogs);
        model.addAttribute("rentLogs",rentLogs);
        return "emp/list";
    }

    /**
     * 还书
     * @param rentId
     * @return
     */
    @RequestMapping("/repay/{rentId}")
    public String repay(@PathVariable Integer rentId){
        logService.update(new UpdateWrapper<RentLog>()
                .eq("rent_id",rentId)
                .set("log_state","1"));
        RentLog log = logService.getById(rentId);
        bookService.update(new UpdateWrapper<Book>()
                .eq("book_id",log.getBookId())
                .set("book_state","0"));
        return "redirect:/borrowed";
    }

}
