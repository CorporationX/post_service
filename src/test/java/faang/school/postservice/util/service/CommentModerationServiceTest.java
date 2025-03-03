package faang.school.postservice.util.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentModerationServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentModerationService moderationService;

    private Comment comment;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .content("Test content")
                .verified(false)
                .updatedAt(now)
                .verifiedDate(now.minusDays(1))
                .build();
    }

    @Test
    void testGetCommentsForModeration() {
        Page<Comment> page = new PageImpl<>(Collections.singletonList(comment));
        when(commentRepository.findCommentsForModeration(any(PageRequest.class))).thenReturn(page);

        Page<Comment> result = moderationService.getCommentsForModeration(0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(comment, result.getContent().get(0));
    }
}