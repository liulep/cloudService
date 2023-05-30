package com.yue.product.service.intf;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yue.product.core.entity.User;


public interface UserService extends IService<User> {

    public User selectRoleByUserName(String username);
}
