package com.chr.mybatisplus.service.impl;

import com.chr.mybatisplus.entity.RentLog;
import com.chr.mybatisplus.mapper.RentLogMapper;
import com.chr.mybatisplus.service.IRentLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 借阅记录表 服务实现类
 * </p>
 *
 * @author RAY
 * @since 2020-03-05
 */
@Service
public class RentLogServiceImpl extends ServiceImpl<RentLogMapper, RentLog> implements IRentLogService {

}
