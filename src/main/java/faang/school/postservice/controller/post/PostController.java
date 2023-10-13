package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.DtosResponse;
import faang.school.postservice.dto.post.PictureDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Picture;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.util.exception.DataValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final HashtagService hashtagService;

    @PostMapping("/")
    PostDto addPost(@Valid @RequestBody PostDto dto) {
        PostDto resultDto = postService.addPost(dto);

        return resultDto;
    }

    @PutMapping("/publish/{id}")
    PostDto publishPost(@PathVariable Long id) {
        PostDto resultDto = postService.publishPost(id);

        return resultDto;
    }

    @PutMapping("/update/{id}")
    PostDto updatePost(@PathVariable Long id, @RequestBody String content) {
        PostDto resultDto = postService.updatePost(id, content);

        return resultDto;
    }

    @DeleteMapping("/{id}")
    PostDto deletePost(@PathVariable Long id) {
        PostDto resultDto = postService.deletePost(id);

        return resultDto;
    }

    @GetMapping("/{id}")
    PostDto getPost(@PathVariable Long id) {
        PostDto resultDto = postService.getPost(id);

        return resultDto;
    }

    @GetMapping("/author/drafts/{id}")
    DtosResponse getDraftsByAuthorId(@PathVariable Long id) {
        List<PostDto> resultDtos = postService.getDraftsByAuthorId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/project/drafts/{id}")
    DtosResponse getDraftsByProjectId(@PathVariable Long id) {
        List<PostDto> resultDtos = postService.getDraftsByProjectId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/author/posts/{id}")
    DtosResponse getPostsByAuthorId(@PathVariable Long id) {
        List<PostDto> resultDtos = postService.getPostsByAuthorId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/project/posts/{id}")
    DtosResponse getPostsByProjectId(@PathVariable Long id) {
        List<PostDto> resultDtos = postService.getPostsByProjectId(id);

        return new DtosResponse(resultDtos);
    }

    @GetMapping("/byhashtag/{hashtag}")
    public List<PostDto> getByHashtag(@PathVariable String hashtag) {
        if (hashtag.length() > 255) throw new DataValidationException("Hashtag in too long");
        return hashtagService.getPostByHashtag(hashtag);
    }

    @PutMapping("/{postId}/image")
    public Picture addPicture(@PathVariable long postId, @RequestParam("file") MultipartFile file) {
        return postService.uploadPicture(postId, file);
    }

    @DeleteMapping("/image")
    public void deletePicture(@RequestBody PictureDto pictureDto) {
        postService.deletePicture(pictureDto);
    }
}


