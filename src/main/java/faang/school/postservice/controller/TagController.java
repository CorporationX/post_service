package faang.school.postservice.controller;

import faang.school.postservice.dto.tag.TagAddToPostDto;
import faang.school.postservice.dto.tag.TagAddedToPostDto;
import faang.school.postservice.dto.tag.TagCreateDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagRemoveDto;
import faang.school.postservice.service.TagServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagServiceImpl tagService;

    @GetMapping("/post/{id}")
    public ResponseEntity<List<TagDto>> getTagsForPost(@NotNull @PathVariable Long id) {
        return tagService.getTagsForPost(id);
    }

    @PostMapping()
    public ResponseEntity<TagDto> createTag(@NotNull @RequestBody @Valid TagCreateDto tagCreateDto) {
        return tagService.createTag(tagCreateDto);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<List<TagAddedToPostDto>> addToPost(@NotNull @PathVariable Long postId,
                                                             @NotNull @Valid @RequestBody
                                                             TagAddToPostDto tagAddToPostDto) {
        return tagService.addToPost(postId, tagAddToPostDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagDto>> searchTagsLikeName(@NotNull @RequestParam("name") String tagName) {
        return tagService.searchTagsLikeName(tagName);
    }

    @DeleteMapping("post/{postId}")
    public ResponseEntity<Void> removeTagsFromPost(@NotNull @PathVariable Long postId,
                                                   @NotNull @RequestBody TagRemoveDto tagRemoveDto) {
        return tagService.removeTagsFromPost(postId, tagRemoveDto);
    }
}