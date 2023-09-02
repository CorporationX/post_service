package faang.school.postservice.validator;

import faang.school.postservice.annotations.ValidateLikeDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeDtoValidator implements ConstraintValidator<ValidateLikeDto, LikeDto> {
    private final PostService postService;
    private final CommentService commentService;

    @Override
    public boolean isValid(LikeDto likeDto, ConstraintValidatorContext context) {
        if (likeDto == null) {
            return false;
        }
        if (likeDto.getPostId() == null && likeDto.getCommentId() == null) {
            return false;
        }
        if (likeDto.getPostId() != null && likeDto.getCommentId() != null) {
            return false;
        }
        if (likeDto.getPostId() != null && !postService.existById(likeDto.getPostId())) {
            return false;
        }
        if (likeDto.getCommentId() != null && !commentService.existById(likeDto.getCommentId())) {
            return false;
        }
        return true;
    }
}