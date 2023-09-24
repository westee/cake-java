package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.WxExpress;
import com.westee.cake.generate.WxExpressExample;
import com.westee.cake.generate.WxExpressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 创建快递单后存储相关数据
 */
@Service
public class WxExpressService {
    private final WxExpressMapper wxExpressMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WxExpressService(WxExpressMapper wxExpressMapper) {
        this.wxExpressMapper = wxExpressMapper;
    }

    public void insertWxExpress(Object express) throws JsonProcessingException {
        WxExpress wxExpress = objectMapper.readValue(express.toString(), WxExpress.class);
        wxExpress.setUpdatedAt(new Date());
        wxExpress.setCreatedAt(new Date());
        if (Objects.equals(wxExpress.getErrmsg(), "ok")) {
            wxExpress.setOrderStatus(WxExpressOrderStatus.ORDER_CREATED.getCode().toString());
            wxExpressMapper.insert(wxExpress);
        } else {
            throw HttpException.badRequest(wxExpress.getErrmsg());
        }
    }

    public void updateWxExpress(HashMap<String, String> express) {
        WxExpressExample wxExpressExample = new WxExpressExample();
        wxExpressExample.createCriteria().andWxOrderIdEqualTo(express.get("wx_order_id"));
        WxExpress wxExpress = new WxExpress();
        wxExpress.setOrderStatus(express.get("order_status"));
        wxExpress.setUpdatedAt(new Date());
        wxExpressMapper.updateByExampleSelective(wxExpress, wxExpressExample);
    }

    public List<WxExpress> getExpressByWxOrderNo(String wxOrderNo) {
        WxExpressExample wxExpressExample = new WxExpressExample();
        wxExpressExample.createCriteria().andWxOrderIdEqualTo(wxOrderNo);
        return wxExpressMapper.selectByExample(wxExpressExample);
    }

    public enum WxExpressOrderStatus {
        ORDER_CREATED(10000, "订单创建成功"),
        MERCHANT_CANCELLED(20000, "商家取消订单"),
        DELIVERY_CANCELLED(20001, "配送方取消订单"),
        DELIVERY_ACCEPTED(30000, "配送员接单"),
        DELIVERY_ARRIVED(40000, "配送员到店"),
        DELIVERY_ONGOING(50000, "配送中"),
        DELIVERY_CANCELLED_BY_COURIER(60000, "配送员撤单"),
        DELIVERY_COMPLETED(70000, "配送完成"),
        DELIVERY_EXCEPTION(90000, "配送异常");

        private final Integer code;
        private final String description;

        WxExpressOrderStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
