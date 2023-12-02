package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    void addEmployee(EmployeeDTO employeeDTO);//接口中所有的方法都是public方法，因此不需要声明

    /**
     * 员工分页
     * @param employeePageQueryDTO
     * @return
     */
    PageResult querypage(EmployeePageQueryDTO employeePageQueryDTO);

    //启用禁用员工账号
    void startOrStop(Integer status, Long id);

    //根据id查员工
    Employee getById(Long id);

    void update(EmployeeDTO employeeDTO);
}
