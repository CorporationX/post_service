package faang.school.postservice.redisdemo.controller;

import faang.school.postservice.redisdemo.dto.ArticleDto;
import faang.school.postservice.redisdemo.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/articles")
    public ArticleDto createArticle(@RequestBody ArticleDto articleDto) {
        return articleService.createArticle(articleDto);
    }

    @PutMapping("/articles")
    public List<ArticleDto> findByHashTags(@RequestBody ArticleDto articleDto) {
        return articleService.findArticleByHashTag(articleDto);

//        return null;
    }
}