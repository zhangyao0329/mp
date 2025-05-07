package com.itheima.mp.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.itheima.mp.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "tb_user", autoResultMap = true)
public class User {

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 注册手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 详细信息
     */
    @TableField(value = "info",typeHandler = JacksonTypeHandler.class)
    private UserInfo info;

    /**
     * 使用状态（1正常 2冻结）
     */
    @TableField("status")
    private UserStatus status;

    /**
     * 账户余额
     */
    @TableField("balance")
    private Integer balance;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
