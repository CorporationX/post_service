package faang.school.postservice.factory.post;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.CacheRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.user.UserCacheRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedPostFactory {

    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserCacheRepository userCacheRepository;
    private final UserMapper userMapper;
    private final CacheRepository cacheRepository;

    public FeedPostDto fromPost(Post post) {
        FeedPostDto feedPostDto = postMapper.toFeedPostDto(post);
        addLastComments(feedPostDto);
        addUserData(feedPostDto);
        return feedPostDto;
    }

    public FeedPostDto fromPostCache(PostCache postCache) {
        FeedPostDto feedPostDto = postMapper.toFeedPostDto(postCache);
        addUserData(feedPostDto);
        return feedPostDto;
    }

    private void addLastComments(@NonNull FeedPostDto feedPostDto) {
        List<Comment> last3Comments = commentRepository.findTop3ByPostIdOrderByCreatedAtDesc(feedPostDto.getId());
        feedPostDto.setLastComments(
                last3Comments.stream()
                        .map(commentMapper::toFeedCommentDto)
                        .toList()
        );
    }

    private void addUserData(@NonNull FeedPostDto feedPost) {
        if (feedPost.getAuthorId() == null || feedPost.getAuthorUser() != null) {
            return;
        }

        Optional<UserCache> cacheUser = userCacheRepository.findById(String.valueOf(feedPost.getAuthorId()));
        if (cacheUser.isPresent()) {
            feedPost.setAuthorUser(userMapper.toFeedUserDto(cacheUser.get()));
        } else {
            log.info("User ID: {} cache miss.", feedPost.getAuthorId());
            UserDto userDto = cacheRepository.getUser(feedPost.getAuthorId());
            log.info("Retrieved user ID: {} from DB.", feedPost.getAuthorId());
            feedPost.setAuthorUser(userMapper.toFeedUserDto(userDto));
//            userCacheRepository.saveIfAbsent(userMapper.toUserCache(userDto));
        }
    }
}
