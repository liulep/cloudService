package com.yue.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yue.user.core.entity.User;
import org.springframework.stereotype.Repository;

//用户mapper
@Repository
public  interface UserMapper extends BaseMapper<User> {
}
