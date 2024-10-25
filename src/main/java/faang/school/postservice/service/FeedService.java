package faang.school.postservice.service;

import faang.school.postservice.cache.entity.UserCache;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.cache.repository.PostCacheRepository;
import faang.school.postservice.cache.repository.UserCacheRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    @Value("${feed.batch}")
    private Integer postsBatchSize;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final UserCacheRepository userCacheRepository;
    private final PostRepository postRepository;
    private final PostCacheRepository postCacheRepository;
    private final FeedRepository feedRepository;

    @Transactional
    public List<PostDto> getPosts(Long lastPostId) {
        long userId = userContext.getUserId();
        Collection<Long> postIdsBatch = feedRepository.getNextBatch(userId, lastPostId, postsBatchSize);

        if (!postIdsBatch.isEmpty()) {
            return postCacheRepository.findPostsByIds(postIdsBatch)
                    .stream()
                    .map(postMapper::toDto)
                    .toList();
        } else {
            Pageable pageable = PageRequest.of(0, postsBatchSize);
            Optional<UserCache> cachedUser = userCacheRepository.findById(userId);

            List<Long> userPostAuthors;
            if (cachedUser.isPresent()) {
                userPostAuthors = cachedUser.get().getUserSubscribedAuthors();
            } else {
                userPostAuthors = userServiceClient.getUserSubscribedAuthors(userId).stream()
                        .map(UserDto::getId)
                        .toList();
            }
            return postRepository.findPostsByAuthorIdsAndLastPostId(userPostAuthors, lastPostId, pageable)
                    .stream().map(postMapper::toDto)
                    .toList();
        }
    }

    @Async
    @Transactional
    public void addPostIdToFollowers(long postId, List<Long> followerIds){
        feedRepository.addPostToUsers(postId, followerIds);
    }

    @Transactional
    public void addPostIdToUserFeed(Long userId, Long postId) {
        feedRepository.addPost(userId, postId);
    }

    @Transactional
    public void addLikeToPost(Long postId) {
        postCacheRepository.addLikeToPost(postId);
    }

    @Transactional
    public void addViewToPost(Long postId) {
        postCacheRepository.addViewToPost(postId);
    }
}
