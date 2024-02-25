package faang.school.postservice.mapper;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.service.PostService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {PostService.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AdMapper {
    @Mapping(source = "postId", target = "post")
    Ad toEntity(AdDto adDto);

    @Mapping(source = "post.id", target = "postId")
    AdDto toDto(Ad ad);

    List<Ad> toEntityList(List<AdDto> adDtos);

    List<AdDto> toDtoList(List<Ad> ads);
}
