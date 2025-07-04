package faang.school.postservice.service;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.dto.post.PostCashDto;
import faang.school.postservice.dto.post.PostUiDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.post.PostCashRepository;
import faang.school.postservice.repository.user.UserDtoCashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    @Value("${spring.newsfeed.size}")
    private int newsFeedMaxSize;
    private static final String NEWS_FEED_KEY_PREFIX = "newsfeed:";
    private final RedisTemplate<String, Long> redisNewsFeedTemplate;
    private final PostCashRepository postCashRepository;
    private final UserDtoCashRepository userDtoCashRepository;

    public NewsFeed getNewsFeed(Long userId) {
        String key = getKey(userId);
        Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, 0, -1);

        SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
        if (postIds != null) {
            posts.addAll(postIds);
        }
        return new NewsFeed(userId, posts);
    }

    public List<PostUiDto> getNewsFeedContent(Long userId) {
        String key = getKey(userId);
        Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, 0, -1);

        SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
        if (postIds != null) {
            posts.addAll(postIds);
        }
        List<PostUiDto> newsFeedPosts = new ArrayList<>();
        for(Long p: posts) {
            Optional<PostCashDto> optionalPost = postCashRepository.findById(p);
            if(optionalPost.isPresent()) {
                PostUiDto postUiDto = new PostUiDto();
                Optional<UserDto> optionalUserDto = userDtoCashRepository.findById(optionalPost.get().getAuthorId());
                postUiDto.setId(p);
                postUiDto.setContent(optionalPost.get().getContent());
                optionalUserDto.ifPresent(postUiDto::setAuthor);
                postUiDto.setProjectId(optionalPost.get().getProjectId());
                postUiDto.setLikesNumber(optionalPost.get().getLikesNumber());
                newsFeedPosts.add(postUiDto);
            }
        }
        return newsFeedPosts;
    }

//    public List<PostCashDto> getNewsFeedContent(Long userId) {
//        String key = getKey(userId);
//        Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, 0, -1);
//
//        SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
//        if (postIds != null) {
//            posts.addAll(postIds);
//        }
//        //post content fill out
//        List<PostCashDto> newsFeedPostsDto = new ArrayList<>();
//        for(Long p: posts) {
//            Optional<PostCashDto> optional = postCashRepository.findById(p);
//            optional.ifPresent(newsFeedPostsDto::add);
//        }
//        //post authorId fill out
//
//        return newsFeedPostsDto;
//    }

    public void addPostIdToUserNewsFeed(PostAndFollowersDto postAndFollowersDto) {
        for (Long follower : postAndFollowersDto.getFollowers()) {
            double score = -System.currentTimeMillis();
            String redisKey = getKey(follower);
            if (Boolean.TRUE.equals(redisNewsFeedTemplate.hasKey(redisKey))) {
                redisNewsFeedTemplate.opsForZSet().add(
                        redisKey,
                        postAndFollowersDto.getId(),
                        score);
                trimNewsFeed(redisKey, newsFeedMaxSize);
                log.info("Size of newsfeed: {}", redisNewsFeedTemplate.opsForZSet().size(redisKey));
            } else {
                redisNewsFeedTemplate.opsForZSet().add(
                        redisKey,
                        postAndFollowersDto.getId(),
                        score);
            }
        }

        log.info("NewsFeedService.addPostIdToUserNewsFeed() {}", postAndFollowersDto);
//        for(Long follower: postAndFollowersDto.getFollowers()) {
//            Optional<NewsFeed> optional = newsFeedCashRepository.findById(follower);
//            if(optional.isPresent()) {
//                NewsFeed newsFeed = optional.get();
//                TreeSet<Long> reversePosts = new TreeSet<>(Comparator.reverseOrder());
//                reversePosts.addAll(newsFeed.getPosts());
//                newsFeed.setPosts(reversePosts);
//                if(newsFeed.getPosts().size() < newsFeedMaxSize) {
//                    newsFeed.getPosts().add(postAndFollowersDto.getId());
//                    newsFeedCashRepository.save(newsFeed);
//                } else {
//                    Long lastPost = newsFeed.getPosts().last();
//                    log.info("newsFeed.getPosts().last() = {}", lastPost);
//                    boolean removed = newsFeed.getPosts().remove(lastPost);
//                    newsFeed.getPosts().add(postAndFollowersDto.getId());
//                    log.info("newsFeed.getPosts() after adding post {}", newsFeed.getPosts());
//                    newsFeedCashRepository.save(newsFeed);
//                }
//            } else {
//                SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
//                posts.add(postAndFollowersDto.getId());
//                NewsFeed newsFeed = new NewsFeed(follower, posts);
//                newsFeedCashRepository.save(newsFeed);
//            }
//        }
    }

    private String getKey(Long userId) {
        return NEWS_FEED_KEY_PREFIX + userId;
    }

    private void trimNewsFeed(String redisKey, int size) {
        Long feedSize = redisNewsFeedTemplate.opsForZSet().size(redisKey);
        if (feedSize != null) {
            redisNewsFeedTemplate.opsForZSet().removeRange(redisKey, size, feedSize);
        }
    }
}
