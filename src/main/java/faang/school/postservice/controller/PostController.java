package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/createPost")
    public PostDto create(@RequestBody @Valid PostDto postDto) {
        return postService.create(postDto);
    }

    @PutMapping("/publish/{id}")
    public PostDto publish(@PathVariable Long id) {
        return postService.publish(id);
    }

    @PutMapping("/update/{id}")
    public PostDto update(@RequestBody PostDto postDto, @PathVariable Long id) {
        postDto.setId(id);
        return postService.update(postDto, id);
    }

    @PutMapping("/delete/{id}")
    public PostDto delete(@PathVariable Long id) {
        return postService.delete(id);
    }

    @GetMapping("/get/{id}")
    public PostDto get(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/getAuthorPosts/{id}")
    public List<PostDto> getAllNonPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllNonPublishedByAuthorId(id);
    }

    @GetMapping("/getProjectPosts/{id}")
    public List<PostDto> getAllNonPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllNonPublishedByProjectId(id);
    }

    @GetMapping("/getPublishedByAuthorId/{id}")
    public List<PostDto> getAllPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllPublishedByAuthorId(id);
    }

    @GetMapping("/getPublishedByProjectId/{id}")
    public List<PostDto> getAllPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllPublishedByProjectId(id);
    }

    private boolean validatePostCreate(PostDto postDto) {
        return ((postDto.getAuthorId() == null && postDto.getProjectId() != null) || (postDto.getAuthorId() != null && postDto.getProjectId() == null)); //&&
                //(postDto.getContent() != null && !postDto.getContent().isBlank());
    }

}
