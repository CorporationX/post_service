package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/")
public class PostController {
    private final PostService postService;

    @GetMapping("draft/by-author/{authorId}")
    public List<ResponsePostDto> getAllDraftByAuthor(@PathVariable Long authorId){
        return postService.getAllDraftByAuthor(authorId);
    }
    @GetMapping("published/by-author/{authorId}")
    public List<ResponsePostDto> getAllPublishedByAuthor(@PathVariable Long authorId){
        return postService.getAllPublishedByAuthor(authorId);
    }

    @GetMapping("draft/by-project/{projectId}")
    public List<ResponsePostDto> getAllDraftByProject(@PathVariable Long projectId){
        return postService.getAllDraftByProject(projectId);
    }
    @GetMapping("published/by-project/{projectId}")
    public List<ResponsePostDto> getAllPublishedByProject(@PathVariable Long projectId){
        return postService.getAllPublishedByProject(projectId);
    }
}
