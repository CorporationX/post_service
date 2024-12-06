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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikePostService {

    private final LikeEventPublisher likeEventPublisher;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public LikePostCreateDto likePost(LikePostCreateDto likePostCreateDto) {
        Post post = postRepository.findById(likePostCreateDto.getPostId())
                .orElseThrow(EntityNotFoundException::new);

        Like like = Like.builder()
                .userId(likePostCreateDto.getLikedUserId())
                .post(post)
                .build();

        likeRepository.save(like);

        likeEventPublisher.publish(LikePostResponseDto.builder()
                .authorPostId(post.getAuthorId())
                .likedUserId(likePostCreateDto.getLikedUserId())
                .postId(likePostCreateDto.getPostId())
                .likeTime(LocalDateTime.now())
                .build()
        );
        return likePostCreateDto;
    }
}
