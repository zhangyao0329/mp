package com.itheima.mp.service.Impl;

import com.itheima.mp.domain.po.Address;
import com.itheima.mp.mapper.AddressMapper;
import com.itheima.mp.service.IAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyao
 * @since 2025-05-07
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
