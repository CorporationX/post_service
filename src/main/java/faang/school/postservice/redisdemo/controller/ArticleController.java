package faang.school.postservice.redisdemo.controller;

import faang.school.postservice.redisdemo.dto.ArticleDto;
import faang.school.postservice.redisdemo.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/trending")
    public ArticleDto getRandomArticle() {
        return articleService.getRandomArticle();
    }

    @GetMapping("/articles/{id}")
    public ArticleDto getArticle(@PathVariable long id) {
        return articleService.getCachedArticle(id);
    }
}
