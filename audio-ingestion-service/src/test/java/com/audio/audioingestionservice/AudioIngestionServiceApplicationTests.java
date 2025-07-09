package com.audio.audioingestionservice;

import com.audio.audioingestionservice.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestPropertySource(properties = {
		"spring.cloud.config.enabled=false",
		"spring.cloud.bootstrap.enabled=false",
		"spring.config.use-legacy-processing=true"
})
class AudioIngestionServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
