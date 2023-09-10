package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final LikeEventPublisher likeEventPublisher;
    private static final int BATCH_SIZE = 100;

    @Transactional
    public LikeDto createLike(LikeDto likeDto) {
        likeDto.setId(null);
        Like like = likeRepository.save(likeMapper.toEntity(likeDto));
        likeEventPublisher.publishMessage(createLikeEvent(likeDto));
        log.info("Created like. Id: {}", like.getId());
        return likeMapper.toDto(like);
    }

    public List<UserDto> getUsersByPostId(long postId) {
        List<Long> userIds = getLikedUserIdsByPost(postId);
        return getAllUsersDto(userIds);
    }

    public List<UserDto> getUsersByCommentId(long commentId) {
        List<Long> userIds = getLikedUserIdsByComment(commentId);
        return getAllUsersDto(userIds);
    }

    private LikeEventDto createLikeEvent(LikeDto likeDto) {
        LikeEventDto likeEventDto = new LikeEventDto();
        likeEventDto.setLikeAuthorId(likeDto.getUserId());
        likeEventDto.setLikeAuthorName(userServiceClient.getUser(likeDto.getUserId()).getUsername());
        if (likeDto.getPostId() != null) {
            likeEventDto.setPostId(likeDto.getPostId());
            likeEventDto.setPostAuthor(postService.getAuthorId(likeDto.getPostId()));
        } else {
            likeEventDto.setCommentId(likeDto.getCommentId());
            likeEventDto.setCommentAuthor(commentService.getAuthorId(likeDto.getPostId()));
        }
        return likeEventDto;
    }

    private List<Long> getLikedUserIdsByPost(long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<Long> getLikedUserIdsByComment(long commentId) {
        return likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<UserDto> getAllUsersDto(List<Long> userIds) {
        List<UserDto> allUsers = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            allUsers.addAll(userServiceClient.getUsersByIds(userIds.subList(i, endIndex)));
        }
        return allUsers;
    }
}
