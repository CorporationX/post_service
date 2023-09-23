package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.ProjectContext;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
public class PostController {
    private final UserContext userContext;
    private final ProjectContext projectContext;
    private final PostService postService;

    @PostMapping("/create")
    public void createPost(@RequestBody @Valid PostDto postDto) {
        Long userId = userContext.getUserId();
        Long projectId = projectContext.getProjectId();
        postService.createPost(definitionId(userId, projectId, postDto));
    }

    @PostMapping("/publishByUser/{postId}")
    public void publishPost(@PathVariable long postId) {
        Long userId = userContext.getUserId();
        postService.publishPost(postId, userId);
    }

    @PostMapping("/publishByProject/{postId}")
    public void publishPostByProject(@PathVariable long postId) {
        Long projectId = projectContext.getProjectId();
        postService.publishPostByProject(postId, projectId);
    }

    @PostMapping("/update/{postId}")
    public void updatePost(@PathVariable long postId, @RequestBody @Valid PostDto postDto) {
        Long userId = userContext.getUserId();
        Long projectId = projectContext.getProjectId();
        postService.updatePost(postId, definitionId(userId, projectId, postDto));
    }

    @DeleteMapping("/deleteByUser/{postId}")
    public void deletePost(@PathVariable long postId) {
        long userId = userContext.getUserId();
        postService.deletePost(postId, userId);
    }

    @DeleteMapping("/deleteByProject/{postId}")
    public void deletePostByProject(@PathVariable long postId) {
        Long projectId = projectContext.getProjectId();
        postService.deletePostByProject(postId, projectId);
    }

    @GetMapping("/get/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/getAllUsersDrafts")
    public List<PostDto> getAllUsersDrafts() {
        long userId = userContext.getUserId();
        return postService.getAllUsersDrafts(userId);
    }

    @GetMapping("/getAllProjectDrafts")
    public List<PostDto> getAllProjectDrafts() {
        long projectId = projectContext.getProjectId();
        return postService.getAllProjectDrafts(projectId);
    }

    @GetMapping("/getAllUsersPublished")
    public List<PostDto> getAllUsersPublished() {
        long userId = userContext.getUserId();
        return postService.getAllUsersPublished(userId);
    }

    @GetMapping("/getAllProjectPublished")
    public List<PostDto> getAllProjectPublished() {
        long projectId = projectContext.getProjectId();
        return postService.getAllProjectPublished(projectId);
    }

    private PostDto definitionId(Long userId, Long projectId, PostDto postDto) {
        if (userId != null) {
            postDto.setAuthorId(userId);
        } else if (projectId != null) {
            postDto.setProjectId(projectId);
        }
        return postDto;
    }
}
