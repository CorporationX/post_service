package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.context.UserHeaderFilter;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final LikeService service;
    private final UserContext userContext;

    @PostMapping("/")
    public LikeDto likePost(@Valid LikeDto likeDto){
        return service.likePost(likeDto, userContext.getUserId());
    }

    @DeleteMapping("/")
    public LikeDto removeLikeFromPost(@Valid LikeDto likeDto){
        return service.removeLikeFromPost(likeDto, userContext.getUserId());
    }

    @PostMapping("/comment")
    public LikeDto likeComment(@Valid LikeDto likeDto){
        return service.likeComment(likeDto, userContext.getUserId());
    }

    @DeleteMapping("/comment")
    public LikeDto removeLikeFromComment(@Valid LikeDto likeDto){
        return service.removeLikeFromComment(likeDto, userContext.getUserId());
    }
}
