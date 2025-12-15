package com.openclassrooms.starterjwt.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestConfig.class)
@SpringBootTest(classes = com.openclassrooms.starterjwt.SpringBootSecurityJwtApplication.class)
@AutoConfigureMockMvc

@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
}

