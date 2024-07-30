package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.exception.WrongInputException;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static faang.school.postservice.exception.MessageError.NOT_ALLOWED_EMPTY_POST_MESSAGE;

@RestController
@RequiredArgsConstructor
@Slf4j

@Validated
public class PostController {
    private final PostService postService;
    @PostMapping("/draftPost")
    public PostDto createDraftPost(@RequestBody PostDto dto){
        if (validateCreatingPost(dto)){
            return postService.createDraftPost(dto);
        } return null;
    }

    @GetMapping ("/draft/{draftId}")
    public PostDto publishPost(@PathVariable @NonNull @Positive Long draftId){
        return postService.publishPost(draftId);
    }

    @PutMapping("/post/{postId}")
    public PostDto updatePost (@PathVariable @NonNull @Positive Long postId, @RequestBody PostDto postDto){
        if (postDto.getContent()==null || postDto.getContent().isEmpty() || postDto.getContent().isBlank()){
            log.info("Tried to update Post with null or empty message. Not allowed to have empty content in post");
            throw new WrongInputException(NOT_ALLOWED_EMPTY_POST_MESSAGE);
        }
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/post/{postId}")
    public PostDto deletePost(@PathVariable @NonNull @Positive Long postId){
        return postService.deletePost(postId);
    }

    @GetMapping("/post/{postId}")
    public PostDto getPost(@PathVariable @NonNull @Positive Long postId){
       return postService.getPost(postId);
    }

    @GetMapping("/drafts/users/{publisherId}")
    public List<PostDto> getDraftPostsForUser(@PathVariable @NonNull @Positive Long publisherId){
        PostDto postDto = initPostDto(false,publisherId,"User");
        return postService.getSortedPosts(postDto);
    }

    @GetMapping("/drafts/projects/{publisherId}")
    public List<PostDto> getDraftPostsForProject(@PathVariable @NonNull @Positive Long publisherId){
        PostDto postDto = initPostDto(false,publisherId,"Project");
        return postService.getSortedPosts(postDto);
    }

    @GetMapping("/posts/users/{publisherId}")
    public List<PostDto> getPostsForUser(@PathVariable @NonNull @Positive Long publisherId){
        PostDto postDto = initPostDto(true,publisherId,"User");
        return postService.getSortedPosts(postDto);
    }

    @GetMapping("/posts/projects/{publisherId}")
    public List<PostDto> getPostsForProjects(@PathVariable @NonNull @Positive Long publisherId){
        PostDto postDto = initPostDto(true,publisherId,"Project");
        return postService.getSortedPosts(postDto);
    }

    private boolean validateCreatingPost(PostDto dto){
        if (dto.getAuthorId()!=null && dto.getProjectId()!=null){
            log.error("Not allowed to assign one post to an user and a project simultaneously");
            throw new WrongInputException("Not allowed to assign one post to an user and a project simultaneously");
        } else if (dto.getContent()==null || dto.getContent().isEmpty() || dto.getContent().isBlank()) {
            log.error("Tried to create Post with null or empty message");
            throw new WrongInputException("Message could not be empty");
        } else {
            return true;
        }
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
