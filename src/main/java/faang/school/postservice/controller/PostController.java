package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostShortContentDto;
import faang.school.postservice.mapper.PostShortContentMapper;
import faang.school.postservice.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    private PostService postService;
    private PostShortContentMapper postShortContentMapper;

    @GetMapping("/{postId}")
    public PostShortContentDto getShortPostContent(@PathVariable Long postId) {
        return postShortContentMapper.toDto(postService.getPost(postId));
    }
}
