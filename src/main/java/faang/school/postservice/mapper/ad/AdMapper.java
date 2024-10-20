package faang.school.postservice.mapper.ad;

import faang.school.postservice.model.dto.ad.AdDto;
import faang.school.postservice.model.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdMapper {

    @Mapping(target = "post.id", source = "postId")
    Ad toEntity(AdDto adDto);

    @Mapping(target = "postId", source = "post.id")
    AdDto toDto(Ad ad);
}
