package com.incapp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.incapp.entity.Book;

@Repository
public interface BookRepo extends JpaRepository<Book, String> {
//	List<Book> findAllByNameLike(String name);//allowed
	List<Book> findAllByNameLike(@Param("name") String name);
//	List<Book> findAllByNameLike(@Param("name") String n);//can change variable name
	List<Book> findAllByNameContains(String name);
	List<Book> findAllByAname(String aname);
	
	@Query("select b from Book b where b.price > :price and b.aname like :aname")
//	List<Book> getMyBooks(int price, String aname); //allowed
	List<Book> getMyBooks(@Param("price") int p, @Param("aname") String aname);
}
