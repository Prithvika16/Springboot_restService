package com.prithvika.repository;

import java.util.List;

import com.prithvika.controller.Library;

public interface LibraryRepositoryCustom {
	
	List<Library> findAllByAuthor(String authorName);

}
