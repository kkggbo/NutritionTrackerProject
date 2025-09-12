package com.nt.reward;


import com.nt.common.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
@MapperScan("com.nt.reward.mapper")
public class RewardApplication {

	public static void main(String[] args) {
		SpringApplication.run(RewardApplication.class, args);
	}

}
