package com.blockexplore.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MoneyAreaDTO {
    private String transKind;
    private Integer sumSuccessPrice;  // 或 Integer/Double
    private Integer area;
    private Integer count;
}
