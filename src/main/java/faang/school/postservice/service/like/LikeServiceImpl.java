package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final PostMapper postMapper;
    private final LikeRepository likeRepository;
    private final PostServiceImpl postService;
    private final LikeEventPublisher likePublisher;

    @Override
    public LikeDto addCommentLike(LikeDto likeDto) {
        return null;
    }

    @Override
    public void deleteCommentLike(Long userId, Long commentId) {

    }

    @Override
    public LikeDto addPostLike(LikeDto likeDto) {
        likeValidator.validateUserExistence(likeDto);
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        Post post = postMapper.toEntity(postService.getPost(postId));
        Like like = likeMapper.toEntity(likeDto);
        post.getLikes().add(like);
        likePublisher.publish(new LikeEvent(postId, post.getAuthorId(), userId, LocalDateTime.now()));
        log.info("Like with likeId = {} added on post with postId = {} by user with userId = {}", like.getId(), postId, userId);
        return likeMapper.toDto(like);
    }

    @Override
    public void deletePostLike(Long userid, Long postId) {

    }
}
