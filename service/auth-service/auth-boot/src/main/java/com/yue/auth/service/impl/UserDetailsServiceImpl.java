package com.yue.auth.service.impl;

import com.yue.auth.mapper.UserMapper;
import com.yue.common.model.security.SecurityUser;
import com.yue.auth.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findUserByUsername(username);
        if(ObjectUtils.isEmpty(user)){
            throw new RuntimeException("用户不能为空");
        }
        List<String> roles = userMapper.findUserRoleByUsername(user.getUsername());
        return SecurityUser
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.getEnabled()==0)
                .accountNonExpired(true)
                .accountNonLocked(user.getAccountNonLocked()==0)
                .credentialsNonExpired(true)
                .authorities(authorizes(roles))
                .build();
    }

    //获取权限集合
    private Collection<? extends GrantedAuthority> authorizes(List<String> roles){
        List<String> authorizes = roles.stream().map(item -> "ROLE_" + item).collect(Collectors.toList());
        return AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",",authorizes));
    }
}
