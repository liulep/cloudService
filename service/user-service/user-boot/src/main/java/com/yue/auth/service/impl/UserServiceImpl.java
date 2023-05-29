package com.yue.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yue.user.core.entity.User;
import com.yue.auth.mapper.UserMapper;
import com.yue.auth.service.intf.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
