package com.prithvikav;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvika.SpringBootRestServiceApplication;
import com.prithvika.controller.AddResponse;
import com.prithvika.controller.Library;
import com.prithvika.controller.LibraryController;
import com.prithvika.repository.LibraryRepository;
import com.prithvika.service.LibraryService;

@SpringBootTest(classes = SpringBootRestServiceApplication.class)
@AutoConfigureMockMvc
class SpringBootRestServiceApplicationTests {

    @Autowired
    private LibraryController controller;

    @MockBean
    private LibraryRepository repository;

    @MockBean
    private LibraryService libraryService;

    @Autowired
    private MockMvc mockMvc;

    private Library sampleBook;

    @BeforeEach
    public void setup() {
        sampleBook = buildLibrary();
        when(libraryService.getBookById(sampleBook.getId())).thenReturn(sampleBook);
        when(repository.findAllByAuthor("Prithvika")).thenReturn(List.of(sampleBook, sampleBook));
    }

    @Test
    void contextLoads() {}

    @Test
    public void checkBuildIdLogic() {
        LibraryService libService = new LibraryService();
        assertEquals("OLDZMAN24", libService.buildId("ZMAN", 24));
        assertEquals("MAN24", libService.buildId("MAN", 24));
    }

    @Test
    public void addBookDirectTest() {
        when(libraryService.buildId(sampleBook.getIsbn(), sampleBook.getAisle())).thenReturn(sampleBook.getId());
        when(libraryService.checkBookAlreadyExist(sampleBook.getId())).thenReturn(false);
        when(repository.save(any())).thenReturn(sampleBook);

        ResponseEntity<?> response = controller.addBookImplementation(sampleBook);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        AddResponse responseBody = (AddResponse) response.getBody();
        assertEquals(sampleBook.getId(), responseBody.getId());
        assertEquals("Success Book is Added", responseBody.getMsg());
    }

    @Test
    public void addBookApiTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String bookJson = mapper.writeValueAsString(sampleBook);

        when(libraryService.buildId(sampleBook.getIsbn(), sampleBook.getAisle())).thenReturn(sampleBook.getId());
        when(libraryService.checkBookAlreadyExist(sampleBook.getId())).thenReturn(false);
        when(repository.save(any())).thenReturn(sampleBook);

        mockMvc.perform(post("/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(sampleBook.getId()));
    }

    @Test
    public void updateBookTest() throws Exception {
        Library updatedBook = buildUpdatedLibrary();
        ObjectMapper mapper = new ObjectMapper();
        String bookJson = mapper.writeValueAsString(updatedBook);

        when(repository.save(any())).thenReturn(updatedBook);

        mockMvc.perform(put("/updateBook/" + sampleBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.book_name").value("Boot"));
    }

    @Test
    public void getBooksByAuthorTest() throws Exception {
        mockMvc.perform(get("/getBooks/author")
                .param("authorname", "Prithvika"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].author").value("Prithvika"));
    }

    @Test
    public void deleteBookTest() throws Exception {
        doNothing().when(repository).delete(any());

        mockMvc.perform(delete("/deleteBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\" : \"sfe3b\"}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andExpect(content().string("Book is deleted"));
    }

    private Library buildLibrary() {
        Library lib = new Library();
        lib.setAisle(322);
        lib.setBook_name("Spring");
        lib.setIsbn("sfe");
        lib.setAuthor("Prithvika");
        lib.setId("sfe3b");
        return lib;
    }

    private Library buildUpdatedLibrary() {
        Library lib = new Library();
        lib.setAisle(322);
        lib.setBook_name("Boot");
        lib.setIsbn("rain");
        lib.setAuthor("Prithvika");
        lib.setId("sfe3b");
        return lib;
    }
}
