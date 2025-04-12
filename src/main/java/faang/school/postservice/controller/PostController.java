package faang.school.postservice.controller;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    @PostMapping("/createdraft")
    public PostDto createDraft(@RequestBody PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PostMapping("/createpost/{id}")
    public PostDto createPost(@PathVariable Long id) {
        return postService.createPost(id);
    }

    @PutMapping("/update")
    public PostDto updatePost(@RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/softDelete/{id}")
    public void softDeletePost(@PathVariable Long id) {
        postService.softDeletePost(id);
    }

    @GetMapping("/getpostbyid/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/getAllBlackPostsByAuthorId/{authorId}")
    public List<PostDto> getAllBlackPostsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllBlackPostsByAuthorId(authorId);
    }

    @GetMapping("/getAllBlackProjectsByAuthorId/{projectId}")
    public List<PostDto> getAllBlackProjectsByAuthorId(@PathVariable Long projectId) {
        return postService.getAllBlackProjectsByAuthorId(projectId);
    }

    @GetMapping("/getAllPublicPostsByAuthorId/{authorId}")
    public List<PostDto> getAllPublicPostsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllPublicPostsByAuthorId(authorId);
    }

    @GetMapping("/getAllPublicProjectsByAuthorId/{projectId}")
    public List<PostDto> getAllPublicProjectsByAuthorId(@PathVariable Long projectId) {
        return postService.getAllPublicProjectsByAuthorId(projectId);
    }
}
