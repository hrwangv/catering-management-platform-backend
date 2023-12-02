package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor //添加的有参构造器
@NoArgsConstructor  //添加无参构造器
public class PageResult implements Serializable {

    private long total; //总记录数

    private List records; //当前页数据集合

}
