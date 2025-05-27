package faang.school.postservice.controller;

import faang.school.postservice.dto.likesystem.LikeDto;
import faang.school.postservice.service.LikeSystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeSystemController {
    private final LikeSystemService likeSystemService;

    @PostMapping(path = "/post")
    public LikeDto addLikePost(@Valid @RequestBody LikeDto likeDto){
        return likeSystemService.addLikePost(likeDto);
    }

    @DeleteMapping(path = "/post/{id}")
    public LikeDto deleteLikePost(@PathVariable("id") Long id){
        return likeSystemService.deleteLikePost(id);
    }

    @PostMapping(path = "/comment")
    public LikeDto addLikeComment(@Valid @RequestBody LikeDto likeDto){
        return likeSystemService.addLikeComment(likeDto);
    }

    @DeleteMapping(path = "/comment/{id}")
    public LikeDto deleteLikeComment(@PathVariable("id") Long id){
        return likeSystemService.deleteLikeComment(id);
    }
}
