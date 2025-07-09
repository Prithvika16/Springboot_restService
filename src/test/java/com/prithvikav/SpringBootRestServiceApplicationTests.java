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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prithvika.SpringBootRestServiceApplication;
import com.prithvika.controller.AddResponse;
import com.prithvika.controller.Library;
import com.prithvika.controller.LibraryController;
import com.prithvika.repository.LibraryRepository;
import com.prithvika.service.LibraryService;

import java.util.ArrayList;

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

    @BeforeEach
    public void setup() {
        Library book = buildLibrary();
        when(libraryService.getBookById(book.getId())).thenReturn(book);
        when(repository.findAllByAuthor("Prithvika")).thenReturn(List.of(book, book));
    }

    @Test
    void contextLoads() {}

    @Test
    public void checkBuildIDLogic() {
        LibraryService lib = new LibraryService();
        String id = lib.buildId("ZMAN", 24);
        assertEquals("OLDZMAN24", id);

        String id1 = lib.buildId("MAN", 24);
        assertEquals("MAN24", id1);
    }

    @Test
    public void addBookTest() {
        Library lib = buildLibrary();
        when(libraryService.buildId(lib.getIsbn(), lib.getAisle())).thenReturn(lib.getId());
        when(libraryService.checkBookAlreadyExist(lib.getId())).thenReturn(false);
        when(repository.save(any())).thenReturn(lib);

        ResponseEntity<?> response = con.addBookImplementation(lib);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        AddResponse ad = (AddResponse) response.getBody();
        assertEquals(lib.getId(), ad.getId());
        assertEquals("Success Book is Added", ad.getMsg());
    }

    @Test
    public void addBookControllerTest() throws Exception {
        Library lib = buildLibrary();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(lib);

        when(libraryService.buildId(lib.getIsbn(), lib.getAisle())).thenReturn(lib.getId());
        when(libraryService.checkBookAlreadyExist(lib.getId())).thenReturn(false);
        when(repository.save(any())).thenReturn(lib);

        this.mockMvc.perform(post("/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(lib.getId()));
    }

    @Test
    public void updateBookTest() throws Exception {
        Library lib = buildLibrary();
        Library updated = UpdateLibrary();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(updated);

        when(libraryService.getBookById(lib.getId())).thenReturn(lib);
        when(repository.save(any())).thenReturn(updated);

        this.mockMvc.perform(put("/updateBook/" + lib.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.book_name").value("Boot"));
    }

    @Test
    public void getAuthorNameBooksTest() throws Exception {
        this.mockMvc.perform(get("/getBooks/author")
                .param("authorname", "Prithvika"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].author").value("Prithvika"));
    }

    @Test
    public void deleteBookControllerTest() throws Exception {
        when(libraryService.getBookById(any())).thenReturn(buildLibrary());
        doNothing().when(repository).delete(any());

        this.mockMvc.perform(delete("/deleteBook")
                .contentType(MediaType.APPLICATION_JSON)
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
        lib.setId("sfe3b");
        return lib;
    }
}
