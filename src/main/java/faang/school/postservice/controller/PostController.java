package faang.school.postservice.controller;

import faang.school.postservice.dto.post.DtosResponse;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.util.validator.PostControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostControllerValidator validator;

    @PostMapping("/add")
    ResponseEntity<PostDto> addPost(@RequestBody PostDto dto) {
        validator.validateToAdd(dto);

        return ResponseEntity.ok(postService.addPost(dto));
    }

    @PutMapping("/publish/{id}")
    ResponseEntity<PostDto> publishPost(@PathVariable Long id) {
        validator.validateToPublish(id);

        return ResponseEntity.ok(postService.publishPost(id));
    }

    @PutMapping("/update/{id}")
    ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody String content) {
        validator.validateToUpdate(id, content);

        return ResponseEntity.ok(postService.updatePost(id, content));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<PostDto> deletePost(@PathVariable Long id) {
        validator.validateToDelete(id);

        return ResponseEntity.ok(postService.deletePost(id));
    }

    @GetMapping("/get/{id}")
    ResponseEntity<PostDto> getPost(@PathVariable Long id){
        validator.validateToGet(id);

        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping("/author/drafts/{id}")
    ResponseEntity<DtosResponse> getDraftsByAuthorId(@PathVariable Long authorId){
        validator.validateToGetByAuthorId(authorId);

        return ResponseEntity.ok(new DtosResponse(postService.getDraftsByAuthorId(authorId)));
    }

    @GetMapping("/project/drafts/{id}")
    ResponseEntity<DtosResponse> getDraftsByProjectId(@PathVariable Long projectId){
        validator.validateToGetByProjectId(projectId);

        return ResponseEntity.ok(new DtosResponse(postService.getDraftsByProjectId(projectId)));
    }

    @GetMapping("/author/posts/{id}")
    ResponseEntity<DtosResponse> getPostsByAuthorId(@PathVariable Long authorId){
        validator.validateToGetByAuthorId(authorId);

        return ResponseEntity.ok(new DtosResponse(postService.getPostsByAuthorId(authorId)));
    }

    @GetMapping("/project/posts/{id}")
    ResponseEntity<DtosResponse> getPostsByProjectId(@PathVariable Long projectId){
        validator.validateToGetByProjectId(projectId);

        return ResponseEntity.ok(new DtosResponse(postService.getPostsByProjectId(projectId)));
    }
}

