package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.DeleteStatus;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Address;
import com.westee.cake.generate.AddressExample;
import com.westee.cake.generate.AddressMapper;
import com.westee.cake.mapper.MyAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AddressService {
    private final AddressMapper addressMapper;
    private final MyAddressMapper myAddressMapper;

    @Autowired
    public AddressService(AddressMapper mapper, MyAddressMapper myAddressMapper) {
        this.addressMapper = mapper;
        this.myAddressMapper = myAddressMapper;
    }

    public PageResponse<Address> getAddressListByShopId(int pageNum, int pageSize, long userId) {
        AddressExample addressExample = new AddressExample();
        addressExample.createCriteria().andUserIdEqualTo(userId).andDeletedEqualTo(DeleteStatus.OK.getValue());
        long count = addressMapper.countByExample(addressExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        PageHelper.startPage(pageNum, pageSize);
        List<Address> addressList = addressMapper.selectByExample(addressExample);
        return PageResponse.pageData(pageNum, pageSize, totalPage, addressList);
    }

    public Address getAddressById(long addressId, long userId) {
        Address address = addressMapper.selectByPrimaryKey(addressId);
        if (Objects.isNull(address)) {
            throw HttpException.notFound("地址未找到");
        }
        if (!Objects.equals(address.getUserId(), userId)) {
            throw HttpException.notAuthorized("没有权限");
        }
        return address;
    }

    public Address createAddress(Address address, long userId) {
        address.setUserId(userId);
        address.setId(null);
        address.setDeleted(DeleteStatus.OK.getValue());
        address.setUpdatedAt(new Date());
        address.setCreatedAt(new Date());
        addressMapper.insertSelective(address);
        return address;
    }

    public Address updateAddress(Address address, long userId) {
        Address addressResult = addressMapper.selectByPrimaryKey(address.getId());
        if (Objects.equals(addressResult, null)) {
            throw HttpException.forbidden("参数不合法");
        }
        Long ownerUserId = addressResult.getUserId();
        if (Objects.equals(ownerUserId, userId)) {
            address.setUpdatedAt(new Date());
            address.setCreatedAt(new Date());
            addressMapper.updateByPrimaryKeySelective(address);
            return address;
        } else {
            throw HttpException.forbidden("拒绝访问");
        }
    }

    public Address deleteAddress(Long addressId, long userId) {
        Address address = addressMapper.selectByPrimaryKey(addressId);
        if (Objects.equals(address, null)) {
            throw HttpException.forbidden("参数不合法");
        }
        Long ownerUserId = address.getUserId();
        if (Objects.equals(ownerUserId, userId)) {
            address.setDeleted(DeleteStatus.DELETED.getValue());
            address.setUpdatedAt(new Date());
            address.setCreatedAt(new Date());
            addressMapper.updateByPrimaryKeySelective(address);
            return address;
        } else {
            throw HttpException.forbidden("拒绝访问");
        }
    }

    public Address getAddressDefaultOrNewest(Long tokenUserId) {
        AddressExample addressExample = new AddressExample();
        addressExample.createCriteria().andUserIdEqualTo(tokenUserId).andDefaultAddressEqualTo(true);

        List<Address> addressList = addressMapper.selectByExample(addressExample);
        if (addressList.isEmpty()) {
            Address address = myAddressMapper.selectLatestAddress(tokenUserId);
            if (Objects.isNull(address)) {
                throw HttpException.notFound("请先添加一个地址");
            } else {
                return address;
            }
        } else {
            return addressList.get(0);
        }
    }
}
