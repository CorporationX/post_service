package faang.school.postservice.service.like;

public interface LikeService<T> {

    T addLike(long userId, long id);

    void removeLike(long userId, long id);

}
