package faang.school.postservice.controller.post;

import faang.school.postservice.service.post.PostImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostImagesService postImagesService;

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
