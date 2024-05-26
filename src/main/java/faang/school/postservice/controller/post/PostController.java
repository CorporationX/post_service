package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.SpellCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.default}/post")
public class PostController {
    private final SpellCheckService spellCheckService;
    private final PostService postService;

    @PutMapping("/{postId}/content")
    public void correctTextInPost(@PathVariable("postId") Long postId) {
        spellCheckService.spellCheckPostById(postId);
    }

    @PostMapping
    public PostDto createPost(@RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PostMapping("/publish/{postId}")
    public PostDto publishPost(@PathVariable("postId") long postId) {
        return postService.publishedPost(postId);
    }

    @PutMapping("/{postId}/{content}")
    public PostDto updatePost(@PathVariable("postId") long postId, @PathVariable("content") String content) {
        return postService.updatePost(postId, content);
    }

    @PutMapping("/markDelete/{postId}")
    public PostDto markDelete(@PathVariable("postId") long postId) {
        return postService.markDeletePost(postId);
    }

    @GetMapping("/notDeleteByAuthor/{authorId}")
    public List<PostDto> getPostsNotDeleteByAuthorId (@PathVariable("authorId") long authorId) {
        return postService.getPostsNotDeleteByAuthorId(authorId);
    }

    @GetMapping("/notDeleteByProject/{projectId}")
    public List<PostDto> getPostsNotDeleteByProjectId (@PathVariable("projectId") long projectId) {
        return postService.getPostsNotDeleteByProjectId(projectId);
    }

    @GetMapping("/publishedByAuthor/{authorId}")
    public List<PostDto> getPostsPublishedByAuthorId (@PathVariable("authorId") long authorId) {
        return postService.getPostsPublishedByAuthorId(authorId);
    }

    @GetMapping("/notDeleteByAuthor/{authorId}")
    public List<PostDto> getPostsPublishedByProjectId (@PathVariable("projectId") long projectId) {
        return postService.getPostsPublishedByProjectId(projectId);
    }

    @GetMapping("{postId}")
    public PostDto getPostById(@PathVariable("postId") long postId) {
        return postService.getPostById(postId);
    }
}
