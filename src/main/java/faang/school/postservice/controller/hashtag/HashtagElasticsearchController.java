package faang.school.postservice.controller.hashtag;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.service.hashtag.HashtagElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/es/hashtags")
@RequiredArgsConstructor
public class HashtagElasticsearchController {

    private final HashtagElasticsearchService hashtagElasticsearchService;

    @PostMapping("/save")
    public HashtagDto saveHashtag(@RequestBody HashtagDto hashtagDto) {
        return hashtagElasticsearchService.save(hashtagDto);
    }

    @GetMapping("/find/{id}")
    public Optional<HashtagDto> findHashtagById(@PathVariable Long id) {
        return hashtagElasticsearchService.findById(id);
    }

    @GetMapping("/findAll")
    public List<HashtagDto> findAllHashtags() {
        return hashtagElasticsearchService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteHashtagById(@PathVariable Long id) {
        hashtagElasticsearchService.deleteById(id);
    }
}