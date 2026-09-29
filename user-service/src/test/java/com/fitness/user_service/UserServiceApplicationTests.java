package com.fitness.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApplicationTests {
	@Autowired
	private MockMvc mvc;

	@Test
	void contextLoads() {
	}

	@Test
	void openApiDocumentsUserEndpoints() throws Exception {
		mvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Fitness User Service API"))
				.andExpect(jsonPath("$.paths['/api/users']").exists())
				.andExpect(jsonPath("$.paths['/api/users/register']").exists())
				.andExpect(jsonPath("$.paths['/api/users/{userId}/password']").exists())
				.andExpect(jsonPath("$.components.schemas.UserUpdateRequest.properties.email").doesNotExist())
				.andExpect(jsonPath("$.components.schemas.UserUpdateRequest.properties.password").doesNotExist());
	}

	@Test
	void swaggerUiIsAvailable() throws Exception {
		mvc.perform(get("/swagger-ui.html"))
				.andExpect(status().is3xxRedirection());
	}

}
