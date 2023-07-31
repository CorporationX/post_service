package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/")
public class PostController {
    private final PostService postService;

    @PutMapping
    public ResponsePostDto update(UpdatePostDto dto){
        return postService.update(dto);
    }
}
