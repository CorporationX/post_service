package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.comment.FeedCommentRedisService;
import faang.school.postservice.service.feed.post.FeedPostRedisService;
import faang.school.postservice.service.kafka.publisher.KafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final UserServiceClient userServiceClient;
    private final KafkaPublisher kafkaPublisher;
    private final FeedPostRedisService feedPostRedisService;
    private final FeedCommentRedisService feedCommentRedisService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final ExecutorService feedNextPostBatchExecutor;
    private final ExecutorService feedNextCommentBatchExecutor;
    private final CommentMapper commentMapper;

    @Value("${app.feed.user-heat-batch-size}")
    private int userHeatBatchSize;

    @Value("${app.feed.heat-topic}")
    private String heatTopic;

    @Value("${app.feed.post-batch-size}")
    private int postBatchSize;

    @Value("${app.feed.comment-batch-size}")
    private int commentBatchSize;

    @Override
    public Page<FeedPostDto> getFeedPosts(Long userId, int offset) {
        List<FeedPostDto> posts;
        Pageable pageable = PageRequest.of(offset / postBatchSize, postBatchSize);
        if (feedPostRedisService.isPostAvailableInCache(userId, offset)) {
            posts = feedPostRedisService.loadPostsFromCache(userId, offset);
            posts.forEach(post -> {
                if (feedCommentRedisService.isCommentAvailableInCache(post.getId(), 0)) {
                    feedCommentRedisService.preloadPostComments(post.getId());
                }
            });
        } else {
            List<Long> followees = userServiceClient.getFollowees(userId);
            posts = postMapper.toFeedPostDtoList(postRepository
                    .findPublishedPostsByAuthorIds(pageable, followees).getContent());

            feedNextPostBatchExecutor.submit(() -> heatNextPostBatch(userId, offset, posts));
        }
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
    public Page<FeedCommentDto> getFeedComments(Long postId, int offset) {
        List<FeedCommentDto> comments;
        Pageable pageable = PageRequest.of(offset / commentBatchSize, commentBatchSize);
        if (feedCommentRedisService.isCommentAvailableInCache(postId, offset)) {
            comments = feedCommentRedisService.loadCommentsFromCache(postId, offset);
        } else {
            comments = commentMapper.toFeedCommentDtoList(commentRepository
                    .findByPostIdOrderByCreatedAtDesc(pageable, postId).getContent());

            feedNextCommentBatchExecutor.submit(() -> heatNextCommentBatch(postId, offset, comments));
        }
        return new PageImpl<>(comments, pageable, comments.size());
    }

    @Override
    public void heatFeedCache() {
        int page = 0;
        while (true) {
            List<Long> userIds = userServiceClient.getUserIdsByPage(page, userHeatBatchSize);
            if (userIds.isEmpty()) {
                break;
            }
            userIds.forEach(userId -> kafkaPublisher.send(heatTopic, userId));
            page++;
        }
    }

    public void heatFeedConsumer(Long userId) {
        log.info("Heating cached user id {}", userId);
        feedPostRedisService.preloadUserPosts(userId);
        List<Long> followees = userServiceClient.getFollowees(userId);
        if (followees.isEmpty()) {
            return;
        }
        List<Long> postIds = postRepository.findPublishedPostIdsByAuthorIds(
                PageRequest.of(0, postBatchSize), followees).getContent();

        postIds.forEach(feedCommentRedisService::preloadPostComments);
    }

    private void heatNextPostBatch(Long userId, int offset, List<FeedPostDto> posts) {
        feedPostRedisService.updateUserPostOffset(userId, offset + postBatchSize);
        posts.forEach(feedPostDto -> {
            feedPostRedisService.cachePostDetails(feedPostDto);
            feedPostRedisService.cachePostIdForUser(userId, feedPostDto.getId(), offset + postBatchSize);
        });
    }

    private void heatNextCommentBatch(Long postId, int offset, List<FeedCommentDto> comments) {
        feedCommentRedisService.updatePostCommentsOffset(postId, offset + postBatchSize);
        comments.forEach(feedPostDto -> {
            feedCommentRedisService.cacheCommentDetails(feedPostDto);
            feedCommentRedisService.cacheCommentIdForPost(postId, feedPostDto.getId(), offset + postBatchSize);
        });
    }
}
