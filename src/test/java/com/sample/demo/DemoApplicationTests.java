package com.sample.demo;

import com.sample.demo.constant.MockConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	@Mock
	private RestTemplate restTemplate;

	@Test
	public void testMainMethod() {
		try (MockedStatic<SpringApplication> springApplicationMockedStatic =
					 mockStatic(SpringApplication.class)) {
			springApplicationMockedStatic
					.when(() -> SpringApplication.run(DemoApplication.class, new String[]{}))
					.thenReturn(null);
			DemoApplication.main(new String[]{});
			springApplicationMockedStatic
					.verify(() -> SpringApplication.run(DemoApplication.class, new String[]{}), times(1));
			assertEquals(MockConstants.WEATHER_APPLICATION, Thread.currentThread().getName());
		}
	}
}
