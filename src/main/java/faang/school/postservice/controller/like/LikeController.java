package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDtoForComment;
import faang.school.postservice.dto.like.LikeDtoForPost;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.OK)
    public void likePost(@RequestBody @Valid LikeDtoForPost likeDtoForPost) {
        likeService.addLikeByPost(likeDtoForPost);
    }

    @DeleteMapping("/post")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeFromPost(@RequestBody @Valid LikeDtoForPost likeDtoForPost) {
        likeService.deleteLikeFromPost(likeDtoForPost);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public void likeComment(@RequestBody @Valid LikeDtoForComment likeDtoForComment) {
        likeService.addLikeByComment(likeDtoForComment);

    }

    @DeleteMapping("/comment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteLikeFromComment(@RequestBody @Valid LikeDtoForComment likeDtoForComment) {
        likeService.deleteLikeFromComment(likeDtoForComment);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/ids")
    public List<Long> getAllPostLikeIds() {
        return likeService.getAllPostLikeIds();
    }
}