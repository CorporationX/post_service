package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.EmptyContentInPostException;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        validateData(postDto);

        PostDto createdPostDto = postService.crateDraftPost(postDto);
        return createdPostDto;
    }

    @GetMapping("/post/{postId}")
    public PostDto publishPost(@PathVariable("postId") long postId) {
        if (postId < 1) {
            throw new IncorrectIdException("Некорректрый id поста");
        }
        return postService.publishPost(postId);
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