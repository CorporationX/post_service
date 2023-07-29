package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.EmptyContentInPostException;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping ("/publish/{id}")
    public PostDto publishPost(@PathVariable("id") long postId) {
        validatePostId(postId);
        return postService.publishPost(postId);
    }

    @PatchMapping("/update")
    public PostDto updatePost(@RequestBody PostDto updatePost) {
        validatePostId(updatePost.getId());
        validateData(updatePost);

        return postService.updatePost(updatePost);
    }

    @PatchMapping("/delete/{id}")
    public PostDto softDelete(@PathVariable("id") long postId) {
        validatePostId(postId);
        return postService.softDelete(postId);
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