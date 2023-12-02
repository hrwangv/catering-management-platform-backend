package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    //查询用户
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    //新增员工信息
    @Insert("insert into employee (name,username,password,phone,sex,id_number,status,create_time,update_time,create_user,update_user)" +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void inset(Employee employee);//单表的新增操作


    //分页查询方法，不再使用注解方式写sql，动态SQL，写在映射配置文件中
    //前提是在配置文件application中已经写好扫描路径
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    //根据主键动态修改属性
    void update(Employee employee);

    //根据id查用户信息
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
