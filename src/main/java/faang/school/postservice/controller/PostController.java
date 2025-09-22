package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.request.CreatePostRequest;
import faang.school.postservice.dto.request.UpdatePostRequest;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
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

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;
    private final UserContext userContext;

    private Long getCurrentUserId() {
        Long userId = userContext.getUserId();
        if (userId == null) {
            throw new IllegalStateException("User context is not set. Please ensure x-user-id header is provided.");
        }
        return userId;
    }

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

        log.info("Successfully created post with ID: {} for user: {}",
                createdPost.getId(), currentUserId);

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
        log.info("Publishing post ID: {} by user ID: {}", postId, currentUserId);

        Post publishedPost = postService.publishPost(postId, currentUserId);

        log.info("Successfully published post ID: {}", postId);

        return ResponseEntity.ok(publishedPost);
    }

    @PostMapping("/schedule")
    public ResponseEntity<Post> schedulePost(
            @Valid @RequestBody CreatePostRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {

        Long currentUserId = getCurrentUserId();
        log.info("Scheduling post for user ID: {} at {}", currentUserId, scheduledAt);

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
        log.info("User {} is viewing post {}", currentUserId, postId);

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession().getId();

        Post post = postService.viewPost(postId, currentUserId, ipAddress, userAgent, "web", sessionId);

        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        log.info("Fetching post with ID: {} (without view tracking)", postId);

        Post post = postService.getPostById(postId);

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {

        Long currentUserId = getCurrentUserId();
        log.info("Updating post ID: {} by user ID: {}", postId, currentUserId);

        Post updatedPost = postService.updatePost(postId, request.getContent(), currentUserId);

        log.info("Successfully updated post ID: {}", postId);

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        Long currentUserId = getCurrentUserId();
        log.info("Deleting post ID: {} by user ID: {}", postId, currentUserId);

        postService.deletePost(postId, currentUserId);

        log.info("Successfully deleted post ID: {}", postId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Integer> getPostLikesCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        int likesCount = post.getLikes() != null ? post.getLikes().size() : 0;

        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<Integer> getPostCommentsCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        int commentsCount = post.getComments() != null ? post.getComments().size() : 0;

        return ResponseEntity.ok(commentsCount);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] ipHeaders = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
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