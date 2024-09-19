package faang.school.postservice.redisdemo.mapper;

import faang.school.postservice.redisdemo.dto.ArticleDto;
import faang.school.postservice.redisdemo.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

//    @Mapping(source = "hashTags", target = "hashTags",)
    ArticleDto toArticleDto(Article article);

    Article toEntity(ArticleDto articleDto);
}
