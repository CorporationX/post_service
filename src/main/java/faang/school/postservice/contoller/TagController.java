package faang.school.postservice.contoller;

import faang.school.postservice.dto.tag.TagAddDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
import faang.school.postservice.dto.tag.TagSearchDto;
import faang.school.postservice.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/post/{id}")
    public ResponseEntity<List<TagDto>> getTagsForPost(@PathVariable Long id) {
        return tagService.getTagsForPost(id);
    }

    @PostMapping(value = "/add")
    public ResponseEntity<List<TagSearchDto>> addToPost(@NotNull @Valid @RequestBody TagAddDto tagAddDto) {
        return tagService.addToPost(tagAddDto);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<TagSearchDto>> searchTagsLikeName(@RequestParam("name") String tagName) {
        return tagService.searchTagsLikeName(tagName);
    }

    @PostMapping(value = "/remove")
    public void removeTagsFromPost(@NotNull @Valid @RequestBody TagRemoveDto tagRemoveDto) {
        tagService.removeTagsFromPost(tagRemoveDto);
    }
}