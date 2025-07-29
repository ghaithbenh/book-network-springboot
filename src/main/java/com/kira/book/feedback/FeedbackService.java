package com.kira.book.feedback;

import com.kira.book.book.Book;
import com.kira.book.book.BookRepository;
import com.kira.book.common.PageResponse;
import com.kira.book.exception.OperationNotPermittedException;
import com.kira.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FeedbackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for an archived or not shareable book");
        }

        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getCreatedBy(), user.getId())) {  // Fixed: compare with user.getId() instead of connectedUser.getName()
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);
        feedback.setBook(book);  // Set the book reference
        feedback.setCreatedBy(user.getId());  // Set the user who created the feedback

        return feedbackRepository.save(feedback).getId();  // Fixed: added parentheses for method call
    }

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());

        // Fixed: changed from FeedbackRepository.findAllByBookId to feedbackRepository.findAllByBookId
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);

        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}