package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.redis.settings.maxSizeComment}")
    private int maxSizeComment;

    @Transactional(readOnly = true)
    public Set<String> getTheLastCommentsForNewsFeed(Long postId) {
        List<CommentFeedDto> commentFeedDtosList = commentRepository.findLastLimitComment(postId, maxSizeComment)
                .stream().map(commentMapper::toFeedDto).toList();

        return commentFeedDtosList.stream().map(comment -> {
            try {
                return objectMapper.writeValueAsString(userServiceClient.getUserByPostId(postId));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }).collect(Collectors.toSet());
    }
}
