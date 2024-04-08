package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.hash.PostHash;
import faang.school.postservice.hash.UserHash;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FeedHashRepository;
import faang.school.postservice.repository.PostHashRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserHashRepository;
import faang.school.postservice.service.hash.FeedHashService;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedHashRepository feedHashRepository;
    private final PostHashRepository postHashRepository;
    private final UserHashRepository userHashRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Value("${feed.post.amount}")
    private int amountPosts;


    public List<FeedDto> getFeed(Long userId, Long postId) {
        LinkedHashSet<Long> userPostIds = getPostIdsForUser(userId, postId);

        List<PostHash> userPostHashes = getPostsByIdsRedis(userPostIds);
        if (userPostHashes.isEmpty()) {
            userPostHashes = getPostsByIdsDB(userPostIds);
        }

        List<Long> userIds = userPostHashes.stream().map(PostHash::getAuthorId).toList();

        List<UserDto> users = getUsersByIdsRedis(userIds);
        if (users.isEmpty()) {
            users = getUsersByIdsDB(userIds);
        }

        List<FeedDto> feed = new ArrayList<>();
        int size = Math.min(userPostHashes.size(), users.size());
        for (int i = 0; i < size; i++) {
            PostHash postHash = userPostHashes.get(i);
            UserDto user = users.get(i);
            FeedDto feedDto = new FeedDto(postHash, user);
            feed.add(feedDto);
        }

        return feed;
    }

    public List<PostHash> getPostsByIdsRedis(LinkedHashSet<Long> postIds) {
        return postHashRepository.findByIds(postIds);
    }

    public List<PostHash> getPostsByIdsDB(LinkedHashSet<Long> postIds) {
        return postRepository.findByPostIds(postIds)
                .stream().map(PostHash::new).toList();
    }

    public List<UserDto> getUsersByIdsRedis(List<Long> userIds) {
        List<UserHash> userHashes = userHashRepository.findByIds(userIds);
        return userHashes.stream()
                .map(UserHash::getUserDto).toList();
    }

    public List<UserDto> getUsersByIdsDB(List<Long> userIds) {
        return userServiceClient.getUsersByIds(userIds);
    }

    public LinkedHashSet<Long> getPostIdsForUser(Long userId, Long postId) {
        LinkedHashSet<Long> postIds = feedHashRepository.findPostIdsByUserId(userId);

        if (postId == null || postId == 0) {
            return getLastNPostIds(postIds, amountPosts);
        } else {
            return getPostIdsBefore(postIds, postId, amountPosts);
        }
    }

    private LinkedHashSet<Long> getLastNPostIds(LinkedHashSet<Long> postIds, int n) {
        LinkedHashSet<Long> result = new LinkedHashSet<>();
        int size = postIds.size();
        int startIndex = Math.max(size - n, 0);

        int count = 0;
        for (Long postId : postIds) {
            if (count >= startIndex) {
                result.add(postId);
            }
            count++;
        }

        return result;
    }

    private LinkedHashSet<Long> getPostIdsBefore(LinkedHashSet<Long> postIds, Long postId, int n) {
        LinkedHashSet<Long> result = new LinkedHashSet<>();
        int postIndex = 0;

        for (Long id : postIds) {
            if (id.equals(postId)) {
                break;
            }
            postIndex++;
        }

        int startIndex = Math.max(postIndex - n, 0);
        int count = 0;

        for (Long id : postIds) {
            if (count >= startIndex && count < postIndex) {
                result.add(id);
            }
            count++;
        }

        return result;
    }

}
