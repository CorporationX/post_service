package faang.school.postservice.controller.resource;

import faang.school.postservice.service.post.PostImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final PostImagesService postImagesService;

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable Long resourceId) {
        postImagesService.deleteImage(resourceId);
    }
}
