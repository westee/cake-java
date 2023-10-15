package com.westee.cake.service;

import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.WxExpress;
import com.westee.cake.generate.WxExpressExample;
import com.westee.cake.generate.WxExpressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(WxExpressService.class);

    @Autowired
    public WxExpressService(WxExpressMapper wxExpressMapper) {
        this.wxExpressMapper = wxExpressMapper;
    }

    public void insertWxExpress(HashMap<String, Object> map) {
        WxExpress wxExpress = new WxExpress();
        wxExpress.setAppid(map.get("appid").toString());
        wxExpress.setDistance(doubleToInt((double) map.get("distance")));
        wxExpress.setStoreOrderId(map.get("store_order_id").toString());
        wxExpress.setErrcode(doubleToInt((double) map.get("errcode")));
        wxExpress.setWxStoreId(map.get("wx_store_id").toString());
        wxExpress.setWxOrderId(map.get("wx_order_id").toString());
        wxExpress.setErrmsg(map.get("errmsg").toString());
        wxExpress.setServiceTransId(map.get("service_trans_id").toString());
        wxExpress.setTransOrderId(map.get("trans_order_id").toString());
        wxExpress.setFee(doubleToInt((double) map.get("fee")));

        wxExpress.setUpdatedAt(new Date());
        wxExpress.setCreatedAt(new Date());
        if (Objects.equals(wxExpress.getErrmsg(), "ok")) {
            wxExpress.setOrderStatus(WxExpressOrderStatus.ORDER_CREATED.getCode().toString());
            wxExpressMapper.insert(wxExpress);
        } else {
            log.error("插入WxExpress时Errmsg错误：{}", wxExpress.getErrmsg() );
            throw HttpException.badRequest(wxExpress.getErrmsg());
        }
    }

    public Integer doubleToInt(Double d) {
        return d.intValue();
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
