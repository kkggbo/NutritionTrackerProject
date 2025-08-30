package com.nt.recipe;

import com.nt.common.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication(scanBasePackages = {"com.nt.recipe", "com.nt.common"})
@MapperScan("com.nt.recipe.mapper")
public class RecipeApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecipeApplication.class, args);
	}

}
