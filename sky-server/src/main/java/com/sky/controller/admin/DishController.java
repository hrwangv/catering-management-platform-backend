package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //新增菜品按钮
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
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

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) { //路劲参数
        log.info("根据id查询菜品，{}",id);
        DishVO dishVo = dishService.getByIdWithFlavor(id);

        return Result.success(dishVo);
    }


    @PutMapping("")
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品，{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);


        return Result.success(dishDTO);
    }

    @PostMapping("status/{status}")
    @ApiOperation("设置菜品起售状态")
    public Result setStatus(@PathVariable Integer status , Long id) {
        log.info("设置id{}起售状态{}",id,status);
        dishService.setStatus(id ,status);
        return Result.success();
    }

}
