package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static faang.school.postservice.exception.message.PostValidationExceptionMessage.INCORRECT_POST_AUTHOR_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NULL_VALUED_POST_ID_EXCEPTION;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto createPost(@Valid @RequestBody PostDto postDto) {
        checkAuthorPresence(postDto);

        return postService.createPost(postDto);
    }

    @PostMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping
    public PostDto updatePost(@Valid @RequestBody PostDto postDto) {
        checkAuthorPresence(postDto);

        if (postDto.getId() == null) {
            throw new DataValidationException(NULL_VALUED_POST_ID_EXCEPTION.getMessage());
        }

        return postService.updatePost(postDto);
    }

    @DeleteMapping("/{postId}/delete")
    public void deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/draft/user/{userId}")
    public List<PostDto> getDraftsOfUser(@PathVariable long userId) {
        return postService.getDraftsOfUser(userId);
    }

    @GetMapping("/draft/project/{projectId}")
    public List<PostDto> getDraftsOfProject(@PathVariable long projectId) {
        return postService.getDraftsOfProject(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getPostsOfUser(@PathVariable long userId) {
        return postService.getPostsOfUser(userId);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDto> getPostsOfProject(@PathVariable long projectId) {
        return postService.getPostsOfProject(projectId);
    }

    private void checkAuthorPresence(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if ((authorId == null && projectId == null) || (authorId != null && projectId != null)) {
            throw new DataValidationException(INCORRECT_POST_AUTHOR_EXCEPTION.getMessage());
        }
    }
}
