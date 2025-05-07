package com.itheima.mp.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.AddressMapper;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final AddressMapper addressMapper;

    public UserServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {

        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance).list();
    }

    @Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        // 1.查询用户
        User user = getById(id);
        // 2.判断用户状态
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }
        // 3.判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 4.扣减余额
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getStatus, UserStatus.FREEZE)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) // 避免并发问题 乐观锁
                .update();

    }

    /**
     * 根据用户ID查询用户信息及其地址信息，并封装为UserVO对象返回。
     *
     * @param id 用户ID，用于查询用户信息
     * @return UserVO 包含用户信息及其地址信息的视图对象
     * @throws RuntimeException 如果用户不存在或用户状态异常，则抛出运行时异常
     */
    @Override
    public UserVO queryUserAddById(Long id) {
        // 根据ID查询用户信息
        User user = getById(id);
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }

        /**
         * 根据用户ID查询用户的地址信息
         *
         * 该代码块通过数据库查询获取与指定用户ID关联的地址列表。
         * 使用lambdaQuery方法构建查询条件，并通过eq方法指定用户ID的匹配条件。
         * 最终通过list方法执行查询并返回结果。
         * SELECT * FROM address WHERE user_id = ${id};
         */
        List<Address> addresseses = Db
                .lambdaQuery(Address.class)
                .eq(Address::getUserId, id)
                .list();

        // 将用户信息拷贝到UserVO对象中
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

        // 如果用户有地址信息，则将地址信息拷贝到UserVO对象中
        if (CollUtil.isNotEmpty(addresseses)) {
            userVO.setAddresses(BeanUtil.copyToList(addresseses, AddressVO.class));
        }

        return userVO;
    }

    /**
     * 根据用户ID列表查询用户及其地址信息，并返回用户视图对象列表。
     *
     * @param ids 用户ID列表，不能为null
     * @return 包含用户及其地址信息的用户视图对象列表，如果查询不到用户则返回空列表
     */
    @Override
    public List<UserVO> queryUserAddByIds(List<Long> ids) {
        // 根据ID列表查询用户信息
        List<User> users = listByIds(ids);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }

        // 获取用户ID列表，用于查询地址信息
        List<Long> userIdList = users.stream().map(User::getId).collect(Collectors.toList());
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIdList).list();
        List<AddressVO> addressVoList = BeanUtil.copyToList(addresses, AddressVO.class);

        // 将地址信息按用户ID分组，方便后续处理
        Map<Long, List<AddressVO>> adressMap = new HashMap<>(0);
        if (CollUtil.isNotEmpty(addressVoList)) {
            adressMap = addressVoList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }

        // 将用户信息转换为用户视图对象，并设置对应的地址信息
        List<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            list.add(userVO);
            userVO.setAddresses(adressMap.get(user.getId()));
        }

        // 返回用户视图对象列表
        return list;
    }

    /**
     * 分页查询用户信息，并根据查询条件进行过滤和排序。
     *
     * @param query 用户查询条件对象，包含分页参数、排序条件、用户名和状态等过滤条件。
     * @return PageDTO<UserVO> 分页查询结果，包含总记录数、总页数以及当前页的用户信息列表。
     */
    @Override
    public PageDTO<UserVO> queryUserpager(UserQuery query) {
        // 1. 构建查询条件
        String name = query.getName();
        Integer status = query.getStatus();

        // 2. 分页查询
        Page<User> page = Page.of(query.getPageNo(), query.getPageSize());
        // 判断排序条件是否为空，若为空则默认按照更新时间降序排序
        if (query.getSortBy() != null) {
            page.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
        } else {
            page.addOrder(new OrderItem("update_time", false));
        }
        // 执行分页查询，根据用户名和状态进行过滤
        Page<User> p = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .page(page);

        // 3. 封装VO结果
        PageDTO<UserVO> dto = new PageDTO<>();
        dto.setTotal(p.getTotal());
        dto.setPages(p.getPages());
        // 获取当前页数据，若为空则返回空列表
        List<User> records = p.getRecords();
        if (CollUtil.isEmpty(records)) {
            dto.setList(Collections.emptyList());
            return dto;
        }
        // 将User对象列表转换为UserVO对象列表
        List<UserVO> vos = BeanUtil.copyToList(records, UserVO.class);
        dto.setList(vos);

        // 4. 返回结果
        return dto;
    }
}
