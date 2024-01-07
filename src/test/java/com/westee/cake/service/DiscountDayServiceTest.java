package com.westee.cake.service;

import com.westee.cake.dao.MyGoodsWithImageMapper;
import com.westee.cake.entity.GoodsWithImages;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.generate.DiscountDay;
import com.westee.cake.generate.DiscountDayMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountDayServiceTest {

    @InjectMocks
    DiscountDayService discountDayService;

    @Mock
    GoodsService goodsService;

    @Mock
    DiscountDayMapper discountDayMapper;

    @Mock
    private MyGoodsWithImageMapper myGoodsWithImageMapper;

    @Test
    void getDiscountDayListTest() {
        String goodsName = "商品";
        DiscountDay discountDayMock = new DiscountDay();
        discountDayMock.setId(1);
        discountDayMock.setGoodsId(1L);
        discountDayMock.setGoodsName(goodsName);
        GoodsWithImages goodsWithImages = new GoodsWithImages();
        goodsWithImages.setName(goodsName);
        goodsWithImages.setPrice(BigDecimal.TEN);
        lenient().when(goodsService.getGoodsByGoodsId(1)).thenReturn(goodsWithImages);
        when(discountDayMapper.selectByExample(any())).thenReturn(List.of(discountDayMock));

        int pageSize = 10;
        int pageNum = 1;
        PageResponse<DiscountDay> discountDayListResponse = discountDayService.getDiscountDayList(pageNum, pageSize);
        assertEquals(pageNum, discountDayListResponse.getPageNum());
        assertEquals(pageSize, discountDayListResponse.getPageSize());
        assertNotNull(discountDayListResponse.getData());
        assertEquals(goodsName, discountDayListResponse.getData().get(0).getGoodsName());
    }

    @Test
    void createDiscountDayTest() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 减1为星期几，这里为了测试改为2
        String dayOfWeekStr = String.valueOf(dayOfWeek - 2);

        DiscountDay discountDayMock = new DiscountDay();
        discountDayMock.setId(1);
        discountDayMock.setDisabled(false);
        discountDayMock.setGoodsId(1L);
        discountDayMock.setDays(dayOfWeekStr);
        discountDayMock.setPrice(BigDecimal.TEN);
        List<DiscountDay> list = List.of(discountDayMock);
        List<DiscountDay> listEmpty = List.of();

        when(discountDayMapper.selectByExample(any())).thenReturn(list);
        GoodsWithImages goodsWithImages = new GoodsWithImages();
        String cakeName = "good cake";
        goodsWithImages.setName(cakeName);
        goodsWithImages.setId(1L);
        goodsWithImages.setPrice(BigDecimal.TEN);

        lenient().when(myGoodsWithImageMapper.getGoodsWithImage(anyLong())).thenReturn(goodsWithImages);
        lenient().when(goodsService.getGoodsByGoodsId(1L)).thenReturn(goodsWithImages);

        when(discountDayMapper.selectByExample(any())).thenReturn(listEmpty);
        when(discountDayMapper.selectByExample(any())).thenReturn(listEmpty);
        DiscountDay discountDay = discountDayService.createDiscountDay(discountDayMock);

        assertEquals(false, discountDay.getDisabled());
        assertEquals(cakeName, discountDay.getGoodsName());
    }

    @Test
    void updateDiscountDayTest() {
        DiscountDay discountDayMock = new DiscountDay();
        discountDayMock.setId(1);
        discountDayMock.setDisabled(true);
        when(discountDayService.updateDiscountDay(discountDayMock)).thenReturn(discountDayMock);
        DiscountDay discountDay = discountDayService.updateDiscountDay(discountDayMock);
        assertEquals(true, discountDay.getDisabled());
    }

    @Test
    void deleteDiscountDayTest() {
        DiscountDay discountDayMock = new DiscountDay();
        discountDayMock.setId(1);
        discountDayMock.setDisabled(true);
        when(discountDayService.deleteDiscountDay(1)).thenReturn(discountDayMock);

        DiscountDay discountDay = discountDayService.deleteDiscountDay(1);

        assertEquals(true, discountDay.getDisabled());
    }
}