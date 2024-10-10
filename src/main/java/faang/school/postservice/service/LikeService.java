package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.like.LikeAddedEvent;
import faang.school.postservice.kafka.Producer;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private static final String MESSAGE_POST_NOT_IN_DB = "Post is not in the database";
    private static final String MESSAGE_ALREADY_LIKED = "Already liked";
    private static final String MESSAGE_COMMENT_ABSENT = "Comment with this Id absent";
    private static final String MESSAGE_LIKE_NOT_PRESENT = "Like is not present";

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final LikeMapper mapper;
    private final Producer kafkaProducer;
    @Value("${spring.kafka.topic.like.added}")
    private String likeAddedTopic;

    public LikeDto addPostLike(Long postId, LikeDto dto) {
        Post post = validateUserAndGetPost(postId, dto);
        if (isPostLikePresent(post, dto)) {
            throw new RuntimeException(MESSAGE_ALREADY_LIKED);
        }
        Like like = mapper.toEntity(dto);
        like.setPost(post);
        kafkaProducer.send(likeAddedTopic, new LikeAddedEvent(postId));
        return mapper.toDto(likeRepository.save(like));
    }

    public LikeDto deletePostLike(Long postId, LikeDto dto) {
        Post post = validateUserAndGetPost(postId, dto);
        if (!isPostLikePresent(post, dto)) {
            throw new RuntimeException(MESSAGE_LIKE_NOT_PRESENT);
        }
        Like like = mapper.toEntity(dto);
        likeRepository.deleteById(like.getId());
        return mapper.toDto(like);
    }

    public LikeDto addCommentLike(Long postId, Long commentId, LikeDto dto) {
        Comment postComment = validateAndGetComment(postId, commentId, dto);
        if (isCommentLikePresent(postComment, dto)) {
            throw new RuntimeException(MESSAGE_ALREADY_LIKED);
        }
        Like like = mapper.toEntity(dto);
        like.setComment(postComment);
        return mapper.toDto(likeRepository.save(like));
    }

    public LikeDto deleteCommentLike(Long postId, Long commentId, LikeDto dto) {
        Comment postComment = validateAndGetComment(postId, commentId, dto);
        if (!isCommentLikePresent(postComment, dto)) {
            throw new RuntimeException(MESSAGE_LIKE_NOT_PRESENT);
        }
        Like like = mapper.toEntity(dto);
        likeRepository.deleteById(like.getId());
        return mapper.toDto(like);
    }

    private Post validateUserAndGetPost(Long postId, LikeDto dto) {
        userServiceClient.getUser(dto.getUserId());
        return getPost(postId);
    }

    private Comment validateAndGetComment(Long postId, Long commentId, LikeDto dto) {
        Post post = validateUserAndGetPost(postId, dto);
        return post.getComments().stream()
                .filter(comment -> commentId.equals(comment.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(MESSAGE_COMMENT_ABSENT));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException(MESSAGE_POST_NOT_IN_DB));
    }

    private boolean isCommentLikePresent(Comment postComment, LikeDto dto) {
        return postComment.getLikes().stream().anyMatch(like -> like.getUserId().equals(dto.getUserId()));
    }

    private boolean isPostLikePresent(Post post, LikeDto dto) {
        return post.getLikes().stream().anyMatch(like -> like.getUserId().equals(dto.getUserId()));
    }
}
