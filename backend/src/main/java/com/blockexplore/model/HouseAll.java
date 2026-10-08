package com.blockexplore.model;

import lombok.Data;

@Data
public class HouseAll {
    private RightCommonInfo rightCommonInfo;
    private RightAddress rightAddress;
    private HouseDetail houseDetail;
}
