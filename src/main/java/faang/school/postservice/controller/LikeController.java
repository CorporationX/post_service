package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {

    private final LikeService likeService;
    private final UserContext userContext;

    @PostMapping("/posts/{postId}")
    public LikeResponseDto addLikeToPost(@PathVariable @Positive Long postId) {

        log.info("Received request [Post]LikeController#addLikeToPost({})", postId);
        return likeService.addLikeToPost(userContext.getUserId(), postId);
    }

    @DeleteMapping("/posts/{postId}")
    public LikeResponseDto removeLikeFromPost(@PathVariable @Positive Long postId) {

        log.info("Received request [Delete]LikeController#removeLikeFromPost({})", postId);
        return likeService.removeLikeFromPost(userContext.getUserId(), postId);
    }

    @PostMapping("/comments/{commentId}")
    public LikeResponseDto addLikeToComment(@PathVariable @Positive Long commentId) {

        log.info("Received request [Post]LikeController#addLikeToComment({})", commentId);
        return likeService.addLikeToComment(userContext.getUserId(), commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    public LikeResponseDto removeLikeFromComment(@PathVariable @Positive Long commentId) {

        log.info("Received request [Delete]LikeController#removeLikeFromComment({})", commentId);
        return likeService.removeLikeFromComment(userContext.getUserId(), commentId);
    }
}
