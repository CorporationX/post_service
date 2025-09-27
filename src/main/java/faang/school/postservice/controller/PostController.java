package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.request.CreatePostRequest;
import faang.school.postservice.dto.request.UpdatePostRequest;
import faang.school.postservice.dto.response.PostDto;
import faang.school.postservice.exception.MissingUserContextException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostCacheService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;
    private final FeedService feedService;
    private final UserContext userContext;
    private final PostCacheService postCacheService;

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody CreatePostRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Creating post for user ID: {}, published: {}", currentUserId, request.isPublished());

        Post createdPost = postService.createPost(
                request.getContent(),
                currentUserId,
                request.getProjectId(),
                request.isPublished()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/draft")
    public ResponseEntity<Post> createDraft(@Valid @RequestBody CreatePostRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Creating draft for user ID: {}", currentUserId);

        Post draft = postService.createPost(
                request.getContent(),
                currentUserId,
                request.getProjectId(),
                false
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(draft);
    }

    @PostMapping("/{postId}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long postId) {
        Long currentUserId = getCurrentUserId();
        Post publishedPost = postService.publishPost(postId, currentUserId);
        return ResponseEntity.ok(publishedPost);
    }

    @PostMapping("/schedule")
    public ResponseEntity<Post> schedulePost(
            @Valid @RequestBody CreatePostRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        Long currentUserId = getCurrentUserId();
        Post scheduledPost = postService.schedulePost(
                request.getContent(),
                currentUserId,
                request.getProjectId(),
                scheduledAt
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduledPost);
    }

    @GetMapping("/{postId}/view")
    public ResponseEntity<Post> viewPost(@PathVariable Long postId, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession().getId();

        Post post = postService.viewPost(postId, currentUserId, ipAddress, userAgent, "web", sessionId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        Long currentUserId = getCurrentUserId();
        Post updatedPost = postService.updatePost(postId, request.getContent(), currentUserId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        Long currentUserId = getCurrentUserId();
        postService.deletePost(postId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Integer> getPostLikesCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        int likesCount = post.getLikes() != null ? post.getLikes().size() : 0;
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getUserFeed() {
        Long currentUserId = getCurrentUserId();
        List<Post> feed = feedService.getUserFeed(currentUserId);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Post>> getTrendingPosts(@RequestParam(defaultValue = "10") int limit) {
        if (limit > 50) {
            limit = 50;
        }
        List<Post> trendingPosts = feedService.getTrendingPosts(limit);
        return ResponseEntity.ok(trendingPosts);
    }

    @GetMapping("/{postId}/cache/status")
    public ResponseEntity<Map<String, Object>> getPostCacheStatus(@PathVariable Long postId) {
        Map<String, Object> status = new HashMap<>();
        status.put("postId", postId);
        status.put("isCached", postCacheService.isPostCached(postId));
        status.put("ttlSeconds", postCacheService.getPostCacheTtl(postId));
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{postId}/cache")
    public ResponseEntity<Map<String, String>> evictPostFromCache(@PathVariable Long postId) {
        postCacheService.evictPost(postId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post " + postId + " evicted from cache");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDto>> getUserFeed(
            @RequestParam(value = "after", required = false) Long afterPostId) {

        Long currentUserId = getCurrentUserId();
        List<PostDto> feed = feedService.getUserFeed(currentUserId, afterPostId);

        return ResponseEntity.ok(feed);
    }

    @GetMapping("/{postId}/views")
    public ResponseEntity<Long> getPostViews(@PathVariable Long postId) {
        long views = postCacheService.getPostViews(postId);
        return ResponseEntity.ok(views);
    }

    private Long getCurrentUserId() {
        Long userId = userContext.getUserId();
        if (userId == null) {
            throw new MissingUserContextException();
        }
        return userId;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] ipHeaders = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };

        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
