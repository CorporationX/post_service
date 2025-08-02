package faang.school.postservice.validator;

public interface Validator<T> {
    void validate(T t);
}
