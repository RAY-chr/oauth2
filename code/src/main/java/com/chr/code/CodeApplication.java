package com.chr.code;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeApplication.class, args);
	}


	@Bean
	Redisson redisson(){
		Config config = new Config();
		//声明redisso对象
		Redisson redisson = null;
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		//得到redisson对象
		redisson = (Redisson) Redisson.create(config);
		return redisson;
	}



}
