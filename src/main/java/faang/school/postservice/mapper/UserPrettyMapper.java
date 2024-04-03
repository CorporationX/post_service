package faang.school.postservice.mapper;

import faang.school.postservice.dto.hash.UserHash;
import faang.school.postservice.dto.hash.UserPretty;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPrettyMapper {
    UserPretty toPretty(UserHash userHash);
}
