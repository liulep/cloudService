package com.yue.user.core.entity;

import com.yue.common.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

//用户实体类
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class User extends BaseEntity implements Serializable {

    private Integer id;

    private String username; //用户名

    private String password; //密码

    private String phone; //联系方式

    private String email; //电子邮件

    private Integer enabled; //用户是否禁用

    private Integer orgId; //组织Id

    private Integer accountNonLocked; //账号是否锁定

}
