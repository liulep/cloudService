package com.yue.filter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yue.filter.core.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

    public User findUserByUsername(@Param("username")String username);

    public List<String> findUserRoleByUsername(@Param("username")String username);

    public List<String> findUserMenuByRole(List<String> roleCodes);

    public List<String> findUserRolesByUrl(@Param("url") String url);

    @Select("select url from menu\n" + "UNION\n" + "select url from api")
    public List<String> findUserMenusAndApi();
}
