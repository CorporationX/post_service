package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;

    @Value("${feed.batch-size:20}")
    private int feedBatchSize;

    public void updateFeed(Long subscriberId, Long postId) {
        feedCacheRepository.update(subscriberId, postId);
    }

    public List<PostForFeedDto> getFeed(long subscriberId, Long postId) {
        if(postId == null) {
            postId = 0L;
        }
        List<Long> feed = feedCacheRepository.getTopPosts(subscriberId, feedBatchSize, postId);

        if(feed.size() != feedBatchSize) {
            feed.addAll(getMissingPosts(feed));
        }

        List<PostForFeedDto> posts = feed.stream()
                .map(this::getCompletePostDTO)
                .collect(Collectors.toList());


        return posts;
    }

    private PostForFeedDto getCompletePostDTO(long postId) {
        PostDto postDto = postCacheRepository.getPost(postId);
        List<CommentDto> commentDto = postCacheRepository.getComments(postId);
        long likes = postCacheRepository.getLikes(postId);
        long views = postCacheRepository.getViews(postId);

        PostForFeedDto post = PostForFeedDto.builder()
                .postDto(postDto)
                .commentDto(commentDto)
                .likes(likes)
                .views(views)
                .build();

        return post;
    }

    private List<Long> getMissingPosts(List<Long> feed) {
        int batchSize = feedBatchSize = feed.size();
        return feedCacheRepository.getUniqueRecommendation(feed, batchSize);
    }
}
