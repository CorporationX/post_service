package faang.school.postservice.service;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.NewsFeedCashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumerService {
    private final NewsFeedCashRepository newsFeedCashRepository;

    private final KafkaLikeProducerService kafkaLikeProducerService;
    @KafkaListener(topics={"PostLike"}, groupId = "consumer-post-like")
    public void consumePostLike(LikeDto likeDto, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        log.info("Acknowledge PostLike: {}" , likeDto);
    }

    @KafkaListener(topics={"CommentLike"}, groupId = "consumer-comment-like")
    public void consumeCommentLike(LikeDto likeDto, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        log.info("Acknowledge CommentLike: {}" , likeDto);
    }

    @KafkaListener(topics={"PostCreation"}, groupId = "consumer-post")
    public void consumePost(PostDto postDto, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        log.info("Acknowledge Post created: {}" , postDto);
    }

    @KafkaListener(topics={"PostAndFollowers"}, groupId = "consumer-post")
    public void consumePostCreation(PostAndFollowersDto postAndFollowersDto, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        log.info("Acknowledge Post created in PostAndFollowers topic: {}" , postAndFollowersDto);
        addPostIdToUserNewsFeed(postAndFollowersDto);
    }

    private void addPostIdToUserNewsFeed(PostAndFollowersDto postAndFollowersDto) {
        for(Long follower: postAndFollowersDto.getFollowers()) {
            Optional<NewsFeed> optional = newsFeedCashRepository.findById(follower);
            if(optional.isPresent()) {
                NewsFeed newsFeed = optional.get();
                newsFeed.getPosts().add(postAndFollowersDto.getId());
                newsFeedCashRepository.save(newsFeed);
            } else {
                SortedSet<Long> posts = new TreeSet<>();
                posts.add(postAndFollowersDto.getId());
                NewsFeed newsFeed = new NewsFeed(follower, posts);
                newsFeedCashRepository.save(newsFeed);
            }
        }
    }
}
