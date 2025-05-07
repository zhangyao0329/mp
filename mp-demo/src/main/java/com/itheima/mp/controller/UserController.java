package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@Api(tags = "用户管理接口")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @ApiOperation(value ="根据条件分页查询用户接口")
    @GetMapping("/page")
    public PageDTO<UserVO> queryUsersPage(UserQuery query){
        return userService.queryUserpager(query);

    }

    @ApiOperation(value ="(优化)->根据条件分页查询用户接口")
    @GetMapping("/page2")
    public PageDTO<UserVO> queryUsersPage2(UserQuery query){
        return userService.queryUsersPage(query);
    }








    @ApiOperation("新增用户")
    @PostMapping
    public void saveUser(@RequestBody UserFormDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userService.save(user);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("{id}")
    public void deleteUserById(@ApiParam("用户id") @PathVariable("id") Long id) {
        userService.removeById(id);
    }

    //    @ApiOperation("根据id查询用户")
//    @GetMapping("/{id}")
//    public UserVO getUserById(@ApiParam("用户id") @PathVariable("id") Long id) {
//        User user = userService.getById(id);
//        UserVO userVO = new UserVO();
//        BeanUtils.copyProperties(user, userVO);
//        return userVO;
//    }
    @ApiOperation("根据id查询用户")
    @GetMapping("/{id}")
    public UserVO getUserAndAddressById(@ApiParam("用户id") @PathVariable("id") Long id) {

        return userService.queryUserAddById(id);
    }

    //    @ApiOperation("根据id批量查询用户")
//    @GetMapping()
//    public List<UserVO> getUserByIds(@ApiParam("用户id集合") @RequestParam("ids") List<Long> ids) {
//        List<User> users = userService.listByIds(ids);
//        List<UserVO> userVOS = BeanUtil.copyToList(users, UserVO.class);
//        return userVOS;
//    }
    @ApiOperation("根据id批量查询用户")
    @GetMapping
    public List<UserVO> getUserByIds(@ApiParam("用户id集合") @RequestParam("ids") List<Long> ids) {

        return userService.queryUserAddByIds(ids);
    }

    @ApiOperation("根据id扣减余额")
    @PutMapping("/{id}/deduction/{money}")
    public void deductionMoneyById(@ApiParam("用户id") @PathVariable Long id, @ApiParam("金额") @PathVariable Integer money) {
//        根据用户id查找用户
        User user = userService.getById(id);
        Integer userBalance = user.getBalance();

        UserStatus status = user.getStatus();
        if (status == UserStatus.FREEZE || user == null) {
            throw new RuntimeException("用户状态异常");
        }
        userBalance -= money;
        if (userBalance < 0) {
            throw new RuntimeException("余额不足");
        }
        user.setBalance(userBalance);
        userService.update(user, new LambdaQueryWrapper<User>().eq(User::getId, id));
    }

    @PutMapping("{id}/deduct/{money}")
    @ApiOperation("根据id扣减余额")
    public void deductBalance(@PathVariable("id") Long id, @PathVariable("money") Integer money) {
        userService.deductBalance(id, money);
    }


    @ApiOperation("根据复杂条件查询用户")
    @GetMapping("/list1")
    public List<UserVO> queryUsers1(@ApiParam("用户名") @RequestParam(value = "username", required = false) String username,
                                    @ApiParam("使用状态") @RequestParam(value = "status", required = false) Integer status,
                                    @ApiParam("最小余额") @RequestParam(value = "minBalance", required = false) String minBalance,
                                    @ApiParam("最大余额") @RequestParam(value = "maxBalance", required = false) String maxBalance
    ) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance);
        List<User> users = userService.list(wrapper);
        List<UserVO> userVOS = BeanUtil.copyToList(users, UserVO.class);
        return userVOS;
    }

    @ApiOperation("根据复杂条件查询用户")
    @GetMapping("/list2")
    public List<UserVO> queryUsers2(UserQuery userQuery) {
        List<User> user = userService.queryUsers(userQuery.getName(), userQuery.getStatus(), userQuery.getMinBalance(), userQuery.getMaxBalance());
        List<UserVO> userVOS = BeanUtil.copyToList(user, UserVO.class);
        return userVOS;
    }


}
