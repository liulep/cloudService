package com.yue.filter.service.intf;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yue.filter.core.entity.User;


public interface UserService extends IService<User> {

    public User selectRoleByUserName(String username);
}
