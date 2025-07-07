package com.prithvika.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prithvika.repository.LibraryRepository;
import com.prithvika.service.LibraryService;

@RestController
public class LibraryController {

    @Autowired
    private LibraryRepository repository;

    @Autowired
    private LibraryService libraryService;

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @PostMapping("/addBook")
    public ResponseEntity<AddResponse> addBookImplementation(@RequestBody Library library) {
        String id = libraryService.buildId(library.getIsbn(), library.getAisle());
        AddResponse ad = new AddResponse();

        if (!libraryService.checkBookAlreadyExist(id)) {
            logger.info("Book does not exist, creating...");
            library.setId(id);
            repository.save(library);

            HttpHeaders headers = new HttpHeaders();
            headers.add("unique", id);

            ad.setMsg("Success Book is Added");
            ad.setId(id);
            return new ResponseEntity<>(ad, headers, HttpStatus.CREATED);
        } else {
            logger.info("Book already exists, skipping creation");
            ad.setMsg("Book already exist");
            ad.setId(id);
            return new ResponseEntity<>(ad, HttpStatus.ACCEPTED);
        }
    }

    @GetMapping("/getBooks/{id}")
    public Library getBookById(@PathVariable("id") String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getBooks/author")
    public List<Library> getBookByAuthorName(@RequestParam("authorname") String authorname) {
        return repository.findAllByAuthor(authorname);
    }

    @PutMapping("/updateBook/{id}")
    public ResponseEntity<Library> updateBook(@PathVariable("id") String id, @RequestBody Library library) {
        Library existingBook = libraryService.getBookById(id);
        existingBook.setAisle(library.getAisle());
        existingBook.setAuthor(library.getAuthor());
        existingBook.setBook_name(library.getBook_name());
        repository.save(existingBook);
        return new ResponseEntity<>(existingBook, HttpStatus.OK);
    }

    @DeleteMapping("/deleteBook")
    public ResponseEntity<String> deleteBookById(@RequestBody Library library) {
        Library libToDelete = libraryService.getBookById(library.getId());
        repository.delete(libToDelete);
        logger.info("Book is deleted");
        return new ResponseEntity<>("Book is deleted", HttpStatus.CREATED);
    }

    @GetMapping("/getBooks")
    public List<Library> getBooks() {
        return repository.findAll();
    }
}
