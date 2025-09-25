package faang.school.postservice.mapper;

import faang.school.postservice.model.redis.CommentCache;
import faang.school.postservice.model.redis.PostCache;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.*;

@Mapper(componentModel = "spring")
public interface PostCacheMapper {

    @Mappings({
            @Mapping(target = "lastComments", ignore = true)
    })
    PostCache fromHash(Map<String, String> hash);

    static Long toLong(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Long.valueOf(s); } catch (NumberFormatException e) { return null; }
    }

    static LocalDateTime toLocalDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDateTime.parse(s);
    }

    @AfterMapping
    default void rebuildComments(Map<String, String> hash, @MappingTarget PostCache target) {
        List<CommentCache> comments = new ArrayList<>();
        for (int i = 0; ; i++) {
            String base = "lastComments.[" + i + "]";
            String id = hash.get(base + ".id");
            if (id == null) break;

            CommentCache commentCache = new CommentCache();
            commentCache.setId(toLong(id));
            commentCache.setAuthorId(toLong(hash.get(base + ".authorId")));
            commentCache.setContent(hash.get(base + ".content"));
            commentCache.setCreatedAt(toLocalDateTime(hash.get(base + ".createdAt")));
            comments.add(commentCache);
        }
        target.setLastComments(comments);
    }
}

