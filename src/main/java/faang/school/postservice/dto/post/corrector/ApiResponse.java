package faang.school.postservice.dto.post.corrector;

public record ApiResponse<T extends Response>(
        boolean status,
        T response,
        int error_code,
        String description) {
}
