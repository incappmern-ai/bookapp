package com.incapp.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.incapp.entity.Book;
import com.incapp.entity.User;
import com.incapp.repo.BookRepo;
import com.incapp.repo.UserRepo;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BookController {
	
	@Autowired
	private BookRepo bookRepo;
	@Autowired
	private UserRepo userRepo;
	
	@RequestMapping("/")
	public String home() {
//		return "index.html"; 
		return "index"; //.html is optional
	}
	
	@PostMapping("/SearchBook")
	public String searchBook(@RequestParam String name, Model m) {
//		List<Book> b=bookRepo.findAllByNameLike("%"+name+"%");
		List<Book> b=bookRepo.findAllByNameContains(name);
		if(!b.isEmpty()) {
			m.addAttribute("books", b);
			return "PrintBooks";
		}else {
			m.addAttribute("msg", "Book Not Found!");
			return "index";
		}
	}
	@PostMapping("/SearchBook2")
	public String searchBook2(@RequestParam String aname, Model m) {
		List<Book> b=bookRepo.findAllByAname(aname);
		if(!b.isEmpty()) {
			m.addAttribute("books", b);
			return "PrintBooks";
		}else {
			m.addAttribute("msg", "Book Not Found!");
			return "index";
		}
	}
	@PostMapping("/SearchBook3")
	public String searchBook3(@RequestParam int price,@RequestParam String aname, Model m) {
		List<Book> b=bookRepo.getMyBooks(price, "%"+aname+"%");
		if(!b.isEmpty()) {
			m.addAttribute("books", b);
			return "PrintBooks";
		}else {
			m.addAttribute("msg", "Book Not Found!");
			return "index";
		}
	}
	@GetMapping("/GetImage")
	public void getImage(@RequestParam String name, HttpServletResponse response ) throws IOException {
		Book b=bookRepo.findById(name).orElse(null);
		byte[] image=null;
		if(b.getImage()!=null) {
			image=b.getImage();
		}else {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/book.png");
			image=is.readAllBytes();
		}
		response.getOutputStream().write(image);
	}
	@GetMapping("/GetImage2")
	public void getImage2(@RequestParam String name, HttpServletResponse response ) throws IOException {
		Book b=bookRepo.findById(name).orElse(null);
		byte[] image=b.getImage();
		response.getOutputStream().write(image);
	}
	@RequestMapping("/BookDetails")
	public String bookDetails(@RequestParam String name,Model m) {
		Book b=bookRepo.findById(name).orElse(null);
		m.addAttribute("book", b);
		return "BookDetails";
	}
	@GetMapping("/GetPdf")
	public void getPdf(@RequestParam String name, HttpServletResponse response ) throws IOException {
		Book b=bookRepo.findById(name).orElse(null);
		byte[] pdf=b.getContent();
		response.getOutputStream().write(pdf);
	}
	@PostMapping("/DownloadPdf")
	public void downloadPdf(@RequestParam String name, HttpServletResponse response ) throws IOException {
		Book b=bookRepo.findById(name).orElse(null);
		byte[] pdf=b.getContent();
		response.setHeader("Content-Disposition","attachment; filename=" + name+".pdf" );
		response.getOutputStream().write(pdf);
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	} 
	@PostMapping("/Login")
	public String login(@ModelAttribute User u, Model m, HttpSession ses) {
		User user=userRepo.findById(u.getId()).orElse(null);
		if(user==null || !user.getPassword().equals(u.getPassword())) {
			m.addAttribute("error", "Invaild Credentials!");
			return "login";
		}else {
			ses.setAttribute("userName", user.getName());
			return "redirect:/UserHome";
		}
	}
	@GetMapping("/UserHome")
	public String userHome(HttpSession ses, Model m) {
		String userName=(String)ses.getAttribute("userName");
		if(userName==null) {
			m.addAttribute("error", "Please Login First!");
			return "login";
		}
		return "UserHome";
	}
	@GetMapping("/Logout")
	public String logout(HttpSession ses, Model m) {
		String userName=(String)ses.getAttribute("userName");
		if(userName==null) {
			m.addAttribute("error", "Please Login First!");
		}
		ses.invalidate();
		return "login";
	} 
//	Why can't we get image or pdf (as byte[]) directly inside a @ModelAttribute Book?
/*
Spring's @ModelAttribute mechanism binds form fields to Java object properties only when the field values are plain text (like String, int, etc.).
But in our case:
	-We’re uploading files, not plain text only.
	-Spring doesn't automatically convert a file upload into a byte[] for a property like private byte[] image; in our Book class.
	-MultipartFile is not the same as byte[].
*/
	@PostMapping("/AddBook")
	public String addBook(@ModelAttribute Book b,@RequestPart MultipartFile ctn,@RequestPart MultipartFile cImage,Model m) throws IOException {
		Book book=bookRepo.findById(b.getName()).orElse(null);
		if(book==null) {
			byte[] c=ctn.getBytes();
			byte[] ci=cImage.getBytes();
			if(c.length==0) {
				c=null;
			}
			if(ci.length==0) {
				ci=null;
			}
			b.setImage(ci);
			b.setContent(c);
			bookRepo.save(b);
			m.addAttribute("msg", "Book Added Successfully!");
		}else {
			m.addAttribute("msg", "Book Already Exist!");
		}
		return "UserHome";
	}
}
