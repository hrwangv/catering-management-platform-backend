package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */

    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对 -已完成
        //密码MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //判断账号是否被锁定
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        // 将dto转化成实体，持久层推荐用实体类
        Employee employee = new Employee();
        //employee.setName(employeeDTO.getName());
        //使用对象属性拷贝，简化代码量，将employdto属性拷贝到实体属性
        BeanUtils.copyProperties(employeeDTO,employee);//使用这个方法的前提是拷贝两方的属性名一定一样

        //设定剩余属性
        //状态正常
        employee.setStatus(StatusConstant.ENABLE);//不要直接写1，硬编码不适合
        //设置密码，默认
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        //设置创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //创建人和修改人的id
//        //TODO: 后期需要修改成当前操作用户的id -已完成
//
//        employee.setUpdateUser(BaseContext.getCurrentId());
//        employee.setCreateUser(BaseContext.getCurrentId());

        employeeMapper.inset(employee);
    }

    @Override
    public PageResult querypage(EmployeePageQueryDTO employeePageQueryDTO) {
        //select * from employee limit 0,10
        //开始分页查询,使用pagehelper插件，实际上插件在后台做的也是字符串的拼接
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        //插件要求返回类型必须为Page类型
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        //将page值拿出来封装到我们规定的格式PageResult
        long total = page.getTotal();
        List<Employee> records =  page.getResult();

        return new PageResult(total,records);


    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //update employee set status = ? where id = ?
        Employee employee = Employee.builder(). //通过构建器创建对象
                                    status(status).
                                    id(id).
                                    build();


        employeeMapper.update(employee); //传入实体对象，根据实体对象的属性修改，功能更多，而不仅仅根据两个参数
    }


    //查询员工信息
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        //处理密码
        employee.setPassword("******");
        return employee;
    }

    //新增员工信息
    @Override
    public void update(EmployeeDTO employeeDTO) {
        //可以重复使用mapper层的update方法，但是需要将update方法传过来的employee对象转化成我们需要的DTO对象
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

//        employee.setUpdateTime(LocalDateTime.now()); //设置DTO没有的更新时间和更新user
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);

    }

}
