package com.vdda.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles(profiles = "dev")
public class CategoryControllerTests {

//	@Autowired
//	private MockMvc mvc;
//
//	@Test
//	public void getCategories() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/categories").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	public void getCategory() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.get("/categories/1").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	public void createCategory() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/categories").content("{\"description\":\"testing\"}").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	public void updateCategorySuccess() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/categories/1").content("{\"description\":\"overridden\"}").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	public void updateCategoryFail() throws Exception {
//		mvc.perform(MockMvcRequestBuilders.post("/categories/999").content("{\"description\":\"overridden\"}").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().is4xxClientError());
//	}
}
