package com.songdexv.nettyrpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.songdexv.nettyrpc")
public class NettyRpcApplication {

	public static void main(String[] args) {
		SpringApplication.run(NettyRpcApplication.class);
	}
}
