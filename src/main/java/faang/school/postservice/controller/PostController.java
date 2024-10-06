package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PostMapping("/draft")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        Post createdPost = postService.createDraftPost(post);


        return postMapper.toDto(createdPost);
    }

    @PutMapping("/publish/{id}")
    public PostDto publishPost(@PathVariable Long id) {
        Post publishedPost = postService.publishPost(id);

        return postMapper.toDto(publishedPost);
    }

    @PatchMapping("/edit")
    public PostDto updatePost(@RequestBody PostDto postDto) {
        Post updatedPost = postService.updatePost(postDto.getId(), postDto.getContent());
        return postMapper.toDto(updatedPost);
    }

    @DeleteMapping("{id}")
    public PostDto deletePost(@PathVariable Long id) {
        return postMapper.toDto(postService.deletePost(id));
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        return postMapper.toDto(postService.getPostById(id));
    }

    @GetMapping("/user-drafts")
    public List<PostDto> getUserDrafts(@RequestParam(name = "user-id") long userId) {
        List<Post> drafts = postService.getUserDrafts(userId);
        return postMapper.toDto(drafts);
    }

    @GetMapping("/project-drafts")
    public List<PostDto> getProjectDrafts(@RequestParam(name = "project-id") long projectId) {
        List<Post> drafts = postService.getProjectDrafts(projectId);
        return postMapper.toDto(drafts);
    }

    @GetMapping("/user-published")
    public List<PostDto> getUserPublishedPosts(@RequestParam(name = "user-id") long userId) {
        List<Post> publishedPosts = postService.getUserPublishedPosts(userId);
        return postMapper.toDto(publishedPosts);
    }

    @GetMapping("/project-published")
    public List<PostDto> getProjectPublishedPosts(@RequestParam(name = "project-id") long projectId) {
        List<Post> publishedPosts = postService.getProjectPublishedPosts(projectId);
        return postMapper.toDto(publishedPosts);
    }

    @PutMapping("{post-id}/files")
    public ResourceDto addFileToPost(@PathVariable("post-id") Long postId,
                                     @RequestParam MultipartFile file) {
        ResourceEntity resourceEntity = resourceService.addFileToPost(file, postId);
        return resourceMapper.toResourceDto(resourceEntity);
    }

    @PutMapping("/files/update")
    public ResourceDto updateFileInPost(@RequestParam MultipartFile file,
                                        @RequestParam("resource-id") Long resourceId) {
        ResourceEntity updatedResourceEntity = resourceService.updateFileInPost(file, resourceId);
        return resourceMapper.toResourceDto(updatedResourceEntity);
    }

    @PutMapping("/files/remove")
    public void removeFileInPost(@RequestParam("resource-id") Long resourceId) {
        resourceService.removeFileInPost(resourceId);
    }

}