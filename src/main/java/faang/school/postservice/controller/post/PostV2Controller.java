package faang.school.postservice.controller.post;

import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostV2CreateDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.post.PostV2UpdateDto;
import faang.school.postservice.feed.FeedDto;
import faang.school.postservice.service.post.PostFeedService;
import faang.school.postservice.service.post.PostV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/posts")
@RestController
public class PostV2Controller implements PostV2Api {

    private final PostV2Service postV2Service;
    private final PostFeedService postFeedService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostV2Dto createPostAsDraft(@Valid @RequestBody PostV2CreateDto postV2CreateDto) {
        return postV2Service.createPostAsDraft(postV2CreateDto);
    }

    @PostMapping("/{postId}/publication")
    public PostV2Dto publishPost(@PathVariable Long postId) {
        return postV2Service.publishPost(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePostSoftly(@PathVariable Long postId) {
        postV2Service.deletePostSoftly(postId);
    }

    @GetMapping
    public PageResponse<PostV2Dto> findAllPublished(
            @RequestParam(required = false) Long authorId,
            @PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postV2Service.findAllPublishedByFilter(authorId, pageable);
    }

    @GetMapping("/{postId}")
    public PostV2Dto findById(@PathVariable Long postId) {
        return postV2Service.findById(postId);
    }

    @GetMapping("/drafts-by-author")
    public PageResponse<PostV2Dto> findAllDraftsByAuthor(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postV2Service.findAllDraftsByAuthor(pageable);
    }

    @PatchMapping("/{postId}")
    public PostV2Dto updatePost(@PathVariable Long postId, @Valid @RequestBody PostV2UpdateDto postV2UpdateDto) {
        return postV2Service.updatePost(postId, postV2UpdateDto);
    }

    @GetMapping("/feed")
    public FeedDto getFeed(
            @RequestParam(required = false) String lastPostId,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return postFeedService.getFeed(lastPostId, limit);
    }
}
