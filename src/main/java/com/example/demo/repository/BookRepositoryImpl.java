package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Book;

@Repository // 專門負責 資料存取 的元件, Spring 會自動建立該物件並管理
public class BookRepositoryImpl implements BookRepository {
	// InMemory 版
	private List<Book> books = new CopyOnWriteArrayList<>();
	
	// 初始資料有4本書
	{
		books.add(new Book(1, "多拉A夢", 12.5, 20, true));
		books.add(new Book(2, "蠟筆小新", 10.5, 30, true));
		books.add(new Book(3, "名偵探柯南", 9.5, 40, true));
		books.add(new Book(4, "多拉B夢", 14.5, 0, false));
	}

	@Override
	public List<Book> findAllBooks() {
		return books;
	}

	@Override
	public Optional<Book> getBookById(Integer id) {
		return books.stream().filter(book -> book.getId().equals(id)).findFirst();
	}

	@Override
	public boolean addBook(Book book) {
		// 建立 newId
		OptionalInt optMaxId = books.stream().mapToInt(Book::getId).max();
		int newId = optMaxId.isEmpty() ? 1 : optMaxId.getAsInt() + 1;
		// 將 newId 設定給 book
		book.setId(newId);
		return books.add(book);
	}

	@Override
	public boolean updateBook(Integer id, Book book) {
		// 透過 id 找到要修改的書
		Optional<Book> optBook = getBookById(id);
		if(optBook.isEmpty()) {
			return false;
		}
		// 得到要修改的書
		Book orginalBook = optBook.get();
		// 更新欄位資料
		if(book.getAmount() != null) {
			orginalBook.setAmount(book.getAmount());
		}
		if(book.getName() != null) {
			orginalBook.setName(book.getName());
		}
		// java 8 以前寫法
		if(book.getPrice() != null) orginalBook.setPrice(book.getPrice());
		// java 8 以後寫法
		Optional.ofNullable(book.getPub()).ifPresent(orginalBook::setPub);
		
		return true;
	}

	@Override
	public boolean deleteBook(Integer id) {
		// 透過 id 找到要刪除的書
		Optional<Book> optBook = getBookById(id);
		if(optBook.isEmpty()) {
			return false;
		}
		// 移除書籍
		return books.remove(optBook.get());
	}

}
