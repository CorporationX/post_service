package faang.school.postservice.controller.post;

import faang.school.postservice.service.post.SpellCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.default}/post")
public class PostController {
    private final SpellCheckService spellCheckService;

    @PutMapping("/{postId}/content")
    public void correctTextInPost(@PathVariable("postId") Long postId) {
        spellCheckService.spellCheckPostById(postId);
    }

}
