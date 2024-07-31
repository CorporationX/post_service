package faang.school.postservice.validator;

@FunctionalInterface
public interface PostServiceValidator<T> {
    void validate(T t);
}
