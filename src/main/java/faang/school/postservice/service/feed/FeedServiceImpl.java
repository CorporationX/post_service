package faang.school.postservice.service.feed;

import faang.school.postservice.broker.producer.PostProcessEventProducer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.feed.FeedItemCommentDto;
import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.feed.FeedRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final PostService postService;
    private final FeedRepository feedRepository;
    private final PostProcessEventProducer postProcessEventProducer;
    private final NewsFeedProperties newsFeedProperties;
    private final LikeService likeService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserContext userContext;

    @Override
    public Set<PostResponseDto> getFeed(long userId, int pageNum) {
        log.info("Get feed for user {}, page {}", userId, pageNum);
        Set<FeedItemDto> feedItems = feedRepository.feedItems(userId, pageNum);

        return feedItems.stream()
                .map(feedItemDto -> {
                    return postService.getPostWithCache(feedItemDto.postId());
                })
                .collect(Collectors.toSet());
    }

    @Override
    public void processNewPost(Long postId, List<Long> followersIds) {

        int batchSize = newsFeedProperties.batchSize();
        PostResponseDto postResponseDto = postService.getPostWithCache(postId);
        List<List<Long>> batches = ListUtils.partition(followersIds, batchSize);

        if (CollectionUtils.isNotEmpty(batches)) {
            batches.forEach(batch -> {
                try {
                    if (CollectionUtils.isNotEmpty(batch)) {
                        postProcessEventProducer.produceSubProcessPostEventAsync(
                                postResponseDto.authorId(),
                                postResponseDto,
                                batch
                        );
                    }
                } catch (Exception e) {
                    log.error("Failed to process batch: {}", batch, e);
                }
            });
        } else {
            log.warn("Batches list is empty or null");
        }
        log.info("Creation new post event processed. Post id {}", postId);
    }

    @Override
    public void subProcessNewPost(Long postId, List<Long> partFollowersIds) {
        PostResponseDto post = postService.getPostWithCache(postId);
        feedRepository.addPostToFollowersFeeds(partFollowersIds, post);
        log.info("New post {} partially processed", postId);
    }

    @Override
    public void subProcessExistingPost(Long postId, List<Long> partFollowersIds) {
        userContext.setUserId(0L);
        PostResponseDto post = postService.getPostWithCache(postId);
        long likesQuantity = likeService.getLikes(postId).size();
        //TODO тут переделать на специальный метод, возвращающий 3 последних поста
        List<CommentResponseDto> comments = commentService.getAllByPostId(postId);
        if (comments.size() > newsFeedProperties.commentsNumber()) {
            comments = comments.subList(0, newsFeedProperties.commentsNumber() - 1);
        }

        LinkedHashSet<FeedItemCommentDto> commentsSet = new LinkedHashSet<>();
        for (CommentResponseDto comment : comments) {
            FeedItemCommentDto feedItemCommentDto = commentMapper.toFeedItemCommentDto(comment);
            commentsSet.add(feedItemCommentDto);
        }

        PostResponseDto filledPost = PostResponseDto.builder()
                .id(post.id())
                .content(post.content())
                .postLikesCounter(likesQuantity)
                .authorId(post.authorId())
                .projectId(post.projectId())
                .publishedAt(post.publishedAt())
                .comments(commentsSet)
                .build();
        feedRepository.addPostToFollowersFeeds(partFollowersIds, filledPost);
        log.info("Existing post {} partially processed", postId);
    }
}


