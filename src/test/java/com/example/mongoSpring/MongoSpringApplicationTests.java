package com.example.mongoSpring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MongoSpringApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    public void testMainMethod() {
        String[] args = {};
        
        MongoSpringApplication.main(args);
        
    }
}
