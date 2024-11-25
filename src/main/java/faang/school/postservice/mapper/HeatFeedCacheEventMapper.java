package faang.school.postservice.mapper;

import faang.school.postservice.model.event.HeatFeedCacheEvent;
import faang.school.postservice.protobuf.generate.HeatFeedCacheEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HeatFeedCacheEventMapper {
    default HeatFeedCacheEventProto.HeatFeedCacheEvent toProto(HeatFeedCacheEvent event) {
        if (event == null) {
            return null;
        }

        HeatFeedCacheEventProto.HeatFeedCacheEvent.Builder builder =
                HeatFeedCacheEventProto.HeatFeedCacheEvent.newBuilder();

        if (event.getUsersIds() != null) {
            builder.addAllUsersIds(event.getUsersIds());
        }


        return builder.build();
    }

    @Mapping(target = "usersIds", source = "usersIdsList")
    HeatFeedCacheEvent toEvent(HeatFeedCacheEventProto.HeatFeedCacheEvent proto);
}
