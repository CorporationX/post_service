package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikePostCreateDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.aop.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikePostService {

    private final LikeEventPublisher likeEventPublisher;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public LikePostCreateDto likePost(LikePostCreateDto likePostCreateDto) {
        log.info("Attempting to like post with ID: {}", likePostCreateDto.getPostId());

        Post post = postRepository.findById(likePostCreateDto.getPostId())
                .orElseThrow(() -> {
                    log.error("Post with ID: {} not found", likePostCreateDto.getPostId());
                    return new EntityNotFoundException("Post not found");
                });

        Like like = Like.builder()
                .userId(likePostCreateDto.getLikedUserId())
                .post(post)
                .build();

        likeRepository.save(like);
        log.info("Like saved for post ID: {} by user ID: {}", likePostCreateDto.getPostId(), likePostCreateDto.getLikedUserId());

        likeEventPublisher.publish(LikePostResponseDto.builder()
                .authorPostId(post.getAuthorId())
                .likedUserId(likePostCreateDto.getLikedUserId())
                .postId(likePostCreateDto.getPostId())
                .likeTime(LocalDateTime.now())
                .build()
        );
        log.info("Like event published for post ID: {}", likePostCreateDto.getPostId());

        return likePostCreateDto;
    }
}
