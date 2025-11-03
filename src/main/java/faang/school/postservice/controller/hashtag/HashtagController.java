package faang.school.postservice.controller.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.hashtag.HashtagService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
@Validated
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/{name}/posts")
    public Page<PostDto> findPostsByHashtag(
            @PathVariable @NotBlank String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return hashtagService.findPostsByHashtagName(name, page, size);
    }
}