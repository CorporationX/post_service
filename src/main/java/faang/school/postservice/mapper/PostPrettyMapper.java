package faang.school.postservice.mapper;

import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.dto.hash.PostPretty;
import faang.school.postservice.dto.hash.UserHash;
import faang.school.postservice.dto.hash.UserPretty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostPrettyMapper {
    PostPretty toPretty(PostHash postHash);

    List<PostPretty> toPretty(List<PostHash> postHashes);

}
