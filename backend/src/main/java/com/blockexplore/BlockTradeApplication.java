package com.blockexplore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.blockexplore","cn.edu.cjxy.iotlink"})
@MapperScan({"com.blockexplore.mapper", "cn.edu.cjxy.iotlink.mapper"})
public class BlockTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlockTradeApplication.class, args);
    }

}
