package faang.school.postservice.service.recommended_service;

import faang.school.postservice.dto.recommended_service.UserActivitiesGetDto;
import faang.school.postservice.dto.recommended_service.UserActivitiesViewDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Сервис для получения активностей пользователя
 *
 * @author Linempy
 * @since 23.11.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivitiesService {
    private static final String OWN_POSTS = "ownPosts";
    private static final String LIKED_POSTS = "likedPosts";
    private static final String POSTS_BY_LIKED_COMMENTS = "postsByLikedComments";
    private static final String COMMENTED_POSTS = "commentedPosts";
    private static final String OWN_COMMENTS = "ownComments";

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public UserActivitiesViewDto getUserActivities(UserActivitiesGetDto dto) {
        Map<Long, Map<String, List<String>>> result = new HashMap<>();

        for (Long userId : dto.userIds()) {
            Map<String, List<String>> activities = collectUserActivities(userId, dto.limit());
            result.put(userId, activities);
        }

        log.info("Были собраны активности для {} пользователей", dto.userIds().size());
        return new UserActivitiesViewDto(result);
    }

    private Map<String, List<String>> collectUserActivities(Long userId, int limit) {
        Map<String, List<String>> activities = new HashMap<>();

        activities.put(OWN_POSTS, postRepository.findContentByAuthorId(userId, limit));
        activities.put(LIKED_POSTS, getLikedPostsContent(userId, limit));
        activities.put(POSTS_BY_LIKED_COMMENTS, getPostsByLikedCommentsContent(userId, limit));
        activities.put(COMMENTED_POSTS, getCommentedPostsContent(userId, limit));
        activities.put(OWN_COMMENTS, commentRepository.findContentByAuthorId(userId, limit));

        return activities;
    }

    private List<String> getLikedPostsContent(Long userId, int limit) {
        Set<Long> likedPostIds = new HashSet<>(likeRepository.findPostIdsByUserId(userId));
        return getPostsContentByIds(new ArrayList<>(likedPostIds), limit);
    }

    private List<String> getPostsByLikedCommentsContent(Long userId, int limit) {
        Set<Long> postIdsOfLikedComments = getPostIdsOfLikedComments(userId);
        return getPostsContentByIds(new ArrayList<>(postIdsOfLikedComments), limit);
    }

    private Set<Long> getPostIdsOfLikedComments(Long userId) {
        List<Long> postIds = likeRepository.findPostIdsByUserId(userId);
        return new HashSet<>(postIds);
    }

    private List<String> getCommentedPostsContent(Long userId, int limit) {
        Set<Long> commentedPostIds = new HashSet<>(commentRepository.findPostIdByAuthorId(userId));
        return getPostsContentByIds(new ArrayList<>(commentedPostIds), limit);
    }

    private List<String> getPostsContentByIds(List<Long> postIds, int limit) {
        if (postIds.isEmpty()) {
            return Collections.emptyList();
        }
        return postRepository.findContentByIds(postIds, limit);
    }

}