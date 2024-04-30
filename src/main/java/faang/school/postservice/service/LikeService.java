package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeAddEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.publisher.kafka.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Value("${spring.kafka.topics.likes}")
    private String likesTopic;

    @Transactional
    public void addLike(LikeDto likeDto) {
        Comment comment = getComment(likeDto.commentId());
        Post post = getPost(likeDto.postId());

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        like.setPost(post);

        LikeAddEvent likeAddEvent = new LikeAddEvent(
                like.getId(),
                like.getUserId(),
                like.getPost().getId(),
                like.getCreatedAt());

        likeRepository.save(like);
        kafkaEventPublisher.sendEvent(likesTopic, likeAddEvent);
    }

    public Like getLike(long likeId) {
        return likeRepository.findById(likeId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Like not found by Id: %d", likeId)
                )
        );
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Post not found by Id: %d", postId)
                        )
                );
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Comment not found by Id: %d", commentId)
                        )
                );
    }
}
