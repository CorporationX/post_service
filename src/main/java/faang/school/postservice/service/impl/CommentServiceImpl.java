package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.event.application.CommentCommittedEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper mapper;
    private final UserServiceClient userServiceClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId) {
        validator.validatePostExist(commentDto.getPostId());
        validator.validateCommentContent(commentDto.getContent());
        Comment comment = mapper.mapToComment(commentDto);
        CommentDto savedCommentDto = mapper.mapToCommentDto(commentRepository.save(comment));
        applicationEventPublisher.publishEvent(new CommentCommittedEvent(savedCommentDto));
        return savedCommentDto;
    }

    @Override
    public List<CommentDto> getComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<Comment> commentsSorted = comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
        return mapper.mapToCommentDto(commentsSorted);
    }

    @Override
    public void deleteComment(Long commentId) {
        validator.validateCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId) {
        validator.validateCommentExist(commentId);
        validator.validateCommentContent(commentDto.getContent());
        userServiceClient.getUser(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.setContent(commentDto.getContent());
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public int getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public List<CommentDto> getRecentComments(Long postId, int numberOfComments) {
        Pageable pageable = PageRequest.of(0, numberOfComments);
        List<Comment> comments = commentRepository.findRecentByPostId(postId, pageable);
        return mapper.mapToCommentDto(comments);
    }

    @Override
    public Map<Long, List<CommentDto>> getTop3CommentsForPosts(List<Long> postIds) {
        List<Object[]> rows = commentRepository.findTop3CommentsPerPost(postIds);

        Map<Long, List<CommentDto>> result = new HashMap<>();
        postIds.forEach(postId -> result.put(postId, new ArrayList<>()));

        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            Long postId = ((Number) row[1]).longValue();
            String content = (String) row[2];
            Long authorId = ((Number) row[3]).longValue();
            Instant instant = (Instant) row[4];
            LocalDateTime createdAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


            CommentDto dto = new CommentDto();
            dto.setId(id);
            dto.setPostId(postId);
            dto.setContent(content);
            dto.setAuthorId(authorId);
            dto.setCreatedAt(createdAt);

            result.get(postId).add(dto);
        }

        return result;
    }

    @Override
    public Map<Long, List<Long>> getTop3CommentsAuthorIds(List<Long> postIds) {
        List<Object[]> rows = commentRepository.findTop3CommentsAuthorIdsPerPost(postIds);

        Map<Long, List<Long>> result = new HashMap<>();
        postIds.forEach(postId -> result.put(postId, new ArrayList<>()));

        for (Object[] row : rows) {
            Long postId = ((Number) row[0]).longValue();
            Long authorId = ((Number) row[1]).longValue();
            result.get(postId).add(authorId);
        }
        return result;
    }

    @Override
    public Map<Long, Integer> getPostIdCommentCountMap(List<Long> postIds) {
        Map<Long, Integer> postIdCommentCountMap = postIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> 0));

        commentRepository.findCommentCountsByPostIds(postIds).forEach(commentCount ->
                postIdCommentCountMap.put(commentCount.getPostId(), commentCount.getCount().intValue()));

        return postIdCommentCountMap;
    }
}
