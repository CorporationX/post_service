package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostHashtagService;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/post")
@RestController
public class PostController {

    private final PostService postService;
    private final PostHashtagService postHashtagService;
    private final PostMapper postMapper;

    @Value("${app.number-posts-by-hashtags}")
    private int POSTS_NUMBER;

    @PostMapping("/create-by-user/{user-id}")
    public ResponseEntity<Void> createPostByUserId(
            @PathVariable("user-id") final Long userId,
            @RequestBody @Valid RequestPostDto requestPostDto) {

        Post post = postMapper.toEntity(requestPostDto);
        postService.createPostByUserId(userId, post);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/create-by-project/{project-id}")
    public ResponseEntity<Void> createPostByProjectId(
            @PathVariable("project-id") final Long projectId,
            @RequestBody @Valid RequestPostDto requestPostDto) {

        Post post = postMapper.toEntity(requestPostDto);
        postService.createPostByProjectId(projectId, post);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/publish-post/{post-id}")
    public ResponseEntity<Void> publishPost(
            @PathVariable("post-id") final Long postId) {

        postService.publishPost(postId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/update/{post-id}")
    public ResponseEntity<Void> updateSubProject(
            @PathVariable("post-id") final Long postId,
            @RequestBody @Valid RequestPostDto requestPostDto) {

        Post post = postMapper.toEntity(requestPostDto);
        postService.updatePost(postId, post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{post-id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("post-id") final Long postId) {

        postService.softDeletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/post/{post-id}")
    public ResponseEntity<ResponsePostDto> getPost(
            @PathVariable("post-id") final Long postId) {

        Post post = postService.getPostById(postId);
        ResponsePostDto responsePostDto = postMapper.toDto(post);

        return new ResponseEntity<>(responsePostDto, HttpStatus.OK);
    }

    @GetMapping("/not-published-posts-by-user/{user-id}")
    public ResponseEntity<List<ResponsePostDto>> getNotPublishedPostsByUser(
            @PathVariable("user-id") final Long userId) {

        final List<Post> notPublishedPosts = postService.getNotPublishedPostsByUser(userId);
        List<ResponsePostDto> responsePostDtos = postMapper.toDto(notPublishedPosts);

        return new ResponseEntity<>(responsePostDtos, HttpStatus.OK);
    }

    @GetMapping("/not-published-posts/{project-id}")
    public ResponseEntity<List<ResponsePostDto>> getNotPublishedPostsByProject(
            @PathVariable("project-id") final Long projectId) {

        final List<Post> notPublishedPosts = postService.getNotPublishedPostsByProject(projectId);
        List<ResponsePostDto> responsePostDtos = postMapper.toDto(notPublishedPosts);

        return new ResponseEntity<>(responsePostDtos, HttpStatus.OK);
    }

    @GetMapping("/published-posts-by-user/{user-id}")
    public ResponseEntity<List<ResponsePostDto>> getPublishedPostsByUser(
            @PathVariable("user-id") final Long userId) {
        final List<Post> notPublishedPosts = postService.getPublishedPostsByUser(userId);
        List<ResponsePostDto> responsePostDtos = postMapper.toDto(notPublishedPosts);

        return new ResponseEntity<>(responsePostDtos, HttpStatus.OK);
    }

    @GetMapping("/published-posts/{project-id}")
    public ResponseEntity<List<ResponsePostDto>> getPublishedPostsByProject(
            @PathVariable("project-id") final Long projectId) {

        final List<Post> notPublishedPosts = postService.getPublishedPostsByProject(projectId);
        List<ResponsePostDto> responsePostDtos = postMapper.toDto(notPublishedPosts);

        return new ResponseEntity<>(responsePostDtos, HttpStatus.OK);
    }

    @GetMapping("/posts-by-hashtag")
    public ResponseEntity<List<ResponsePostDto>> getPostsByHashtag(
            @RequestParam(name = "hashtag") @NonNull String hashtag) {

        List<Post> postsByHashtag = postHashtagService.getLimitedPostsByHashtag(hashtag, POSTS_NUMBER);
        List<ResponsePostDto> posts = postMapper.toDto(postsByHashtag);

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}