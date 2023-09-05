package faang.school.postservice.service;


import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentEventPublisherForAnalytics;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentEventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventService {
    private final CommentRepository commentRepository;
    private final CommentEventValidator commentEventValidator;
    private final CommentEventMapper commentEventMapper;
    private final CommentEventPublisherForAnalytics commentEventPublisher;

    @Transactional
    public CommentEventDto createCommentEvent(long postId, CommentEventDto commentEventDto) {
        commentEventValidator.validateBeforeCreate(commentEventDto);
        Comment comment = commentEventMapper.toCommentEntity(commentEventDto, postId);

        CommentEventDto savedEventDto = commentEventMapper.toCommentDto(commentRepository.save(comment));
        commentEventPublisher.publish(CommentEventDto.builder()
                .postId(commentEventDto.getPostId())
                .authorId(commentEventDto.getAuthorId())
                .commentId(commentEventDto.getCommentId())
                .date(LocalDateTime.now().withNano(0))
                .build());
        return savedEventDto;
    }
}