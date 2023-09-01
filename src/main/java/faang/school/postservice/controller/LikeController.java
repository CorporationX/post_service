package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/like")
@Tag(name = "LikeController", description = "Контроллер получает лайки на постах и комментариях")
public class LikeController {
    private final LikeService service;

    @Operation(summary = "Get Post Likes")
    @GetMapping("/post/{id}")
    public List<UserDto> getPostLikes(@PathVariable long id) {
        return service.getPostLikes(id);
    }

    @Operation(summary = "Get Comment Likes")
    @GetMapping("/comment/{id}")
    public List<UserDto> getCommentLikes(@PathVariable long id) {
        return service.getCommentLikes(id);
    }
}
