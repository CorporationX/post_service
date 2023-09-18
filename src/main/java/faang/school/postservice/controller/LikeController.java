package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;
    private final LikeValidator likeValidator;

    @PostMapping("/create_for_post")
    public LikeDto makeLikeForPost (@RequestBody LikeDto likeDto) {
        likeValidator.validatePostId(likeDto);
        likeValidator.validateUserId(likeDto);
            return likeService.createLikeForPost(likeDto);
        }

    @DeleteMapping("/delete/{postId}")
    public void deleteLikeFromPost (@PathVariable long postId, @RequestBody LikeDto likeDto) {
        likeValidator.validatePostId(likeDto);
        likeValidator.validateUserId(likeDto);
            likeService.deleteLikeForPost(likeDto);
        }


    @PostMapping("/create_for_comment")
    public LikeDto makeLikeForComment(@RequestBody LikeDto likeDto) {
        likeValidator.validateCommentId(likeDto);
        likeValidator.validateUserId(likeDto);
        return likeService.createLikeForComment(likeDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public void deleteLikeFromComment (@PathVariable long commentId, @RequestBody LikeDto likeDto) {
        likeValidator.validateCommentId(likeDto);
        likeValidator.validateUserId(likeDto);
            likeService.deleteLikeForComment(likeDto);
        }
    }

