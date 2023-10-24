package com.zsp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: created by zsp on 2023/10/23 0023 15:34
 */
@Getter
@Setter
@TableName("tb_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderCode;

    private int status;

    private String name;

    private double price;

    private int deleteFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createUserCode;

    private String updateUserCode;

    private int version;

    private String remark;

}
