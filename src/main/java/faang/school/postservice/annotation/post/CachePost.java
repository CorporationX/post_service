package faang.school.postservice.annotation.post;

import faang.school.postservice.model.redis.CacheOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePost {
    String keyPrefix() default "Post:";

    CacheOperation operation() default CacheOperation.DEFAULT;
}
