package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = new User();
        user.setId(5L);
        user.setUsername("Lucy");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.saveUser(user);
    }

    @Test
    void testSelectById() {
        User user = userMapper.queryUserById(5L);
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        List<User> users = userMapper.queryUserByIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        userMapper.updateUser(user);
    }

    @Test
    void testDeleteUser() {
        userMapper.deleteUser(5L);
    }

    /**
     * 测试自定义SQL更新操作。
     * 该测试用例用于验证通过自定义SQL语句更新用户余额的功能。
     *
     * 测试步骤：
     * 1. 创建一个包含用户ID的列表。
     * 2. 设置要更新的金额。
     * 3. 使用LambdaQueryWrapper构建查询条件，筛选出指定ID的用户。
     * 4. 调用userMapper的updateBalanceByIds方法，更新符合条件的用户的余额。
     */
    @Test
    void testCustomSqlUpdate() {
        // 创建包含用户ID的列表
        List<Long> ids = List.of(1L, 2L, 3L);

        // 设置要更新的金额
        int amount = 100;

        // 使用LambdaQueryWrapper构建查询条件，筛选出指定ID的用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getId, ids);

        // 调用userMapper的updateBalanceByIds方法，更新符合条件的用户的余额
        userMapper.updateBalanceByIds(wrapper, amount);
    }










}