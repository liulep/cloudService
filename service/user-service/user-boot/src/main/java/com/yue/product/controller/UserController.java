package com.yue.product.controller;
import com.yue.common.model.security.LoginUser;
import com.yue.common.utils.OauthUtils;
import com.yue.resp.anno.NotControllerResponseAdvice;
import com.yue.user.core.entity.User;
import com.yue.product.service.intf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //获取当前用户信息
    @GetMapping("/currentInfo")
    public LoginUser getUser(){
        LoginUser currentUser = OauthUtils.getCurrentUser();
        return currentUser;
    }

    //通过Id获取用户信息
    @GetMapping("/get/{uid}")
    @NotControllerResponseAdvice
    public User getUserInfoById(@PathVariable(value = "uid")Integer uid){
        return userService.getById(uid);
    }
}
