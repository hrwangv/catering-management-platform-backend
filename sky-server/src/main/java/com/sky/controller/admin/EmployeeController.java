package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId()); //将员工id放在一个hashmap中，key是常量字符串，value是要登陆的员工id
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(), //生成jwt的算法
                jwtProperties.getAdminTtl(), //jwt有效时间
                claims); //请求体，携带信息

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder() //通过builder方式创建对象，前提是类前必须加注解
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     *
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    //新增模块-新增员工
    /**
     * 新增员工
     * @param employeeDTO
     *
     * @return
     *
     */
    @ApiOperation(value = "新增员工")
    @PostMapping("")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}",employeeDTO); //花括号是占位符
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }


    //新增模块，查询员工并分页
    //数据格式query类型，而不是json类型，不需要加requestbody注解
    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageResult> querypage(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.querypage(employeePageQueryDTO);
        return Result.success(pageResult);
    }


    //启用禁用员工账号
    @PostMapping("status/{status}") //路径参数方式传递
    @ApiOperation(value = "启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, Long id) { //路径传参
        log.info("启用禁用员工账号{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }


    //根据id查询
    @GetMapping("/{id}") //这里的id一定要加花括号，表示路径传参
    @ApiOperation("根据员工id查询")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    //修改员工数据
    @PutMapping
    @ApiOperation(value = "编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO) { //使用和新增员工相同的DTO
        log.info("编辑员工信息{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }


}
