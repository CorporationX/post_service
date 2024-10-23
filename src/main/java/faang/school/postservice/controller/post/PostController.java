package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostImagesService;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostImagesService postImagesService;

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable @Positive Long postId){
        return postService.getPost(postId);
    }

    @PostMapping("/{postId}")
    public void addImages(@PathVariable Long postId,
                          @RequestPart("images") List<MultipartFile> images) {
        postImagesService.uploadPostImages(postId, images);
    }

    @PutMapping("/{postId}")
    public void updateImages(@PathVariable Long postId,
                             @RequestPart("images") List<MultipartFile> images) {
        postImagesService.updatePostImages(postId, images);
    }
}
