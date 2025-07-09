package com.prithvikav;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.prithvika.SpringBootRestServiceApplication;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvika.controller.AddResponse;
import com.prithvika.controller.Library;
import com.prithvika.controller.LibraryController;
import com.prithvika.repository.LibraryRepository;
import com.prithvika.service.LibraryService;

@SpringBootTest(classes = SpringBootRestServiceApplication.class)
@AutoConfigureMockMvc
class SpringBootRestServiceApplicationTests {

	@Autowired
	LibraryController con;

	@MockBean
	LibraryRepository repository;

	@MockBean
	LibraryService libraryService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {}

	@Test
	public void checkBuildIDLogic() {
		LibraryService lib = new LibraryService();
		String id = lib.buildId("ZMAN", 24);
		assertEquals(id, "OLDZMAN24");
		String id1 = lib.buildId("MAN", 24);
		assertEquals(id1, "MAN24");
	}

	@Test
	public void addBookTest() {
		Library lib = buildLibrary();
		when(libraryService.buildId(lib.getIsbn(), lib.getAisle())).thenReturn(lib.getId());
		when(libraryService.checkBookAlreadyExist(lib.getId())).thenReturn(false);
		when(repository.save(any())).thenReturn(lib);

		ResponseEntity response = con.addBookImplementation(buildLibrary());
		System.out.println(response.getStatusCode());

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		AddResponse ad = (AddResponse) response.getBody();
		assertEquals(lib.getId(), ad.getId());
		assertEquals("Success Book is Added", ad.getMsg());
	}

	@Test
	public void addBookControllerTest() throws Exception {
		Library lib = buildLibrary();
		ObjectMapper map = new ObjectMapper();
		String jsonString = map.writeValueAsString(lib);

		when(libraryService.buildId(lib.getIsbn(), lib.getAisle())).thenReturn(lib.getId());
		when(libraryService.checkBookAlreadyExist(lib.getId())).thenReturn(false);
		when(repository.save(any())).thenReturn(lib);

		this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(lib.getId()));
	}

	
//	@Test
//	public void updateBookTest() throws Exception {
//		Library lib = buildLibrary();
//		ObjectMapper map = new ObjectMapper();
//		String jsonString = map.writeValueAsString(UpdateLibrary());
//
//		when(libraryService.getBookById(any())).thenReturn(buildLibrary());
//
//		this.mockMvc.perform(put("/updateBook/" + lib.getId()).contentType(MediaType.APPLICATION_JSON)
//				.content(jsonString))
//			.andDo(print())
//			.andExpect(status().isOk())
//			.andExpect(content().json("{\"book_name\":\"Boot\",\"id\":\"sfe3b\",\"isbn\":\"sfe\",\"aisle\":322,\"author\":\"Prithvika\"}"));
//	}
	
//	@Test
//	public void getAuthorNameBooksTest() throws Exception {
//	    this.mockMvc.perform(get("/getBooks/author").param("authorname", "Prithvika"))
//	        .andExpect(status().isOk())
////	        .andExpect(jsonPath("$.length()", is(1))) // Maybe more than 1 returned
////	        .andExpect(jsonPath("$[0].aisle").value(322)); // This might fail
//	}


	@Test
	public void deleteBookControllerTest() throws Exception {
		when(libraryService.getBookById(any())).thenReturn(buildLibrary());
		doNothing().when(repository).delete(buildLibrary());

		this.mockMvc.perform(delete("/deleteBook").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\" : \"sfe3b\"}"))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().string("Book is deleted"));
	}

	

	public Library buildLibrary() {
		Library lib = new Library();
		lib.setAisle(322);
		lib.setBook_name("Spring");
		lib.setIsbn("sfe");
		lib.setAuthor("Prithvika");
		lib.setId("sfe3b");
		return lib;
	}

	public Library UpdateLibrary() {
		Library lib = new Library();
		lib.setAisle(322);
		lib.setBook_name("Boot");
		lib.setIsbn("rain");
		lib.setAuthor("Prithvika");
		lib.setId("rain322");
		return lib;
	}
}
