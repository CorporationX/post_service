package faang.school.postservice.service.cache;

import faang.school.postservice.dto.redis.cash.CommentCache;
import faang.school.postservice.dto.redis.cash.PostCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class KekPostController {

    private final PostCacheService postCacheService;

    @GetMapping("/{id}")
    public PostCache get(@PathVariable long id) {
        return postCacheService.get(id);
    }

    @PostMapping
    public void save(@RequestBody PostCache postCache) {
        postCacheService.save(postCache);
    }

    @PutMapping
    public void update(@RequestBody PostCache postCache) {
        postCacheService.update(postCache);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        postCacheService.delete(id);
    }

    @PostMapping("/comment")
    public void addComment(@RequestBody CommentCache commentCache) {
        postCacheService.addComment(commentCache);
    }
}
