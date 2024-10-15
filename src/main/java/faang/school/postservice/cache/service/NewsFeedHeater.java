package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.NewsFeedRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.event.heater.HeaterNewsFeedEvent;
import faang.school.postservice.kafka.event.heater.HeaterPostsEvent;
import faang.school.postservice.kafka.event.heater.HeaterUsersEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsFeedHeater {
    private final UserServiceClient userServiceClient;
    private final KafkaProducer kafkaProducer;
    private final PostService postService;
    private final CommentService commentService;
    private final PostRedisService postRedisService;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.topic.heater.users}")
    private String heaterUsersTopic;
    @Value("${spring.kafka.topic.heater.news-feeds}")
    private String heaterNewsFeedsTopic;
    @Value("${spring.kafka.topic.heater.posts}")
    private String heaterPostsTopic;
    @Value("${news-feed.max-size}")
    private int newsFeedMaxSize;
    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;

    public void heat() {
        List<UserRedis> usersRedis = userServiceClient.getActiveUsersRedis();
        splitAndSendUsersEvents(usersRedis);

        List<NewsFeedRedis> newsFeeds = getNewsFeedsForUsers(usersRedis);
        splitAndSendNewsFeedsEvents(newsFeeds);

        List<Long> postIds = getUniquePostIds(newsFeeds);
        splitAndSendPostsEvents(postIds);
    }

    public void saveAllPosts(List<Long> postIds) {
        List<PostRedis> posts = postService.findAllByIdsWithLikes(postIds);
        setComments(postIds, posts);
        postRedisService.saveAll(posts);
    }

    private void setComments(List<Long> postIds, List<PostRedis> posts) {
        List<CommentRedis> comments = commentService.findLastBatchByPostIds(commentsMaxSize, postIds);
        if (comments.isEmpty()) {
            return;
        }
        Map<Long, TreeSet<CommentRedis>> commentsByPosts = new HashMap<>();
        comments.forEach(comment -> commentsByPosts
                .computeIfAbsent(comment.getPostId(), k -> new TreeSet<>())
                .add(comment));
        posts.forEach(post -> post.setComments(commentsByPosts.get(post.getId())));
    }

    private List<Long> getUniquePostIds(List<NewsFeedRedis> newsFeeds) {
        return newsFeeds.stream()
                .flatMap(newsFeedRedis -> newsFeedRedis.getPostIds().stream())
                .distinct()
                .toList();
    }

    private List<NewsFeedRedis> getNewsFeedsForUsers(List<UserRedis> usersRedis) {
        return usersRedis.parallelStream()
                .map(user -> {
                    List<Long> postIds = postService.findPostIdsByFollowerId(user.getId(), newsFeedMaxSize);
                    return new NewsFeedRedis(user.getId(), postIds);
                })
                .filter(newsFeed -> !newsFeed.getPostIds().isEmpty())
                .toList();
    }

    private void splitAndSendNewsFeedsEvents(List<NewsFeedRedis> newsFeeds) {
        List<List<NewsFeedRedis>> splitNewsFeeds = split(newsFeeds, batchSize);
        splitNewsFeeds.forEach(list -> kafkaProducer.send(heaterNewsFeedsTopic, new HeaterNewsFeedEvent(list)));
    }

    private void splitAndSendUsersEvents(List<UserRedis> usersRedis) {
        List<List<UserRedis>> splitUsers = split(usersRedis, batchSize);
        splitUsers.forEach(list -> kafkaProducer.send(heaterUsersTopic, new HeaterUsersEvent(list)));
    }

    private void splitAndSendPostsEvents(List<Long> postIds) {
        List<List<Long>> splitUsers = split(postIds, batchSize);
        splitUsers.forEach(list -> kafkaProducer.send(heaterPostsTopic, new HeaterPostsEvent(list)));
    }

    private <T> List<List<T>> split(List<T> list, int batchSize) {
        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += batchSize) {
            result.add(list.subList(i, Math.min(size, i + batchSize)));
        }
        return result;
    }
}
