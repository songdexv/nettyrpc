package com.songdexv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.songdexv.nettyrpc.NettyRpcApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NettyRpcApplication.class})
public class NettyRpcApplicationTests {

	@Test
	public void contextLoads() {
	}

}
