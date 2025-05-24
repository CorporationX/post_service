package faang.school.postservice.controller.like;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public void putLikeToPost(@PathVariable long postId) {
        try {
            likeService.putLikeToPost(postId);
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            ResponseEntity.badRequest().body(e.getMessage());
            return;
        };
        
        ResponseEntity.status(HttpStatus.NO_CONTENT);
    }
    
    @PostMapping("/comment/{commentId}")
    public void putLikeToComment(@PathVariable long commentId) {
        try {
            likeService.putLikeToComment(commentId);
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            ResponseEntity.badRequest().body(e.getMessage());
        };
        
        ResponseEntity.status(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{likeId}")
    public void deleteLike(@PathVariable long likeId) {
        try {
            likeService.deleteLike(likeId);
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            ResponseEntity.badRequest().body(e.getMessage());
        };
        
        ResponseEntity.status(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public void countLikes(PostDto postDto) {

    }

}
