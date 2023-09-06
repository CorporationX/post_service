package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.publisher.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostService postService;
    private final LikeEventPublisher likeEventPublisher;

    @Transactional
    public LikeDto likePost(LikeDto likeDto, Long currentUserId) {
        PostDto currentPost = postService.getPostById(likeDto.getPostId());
        LikeEventDto like = new LikeEventDto(likeDto.getUserId(),currentPost.getAuthorId(),likeDto.getPostId());
        likeEventPublisher.publish(like);
        return null;
    }
}
