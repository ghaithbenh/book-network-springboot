package com.kira.book.book;


import com.kira.book.History.BookTransactionHistory;

import com.kira.book.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder().id(request.id()).title(request.title()).authorName(request.authorName()).synopsis(request.synopsis()).archived(false).shareable(request.shareable()).build();
    }

    public BookResponse toBookResponse( Book book) {
        return BookResponse.builder().id(book.getId()).title(book.getTitle()).authorName(book.getAuthorName()).isbn(book.getIsbn()).synopsis(book.getSynopsis()).rate(book.getRate()).archived(book.isArchived()).shareable(book.isShareable()).owner(book.getOwner().getFullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
        .build();
    }

    public BoroowedBookResponse toBoroowedBookResponse(BookTransactionHistory bookTransactionHistory) {
        return BoroowedBookResponse.builder().id(bookTransactionHistory.getId()).title(bookTransactionHistory.getBook().getTitle())
                .authorName(bookTransactionHistory.getBook().getAuthorName())
                .isbn(bookTransactionHistory.getBook().getIsbn())
                .rate(bookTransactionHistory.getBook().getRate())
                .returned(bookTransactionHistory.isReturned())
                .returnApproved(bookTransactionHistory.isReturnApproved())
                .build();
    }
}
