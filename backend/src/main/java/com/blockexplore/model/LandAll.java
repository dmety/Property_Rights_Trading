package com.blockexplore.model;

import lombok.Data;

@Data
public class LandAll {
    private RightCommonInfo rightCommonInfo; // 公共信息
    private FourAddr fourAddr; // 四至信息
    private RightAddress rightAddress; // 坐落信息
    private LandDetail landDetail; // 专有信息
}
