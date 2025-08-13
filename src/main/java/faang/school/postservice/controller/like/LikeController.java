package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.like.LikeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * LikeController — описание класса.
 * <p>
 *
 * </p>*
 *
 * @author bozya
 * @since 12.08.2025
 */
@Slf4j
@RequestMapping("/likes")
@RestController
public class LikeController {
    LikeServiceImpl likeService;

    @GetMapping("/post/{postId}/like")
    ResponseEntity<List<UserDto>> getAllLikesFromUsersToPost(@PathVariable Long postId) {
        List<UserDto> users = likeService.getUsersWhoLikedPost(postId);

        if(users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/post/{postId}/comment/{commentId}/like")
    ResponseEntity<List<UserDto>> getAllLikesFromUsersToComment(@PathVariable Long postId, @PathVariable Long commentId) {
        List<UserDto> users = likeService.getUsersWhoLikedComment(postId, commentId);

        if(users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }
}