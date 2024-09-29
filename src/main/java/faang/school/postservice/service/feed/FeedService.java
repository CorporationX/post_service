package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.user.UserCacheEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.user.UserCacheProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.feed.HeatFeedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserServiceClient userServiceClient;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;

    private final UserCacheProducer userCacheProducer;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    @Value("${cache.comments.max}")
    private int maxComments;

    public void updateFeed(Long subscriberId, Long postId) {
        feedCacheRepository.update(subscriberId, postId);
    }

    public List<Long> getFeed(Long subscriberId, int batchSize) {
        return feedCacheRepository.getTopPosts(subscriberId, batchSize);
    }

    public void heatFeed() {
        userServiceClient.getAllUsersAndFolowees();
    }

    @Async("taskExecutor")
    public void heatFeedUpdateCache(List<HeatFeedEvent> events) {
        Pageable pageable = PageRequest.of(0, maxFeedSize);
        Pageable commentPageable = PageRequest.of(0, maxComments);

        for(var event: events) {
            List<Post> posts = postRepository.findPostsByAuthorIdsWithLikes(event.getFolowees(), pageable);

            List<PostDto> postDtos = posts.stream()
                    .map(postMapper::toDtoForCache)
                    .collect(Collectors.toList());

            for(var post: postDtos) {
                updateFeed(event.getId(), post.getId());
                postCacheRepository.save(post.getId(), post);
                postCacheRepository.setLike(post.getId(), post.getLikesCount());

                userCacheProducer.sendEvent(new UserCacheEvent(post.getAuthorId()));

                List<Comment> comments =
                        commentRepository.findByPostIdWithLikesOrderByCreatedAtDesc(post.getId(), commentPageable);
                for(var comment: comments) {
                    postCacheRepository.addComment(post.getId(), commentMapper.toDto(comment));
                }
            }
        }
    }
}
