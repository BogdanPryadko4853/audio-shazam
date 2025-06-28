package com.audio.audioingestionservice;

import com.audio.audioingestionservice.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import(TestConfig.class)
class AudioIngestionServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
