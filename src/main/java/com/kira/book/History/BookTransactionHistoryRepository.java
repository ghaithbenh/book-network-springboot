package com.kira.book.History;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


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

    @Query("""
            SELECT Transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.user.id=:userId
            AND transaction.book.id=:bookId
            AND transaction.returned=false
            AND transaction.returnedApproved=false
            """)
  Optional <BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);
    @Query("""
            SELECT Transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.owner.id=:userId
            AND transaction.book.id=:bookId
            AND transaction.returned=true
            AND transaction.returnedApproved=false
            """)

    Optional<BookTransactionHistory> findByBookIdOwnerId(Integer bookId, Integer id);
}