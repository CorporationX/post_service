package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.EmptyContentInPostException;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/drafts")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        validateData(postDto);

        PostDto createdPostDto = postService.crateDraftPost(postDto);
        return createdPostDto;
    }

    @PostMapping ("/publish/{id}")
    public PostDto publishPost(@PathVariable("id") long postId) {
        validatePostId(postId);
        return postService.publishPost(postId);
    }

    @PutMapping("/change")
    public PostDto updatePost(@RequestBody PostDto updatePost) {
        validatePostId(updatePost.getId());
        validateData(updatePost);

        return postService.updatePost(updatePost);
    }

    @PutMapping("/soft-delete/{id}")
    public PostDto softDelete(@PathVariable("id") long postId) {
        validatePostId(postId);
        return postService.softDelete(postId);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable("id") long postId) {
        validatePostId(postId);
        return postService.getPost(postId);
    }

    @GetMapping("drafts/users/{id}")
    public List<PostDto> getUserDrafts(@PathVariable("id") long userId) {
        validatePostId(userId);
        return postService.getUserDrafts(userId);
    }

    @GetMapping("/drafts/projects/{id}")
    public List<PostDto> getProjectDrafts(@PathVariable("id") long projectId) {
        validatePostId(projectId);
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("/users/{id}")
    public List<PostDto> getUserPosts(@PathVariable("id") long userId) {
        validatePostId(userId);
        return postService.getUserPosts(userId);
    }

    @GetMapping("/projects/{id}")
    public List<PostDto> getProjectPosts(@PathVariable("id") long projectId) {
        validatePostId(projectId);
        return postService.getProjectPosts(projectId);
    }

    private void validatePostId(long postId) {
        if (postId < 1) {
            throw new IncorrectIdException("Некорректрый id поста");
        }
    }

    private void validateData(PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new EmptyContentInPostException("Содержание поста не может быть пустым");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new IncorrectIdException("Нет автора поста");
        }
    }
}