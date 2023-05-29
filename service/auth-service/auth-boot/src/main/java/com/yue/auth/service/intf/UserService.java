package com.yue.auth.service.intf;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yue.auth.core.entity.User;


public interface UserService extends IService<User> {

    public User selectRoleByUserName(String username);
}
