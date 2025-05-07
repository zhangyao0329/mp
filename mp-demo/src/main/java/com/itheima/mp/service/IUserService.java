package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;

import java.util.List;

public interface IUserService extends IService<User> {

    List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance);

    void deductBalance(Long id, Integer money);

    UserVO queryUserAddById(Long id);

    List<UserVO> queryUserAddByIds(List<Long> ids);

    PageDTO<UserVO> queryUserpager(UserQuery query);
}
