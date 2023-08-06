package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.response.DtosResponse;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    PostDto addPost(@Valid @RequestBody PostDto dto) {
        PostDto resultDto = postService.addPost(dto);

        return resultDto;
    }

    @PutMapping("/publish/{id}")
    @ResponseStatus(HttpStatus.OK)
    PostDto publishPost(@PathVariable Long id) {
        PostDto resultDto = postService.publishPost(id);

        return resultDto;
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    PostDto updatePost(@PathVariable Long id, @RequestBody String content) {
        PostDto resultDto = postService.updatePost(id, content);

        return resultDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    PostDto deletePost(@PathVariable Long id) {
        PostDto resultDto = postService.deletePost(id);

        return resultDto;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    PostDto getPost(@PathVariable Long id){
        PostDto resultDto = postService.getPost(id);

        return resultDto;
    }

    @GetMapping("/author/drafts/{id}")
    @ResponseStatus(HttpStatus.OK)
    DtosResponse getDraftsByAuthorId(@PathVariable Long id){
        List<PostDto> resultDtos = postService.getDraftsByAuthorId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/project/drafts/{id}")
    @ResponseStatus(HttpStatus.OK)
    DtosResponse getDraftsByProjectId(@PathVariable Long id){
        List<PostDto> resultDtos = postService.getDraftsByProjectId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/author/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    DtosResponse getPostsByAuthorId(@PathVariable Long id){
        List<PostDto> resultDtos = postService.getPostsByAuthorId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/project/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    DtosResponse getPostsByProjectId(@PathVariable Long id){
        List<PostDto> resultDtos = postService.getPostsByProjectId(id);

        return new DtosResponse(resultDtos);
    }
}

