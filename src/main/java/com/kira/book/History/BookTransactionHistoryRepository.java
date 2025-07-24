package com.kira.book.History;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, @Param("userId") Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, @Param("userId") Integer userId);

    @Query("""
            SELECT (COUNT(*) > 0) 
            FROM BookTransactionHistory bookTransactionHistory 
            WHERE bookTransactionHistory .book.id = :bookId 
            AND bookTransactionHistory .user.id = :userId 
            AND bookTransactionHistory .returnApproved = false
            """)
     boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);
}