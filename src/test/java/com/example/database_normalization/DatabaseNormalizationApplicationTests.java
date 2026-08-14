package com.example.database_normalization;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DatabaseNormalizationApplicationTests {
	
	@Container
	@ServiceConnection
	static MySQLContainer mysql = new MySQLContainer("mysql:9.7");

	@Test
	void contextLoads() {
	}

}
