package com.prithvika.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.prithvika.controller.Library;

public class LibraryRepositoryImpl implements LibraryRepositoryCustom{
	
	@Autowired
	LibraryRepository repository;

	@Override
	public List<Library> findAllByAuthor(String authorName) {
		List<Library>bookswithAuthor = new ArrayList<Library>();
		// TODO Auto-generated method stub
		List<Library>books =repository.findAll();
		for(Library item : books)
//			{
	if(item.getAuthor().equalsIgnoreCase(authorName))
	{
		bookswithAuthor.add(item);
	}
//			}
		
		return bookswithAuthor;
	}
	
	

}
