package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.HashTagRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final PostMapper mapper;

    @PostMapping
    public PostResponseDto createPost(@Valid @RequestBody CreatePostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        Post result = postService.create(post);

        return mapper.toDto(result);
    }

    @PatchMapping
    public PostResponseDto updatePost(@Valid @RequestBody UpdatePostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        Post result = postService.update(post);

        return mapper.toDto(result);
    }

    @PutMapping("/{postId}/publish")
    public PostResponseDto publishPost(@PathVariable Long postId) {
        Post result = postService.publish(postId);
        return mapper.toDto(result);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        postService.delete(postId);
    }

    @GetMapping("/search/{authorId}/author")
    public List<PostResponseDto> searchPostsByAuthor(@PathVariable Long authorId, FilterPostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        post.setAuthorId(authorId);
        List<Post> posts = postService.searchByAuthor(post);

        return mapper.listEntitiesToListDto(posts);
    }

    @GetMapping("/search/{projectId}/project")
    public List<PostResponseDto> searchPostsByProject(@PathVariable Long projectId, FilterPostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        post.setProjectId(projectId);
        List<Post> posts = postService.searchByProject(post);

        return mapper.listEntitiesToListDto(posts);
    }

    @GetMapping
    public List<PostCacheDto> findAllByHashTags(@RequestParam(name ="hash_tag") String hashTag,
                                                @RequestParam(name = "start") int start,
                                                @RequestParam(name = "end") int end) {
        return postService.findInRangeByHashTag(hashTag, start, end);
    }
}
