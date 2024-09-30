package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.feed.HeatFeedEvent;
import faang.school.postservice.event.user.UserCacheEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.user.UserCacheProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeatFeedService {
    private final UserServiceClient userServiceClient;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    private final PostCacheRepository postCacheRepository;

    private final UserCacheProducer userCacheProducer;

    private final FeedService feedService;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    @Value("${cache.comments.max}")
    private int maxComments;

    public void heatFeed() {
        userServiceClient.getAllUsersAndFolowees();
    }

    @Async("taskExecutor")
    public void heatFeedUpdateCache(List<HeatFeedEvent> events) {
        Pageable pageable = PageRequest.of(0, maxFeedSize);
        Pageable commentPageable = PageRequest.of(0, maxComments);

        for (HeatFeedEvent event : events) {
            List<Post> posts = getPostsForEvent(event, pageable);

            for (Post post : posts) {
                PostDto postDto = postMapper.toDtoForCache(post);
                updateFeedAndCache(event.getId(), postDto);
                cacheComments(post.getId(), commentPageable);
            }
        }
    }

    private List<Post> getPostsForEvent(HeatFeedEvent event, Pageable pageable) {
        return postRepository.findPostsByAuthorIdsWithLikes(event.getFolowees(), pageable);
    }

    private void updateFeedAndCache(Long userId, PostDto postDto) {
        feedService.updateFeed(userId, postDto.getId());

        postCacheRepository.save(postDto.getId(), postDto);
        postCacheRepository.setLike(postDto.getId(), postDto.getLikesCount());
        postCacheRepository.setViews(postDto.getId(), postDto.getViews());

        userCacheProducer.sendEvent(new UserCacheEvent(postDto.getAuthorId()));
    }

    private void cacheComments(Long postId, Pageable pageable) {
        List<Comment> comments = commentRepository.findByPostIdWithLikesOrderByCreatedAtDesc(postId, pageable);

        for (Comment comment : comments) {
            postCacheRepository.addComment(postId, commentMapper.toDto(comment));
        }
    }
}
