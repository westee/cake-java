package com.westee.cake.service;

import com.westee.cake.dao.MyCakeWithTagMapper;
import com.westee.cake.entity.CakeWithTag;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Cake;
import com.westee.cake.generate.CakeMapper;
import com.westee.cake.generate.CakeTag;
import com.westee.cake.generate.CakeTagMapping;
import com.westee.cake.generate.CakeTagMappingMapper;
import com.westee.cake.generate.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CakeServiceTest {
    @Mock

    private CakeMapper cakeMapper;
    @Mock
    private CakeTagMappingMapper cakeTagMappingMapper;

    @Mock
    private MyCakeWithTagMapper myCakeWithTagMapper;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private CakeService cakeService;

    @Test
    void getCakeList() {
        Cake cake = new Cake();
        cake.setId(1L);
        ArrayList<Cake> cakes = new ArrayList<>();
        cakes.add(cake);
        cakes.add(cake);
        when(cakeMapper.selectByExample(any())).thenReturn(cakes);

        CakeWithTag cakeWithTag = getCakeWithTag();
        when(myCakeWithTagMapper.selectCakeWithTagsByCakeId(anyLong())).thenReturn(cakeWithTag);

        PageResponse<CakeWithTag> response = cakeService.getCakeList(1, 10);
        assertEquals(1, response.getPageNum());
        assertEquals("小蛋糕", response.getData().get(0).getName());
    }

    @Test
    void getCakeByCakeTagTest() {
        CakeTagMapping cakeTagMapping = new CakeTagMapping();
        cakeTagMapping.setCakeId("1");
        cakeTagMapping.setTagId("1");
        cakeTagMapping.setId(1L);

        // 准备模拟数据
        List<CakeTagMapping> cakeTagMappings = Arrays.asList(
                cakeTagMapping,
                cakeTagMapping
        );
        Cake cake = new Cake();
        cake.setId(1L);
        cake.setName("Cake1");

        List<Cake> cakes = Arrays.asList(
                cake,cake
        );

        // 模拟数据库操作返回指定数据
        when(cakeTagMappingMapper.selectByExample(any())).thenReturn(cakeTagMappings);
        when(myCakeWithTagMapper.selectCakeWithTagsByCakeId(anyLong())).thenReturn(getCakeWithTag());
        when(cakeMapper.selectByPrimaryKey(anyLong())).thenReturn(cakes.get(0));

        // 调用方法并验证返回值
        PageResponse<CakeWithTag> response = cakeService.getCakeByCakeTag(1, 2, 2);
        assertEquals(1, response.getPageNum());
        assertEquals(2, response.getPageSize());
        assertEquals(2, response.getData().size());
        assertEquals("小蛋糕", response.getData().get(0).getName());
    }

    @Test
    void getCakeByCakeNameTest() {
        ArrayList<CakeWithTag> objects = new ArrayList<>();
        objects.add(getCakeWithTag());
        int pageNum = 1;
        int pageSize = 10;
        lenient().when(myCakeWithTagMapper.selectByNameLike("", pageSize, 0)).thenReturn(objects);
        PageResponse<CakeWithTag> cakeByCakeName = cakeService.getCakeByCakeName(pageNum, pageSize, "");

        assertEquals(pageNum, cakeByCakeName.getPageNum());
        assertEquals(pageSize, cakeByCakeName.getPageSize());
        assertEquals(1, cakeByCakeName.getData().size());
    }

    @Test
    void insertCakeSuccess() {
        Role admin = new Role();
        admin.setName("admin");
        when(roleService.getUserRoleById(anyLong())).thenReturn(admin);

        CakeWithTag cakeWithTag = getCakeWithTag();
        assertEquals(cakeWithTag, cakeService.insertCake(cakeWithTag, 1L));
    }

    @Test
    void insertCakeFail() {
        Role admin = new Role();
        admin.setName("normal");
        when(roleService.getUserRoleById(anyLong())).thenReturn(admin);

        CakeWithTag cakeWithTag = getCakeWithTag();
        assertThrows(HttpException.class, () -> cakeService.insertCake(cakeWithTag, 1L));
    }

    @Test
    void updateCake() {
        Role admin = new Role();
        admin.setName("admin");
        when(roleService.getUserRoleById(anyLong())).thenReturn(admin);

        CakeWithTag cakeWithTag = getCakeWithTag();
        assertEquals(cakeWithTag, cakeService.updateCake(cakeWithTag, 1L));
    }

    @Test
    void deleteCakeByCakeId() {
        Role admin = new Role();
        admin.setName("admin");
        when(roleService.getUserRoleById(anyLong())).thenReturn(admin);

        CakeWithTag cakeWithTag = getCakeWithTag();
        Cake cake = cakeService.deleteCakeByCakeId(cakeWithTag.getId(), 1L);
        assertEquals(cakeWithTag.getId(), cake.getId());
        assertTrue(cake.getDeleted());
    }

    public CakeWithTag getCakeWithTag() {
        var now = LocalDateTime.now();
        CakeWithTag cakeWithTag = new CakeWithTag(1L, "小蛋糕", "img", false, now, now);
        CakeTag cakeTag = new CakeTag();
        cakeTag.setId(1L);
        ArrayList<CakeTag> cakeTags = new ArrayList<>();
        cakeTags.add(cakeTag);
        cakeWithTag.setTags(cakeTags);
        return cakeWithTag;
    }
}