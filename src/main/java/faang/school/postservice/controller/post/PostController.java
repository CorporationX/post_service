package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @ResponseBody
    public PostDto createDraftPost(@RequestBody @Valid PostDto postDto) {
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/publish")
    @ResponseBody
    public PostDto publishPost(@Valid PostDto postDto) {
        return postService.publishPost(postDto);
    }

    @PutMapping("/update")
    @ResponseBody
    public PostDto updatePost(@Valid PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public PostDto softDeletePost(@Valid PostDto postDto) {
        return postService.softDeletePost(postDto);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PostDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/user/{userId}")
    @ResponseBody
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable Long userId) {
        return postService.getAllDraftsByAuthorId(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    @ResponseBody
    public List<PostDto> getAllDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/published/user/{userId}")
    @ResponseBody
    public List<PostDto> getAllPublishedPostsByAuthorId(@PathVariable Long userId) {
        return postService.getAllPublishedPostsByAuthorId(userId);
    }

    @GetMapping({"/published/project/{projectId}"})
    @ResponseBody
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPublishedPostsByProjectId(projectId);
    }
}
