package faang.school.postservice.service;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.repository.NewsFeedCashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    @Value("${spring.newsfeed.size}")
    private int newsFeedMaxSize;
    private final NewsFeedCashRepository newsFeedCashRepository;

    public NewsFeed getNewsFeed(Long userId) {
        Optional<NewsFeed> optional = newsFeedCashRepository.findById(userId);
        if(optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("User with id=" + userId + " is not found");
        }
    }

    public void addPostIdToUserNewsFeed(PostAndFollowersDto postAndFollowersDto) {
        for(Long follower: postAndFollowersDto.getFollowers()) {
            Optional<NewsFeed> optional = newsFeedCashRepository.findById(follower);
            if(optional.isPresent()) {
                NewsFeed newsFeed = optional.get();
                TreeSet<Long> reversePosts = new TreeSet<>(Comparator.reverseOrder());
                reversePosts.addAll(newsFeed.getPosts());
                newsFeed.setPosts(reversePosts);
                if(newsFeed.getPosts().size() < newsFeedMaxSize) {
                    newsFeed.getPosts().add(postAndFollowersDto.getId());
                    newsFeedCashRepository.save(newsFeed);
                } else {
                    Long lastPost = newsFeed.getPosts().last();
                    log.info("newsFeed.getPosts().last() = {}", lastPost);
                    boolean removed = newsFeed.getPosts().remove(lastPost);
                    newsFeed.getPosts().add(postAndFollowersDto.getId());
                    log.info("newsFeed.getPosts() after adding post {}", newsFeed.getPosts());
                    newsFeedCashRepository.save(newsFeed);
                }
            } else {
                SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
                posts.add(postAndFollowersDto.getId());
                NewsFeed newsFeed = new NewsFeed(follower, posts);
                newsFeedCashRepository.save(newsFeed);
            }
        }
    }
}
