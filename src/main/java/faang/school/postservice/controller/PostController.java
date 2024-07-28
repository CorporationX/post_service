package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("Post Controller")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserContext userContext;
    @PostMapping("/draftPost")
    public PostDto createDraftPost(@RequestBody PostDto dto){

    }

    @GetMapping ("/draft/{draftId}")
    public PostDto publishPost(@PathVariable @NonNull @Positive @Valid Long draftId){

    }

    @PutMapping("/post/{postId}")
    public PostDto updatePost (@PathVariable @NonNull @Positive @Valid Long postId, @RequestBody PostDto postDto){

    }

    @DeleteMapping("/post/{postId}")
    public PostDto deletePost(@PathVariable Long postId, @RequestBody PostDto postDto){

    }

    @GetMapping("/post/{postId}")
    public PostDto getPost(@PathVariable Long postId){

    }

    @GetMapping("/drafts/users/{publisherId}")
    public List<PostDto> getDraftPostsForUser(@PathVariable Long publisherId){
        PostDto postDto = initPostDto(false,publisherId,"User");
    }

    @GetMapping("/drafts/projects/{publisherId}")
    public List<PostDto> getDraftPostsForProject(@PathVariable Long publisherId){
        PostDto postDto = initPostDto(false,publisherId,"Project");
    }

    @GetMapping("/posts/users/{publisherId}")
    public List<PostDto> getPostsForUser(@PathVariable Long publisherId){
        PostDto postDto = initPostDto(true,publisherId,"User");
    }

    @GetMapping("/posts/projects/{publisherId}")
    public List<PostDto> getPostsForProjects(@PathVariable Long publisherId){
        PostDto postDto = initPostDto(true,publisherId,"Project");
    }

    private PostDto initPostDto(boolean published, Long id, String publisher){
        PostDto postDto = new PostDto();
        postDto.setDeleted(false);
        postDto.setPublished(published);
        switch (publisher) {
            case "User" -> postDto.setAuthorId(id);
            case "Project" -> postDto.setProjectId(id);
        }
    return postDto;
    }

}
