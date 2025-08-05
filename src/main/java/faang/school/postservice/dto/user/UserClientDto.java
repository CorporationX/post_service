package faang.school.postservice.dto.user;

public record UserClientDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe
) {
}
