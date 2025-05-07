package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class IUserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("Lilei");
        user.setPassword("123");
        user.setPhone("18211848011");
        user.setBalance(1000);
        user.setInfo(UserInfo.of(24, "英语老师", "女"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userService.save(user);
    }

    @Test
    void testQuery() {
        User user = userService.getById(1L);
        System.out.println(user);

        List<User> users = userService.listByIds(List.of(1L, 2L, 3L));
        users.forEach(System.out::println);
    }

    @Test
    void testPageQuert() {
        int pageNo = 1, pageSize = 5;
// 分页参数
        Page<User> page = Page.of(pageNo, pageSize);
// 排序参数, 通过OrderItem来指定
        page.addOrder(new OrderItem("balance", false));
//分页查询
        Page<User> p = userService.page(page);
//        解析
        long total = p.getTotal();
        System.out.println("总记录数：" + total);
        long pages = p.getPages();
        System.out.println("总页数：" + pages);
        List<User> users = p.getRecords();
        users.forEach(System.out::println);

    }


}