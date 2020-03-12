package com.chr.mybatisplus.mapper;

import com.chr.mybatisplus.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author RAY
 * @since 2019-12-06
 */
public interface UserMapper extends BaseMapper<User> {

    public List<Integer> queryIds();

}
