package com.kira.book.book;

import com.kira.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer bookId
    ){
        return ResponseEntity.ok(service.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>>findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BoroowedBookResponse>>findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
    }
    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BoroowedBookResponse>>findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));
    }
    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer>updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.updateShareableStatus(bookId,connectedUser));
    }
    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer>updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId,connectedUser));
    }
    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer>BorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.borrowBook(bookId,connectedUser));
    }

@PatchMapping("/borrow/return/{book-id")
    public ResponseEntity<Integer> returnBorrowedBook(
        @PathVariable("book-id") Integer bookId,
        Authentication connectedUser
)
{
    return ResponseEntity.ok(service.returnBorrowedBook(bookId,connectedUser));
}
    @PatchMapping("/borrow/return/approve/{book-id")
    public ResponseEntity<Integer> approveReturnBorrowedBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId,connectedUser));
    }
    //upload a file
    @PostMapping(value = "/cover/{book-id}",consumes = "multipart/form-data")
    public ResponseEntity<?>uploadBookCoverPicture (
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    )
    {
        service.uploadBookCoverPicture(file,connectedUser,bookId);
        return ResponseEntity.accepted().build();
    }
}
