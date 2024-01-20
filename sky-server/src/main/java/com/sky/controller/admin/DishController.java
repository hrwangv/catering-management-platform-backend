package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @Author : haoranwang
 * @Date :
 * @Description : 菜品管理
 * @Version :
 */

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate; //注入要使用的redis

    //新增菜品按钮
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //新增菜品之后，改变缓存数据
        String keyNeedtoDelete = "dish_" + dishDTO.getCategoryId();
        cleanCache(keyNeedtoDelete);

        return Result.success(dishDTO);
    }

    //菜品查询回显
    @GetMapping("/page")
    @ApiOperation("菜品分类查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) { //
        log.info("菜品分类查询{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    //删除菜品
    @DeleteMapping
    @ApiOperation("菜品的批量删除")
    public Result delete(@RequestParam List<Long> ids) { //在地址栏中传参，要删除的菜品序号由逗号隔开
        log.info("菜品的批量删除，{}",ids);
        dishService.deleteBatch(ids);
        //要删除的菜品可能属于多个套餐，需要查询菜品对应的套餐
        //为了减少一次查询，直接删除所有菜品关联的套餐
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) { //路径参数
        log.info("根据id查询菜品，{}",id);
        DishVO dishVo = dishService.getByIdWithFlavor(id);

        return Result.success(dishVo);
    }


    @PutMapping("")
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品，{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //修改时，情况也比较复杂，需要分类，这里也直接删除所有缓存数据
//        Set keys = redisTemplate.keys("dish_*");//模糊查询所有菜品关联套餐的key
//        redisTemplate.delete(keys);
        cleanCache("dish_*");
        return Result.success(dishDTO);
    }

    @PostMapping("status/{status}")
    @ApiOperation("设置菜品起售状态")
    public Result setStatus(@PathVariable Integer status , Long id) {
        log.info("设置id{}起售状态{}",id,status);
        dishService.setStatus(id ,status);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    //将清理缓存的共同代码提取出来
    private void cleanCache(String patten) {
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);

    }

}
