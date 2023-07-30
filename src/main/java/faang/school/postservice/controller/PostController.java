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
import org.springframework.web.bind.annotation.RestController;

@RestController("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        validateData(postDto);

        PostDto createdPostDto = postService.crateDraftPost(postDto);
        return createdPostDto;
    }

    @GetMapping("/publish/{id}")
    public PostDto publishPost(@PathVariable("id") long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/update")
    public PostDto updatePost(@RequestBody PostDto updatePost) {
        validateData(updatePost);

        return postService.updatePost(updatePost);
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