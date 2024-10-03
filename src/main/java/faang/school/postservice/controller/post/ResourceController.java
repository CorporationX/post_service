package faang.school.postservice.controller.post;

import faang.school.postservice.service.post.PostImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final PostImagesService postImagesService;

    @PostMapping("/{id}/add")
    public void addImages(@PathVariable Long id,
                          @RequestPart("images") List<MultipartFile> images) {
        postImagesService.uploadPostImages(id, images);
    }

    @PatchMapping("/{id}/update")
    public void updateImages(@PathVariable Long id,
                             @RequestPart("images") List<MultipartFile> images) {
        postImagesService.updatePostImages(id, images);
    }

    @DeleteMapping("/{id}")
    public void deleteImages(@PathVariable Long id) {
        postImagesService.deleteImage(id);
    }
}
