package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @PostMapping("/post/draft")
    public PostDto create(@RequestBody  PostDto postDto) {
        validateCreate(postDto);
        return service.create(postDto);
    }

    @PutMapping("/post/publishing/{id}")
    public PostDto publish(@Min(1) @PathVariable("id") long id) {
        return service.publish(id);
    }

    @PutMapping("/post")
    public PostDto update(@RequestBody PostDto postDto) {
        if (postDto.id() < 1) {
            throw new DataValidationException("Передано некорректное id для обновления:" + postDto.id());
        }
        return service.update(postDto);
    }

    @DeleteMapping("/post/{id}")
    public void softlyDelete(@Min(1) @PathVariable("id") long id) {
        service.softlyDelete(id);
    }

    @GetMapping("/post/{id}")
    public PostDto getPost(@Min(1) @PathVariable("id") long id) {
        return service.getPost(id);
    }

    @GetMapping("/posts/draft/author/{authorId}")
    public List<PostDto> getDraftPostsForUser(@Min(1) @PathVariable("authorId") long authorId) {
        return service.getDraftPostsForUser(authorId);
    }

    @GetMapping("/posts/draft/project/{projectId}")
    public List<PostDto> getDraftPostsForProject(@Min(1) @PathVariable("projectId") long projectId) {
        return service.getDraftPostsForProject(projectId);
    }

    @GetMapping("/posts/author/{authorId}")
    public List<PostDto> getPublishedPostsForUser(@Min(1) @PathVariable("authorId") long authorId) {
        return service.getPublishedPostsForUser(authorId);
    }

    @GetMapping("/posts/project/{projectId}")
    public List<PostDto> getPublishedPostsForProject(@Min(1) @PathVariable("projectId")  long projectId) {
        return service.getPublishedPostsForProject(projectId);
    }

    private void validateCreate(PostDto postDto) {
        if (postDto.authorId() > 0 && postDto.projectId() > 0) {
            throw new DataValidationException("Автор у поста может быть только один");
        }

        if (postDto.authorId() < 1 && postDto.projectId() < 1) {
            throw new DataValidationException("У поста должен быть автор");
        }
    }


}
