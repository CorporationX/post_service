package faang.school.postservice.redisdemo.mapper;

import faang.school.postservice.redisdemo.dto.ArticleDto;
import faang.school.postservice.redisdemo.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    ArticleDto toArticleDto(Article article);
}
