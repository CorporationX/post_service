package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostMediaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/posts/{postId}/media")
@RequiredArgsConstructor
public class PostMediaContorller {
    private final PostMediaService postMediaService;

    @PostMapping("/{images")
    public PostDto addImages(@PathVariable @NotNull @Positive Long postId,
                             @RequestBody @Valid MultipartFile file) {
        return postMediaService.addImages(postId, file);
    }
}
