package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.resource.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public PostDto createPostDraft(@RequestPart @Valid PostDto postDto,
                                   @RequestPart(value = "files", required = false) @Size(max = 10) List<MultipartFile> files) {
        return postService.createPostDraft(postDto, files);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId,
                              @RequestPart PostDto postDto,
                              @RequestPart(value = "files", required = false) @Size(max = 10) List<MultipartFile> files) {
        return postService.updatePost(postId, postDto, files);
    }
}