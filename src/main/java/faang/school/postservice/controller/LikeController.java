package faang.school.postservice.controller;

import faang.school.postservice.LikeMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeMapper likeMapper;
    private final LikeService likeService;

    @PostMapping("/add/topost")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToPost(@RequestBody @Validated LikeDto likeDto) {
        Like like = likeMapper.toEntity(likeDto);
        likeService.addToPost(like);

        return likeMapper.toDto(like);
    }
}
