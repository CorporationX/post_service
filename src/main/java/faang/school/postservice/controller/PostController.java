package faang.school.postservice.controller;

import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.exception.WrongInputException;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j

@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/draftPost")
    public PostDto createDraftPost(@RequestBody @Validated PostDto dto) {
        if (isAuthorOrProjectNotNull(dto)) {
            return postService.createDraftPost(dto);
        }
        return null;
    }

    @GetMapping("/draft/{draftId}")
    public PostDto publishPost(@PathVariable @NonNull @Positive Long draftId) {
        return postService.publishPost(draftId);
    }

    @PutMapping("/post/{postId}")
    public PostDto updatePost(@PathVariable @NonNull @Positive Long postId, @RequestBody PostDto postDto) {
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/post/{postId}")
    public PostDto deletePost(@PathVariable @NonNull @Positive Long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/post/{postId}")
    public PostDto getPost(@PathVariable @NonNull @Positive Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/drafts/users/{publisherId}")
    public List<PostDto> getDraftPostsForUser(@PathVariable @NonNull @Positive Long publisherId) {
        PostDto postDto = PostDto.builder()
                .content("notUsed")
                .published(false)
                .authorId(publisherId)
                .build();
        return postService.getPostsSortedByDate(postDto);
    }

    @GetMapping("/drafts/projects/{publisherId}")
    public List<PostDto> getDraftPostsForProject(@PathVariable @NonNull @Positive Long publisherId) {
        PostDto postDto = PostDto.builder()
                .content("notUsed")
                .published(false)
                .projectId(publisherId)
                .build();
        return postService.getPostsSortedByDate(postDto);
    }

    @GetMapping("/posts/users/{publisherId}")
    public List<PostDto> getPostsForUser(@PathVariable @NonNull @Positive Long publisherId) {
        PostDto postDto = PostDto.builder()
                .content("notUsed")
                .published(true)
                .authorId(publisherId)
                .build();
        return postService.getPostsSortedByDate(postDto);
    }

    @GetMapping("/posts/projects/{publisherId}")
    public List<PostDto> getPostsForProjects(@PathVariable @NonNull @Positive Long publisherId) {
        PostDto postDto = PostDto.builder()
                .content("notUsed")
                .published(true)
                .projectId(publisherId)
                .build();
        return postService.getPostsSortedByDate(postDto);
    }

    private boolean isAuthorOrProjectNotNull(PostDto dto) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null) {
            log.error("Not allowed to assign one post to an user and a project simultaneously");
            throw new WrongInputException("Not allowed to assign one post to an user and a project simultaneously");
        } else {
            return true;
        }
    }
}
