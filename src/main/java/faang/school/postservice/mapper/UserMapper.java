package faang.school.postservice.mapper;

import faang.school.postservice.model.CacheableUser;
import faang.school.postservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    @Value("${feed.cache.user.time-to-live-in-seconds}")
    private long timeToLive;


    @Mapping(target = "timeToLive", source = "id", qualifiedByName = "getTimeToLive")
    public abstract CacheableUser toCacheable(User user);

    @Named("getTimeToLive")
    protected long getTimeToLive(Long id) {
        return timeToLive;
    }
}
