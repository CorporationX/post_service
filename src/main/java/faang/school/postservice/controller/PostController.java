package faang.school.postservice.controller;

import faang.school.postservice.service.PostCorrecter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostCorrecter postCorrecter;

    @PostMapping("/check-grammar")
    public ResponseEntity<String> correctUnpublishedPosts() {
        postCorrecter.correctUnpublishedPosts();
        return ResponseEntity.ok().body("Posts have been spell checked");
    }
}
