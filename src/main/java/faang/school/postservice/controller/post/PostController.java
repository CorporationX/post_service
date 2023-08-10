package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("{postId}/publish")
    public ResponsePostDto publish(@PathVariable Long postId){
        return postService.publish(postId);
    }
  
    @PutMapping
    public ResponsePostDto update(UpdatePostDto dto){
        return postService.update(dto);
    }

    @GetMapping("{id}")
    public ResponsePostDto getById(@PathVariable Long id){
        return postService.getById(id);
    }

    @DeleteMapping("{id}")
    public ResponsePostDto softDelete(@PathVariable Long id){
        return postService.softDelete(id);
    }
  
    @PostMapping
    public ResponsePostDto createDraft(@Valid @RequestBody CreatePostDto dto){
        return postService.createDraft(dto);
    }
}
