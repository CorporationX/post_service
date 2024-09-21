package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    // TODO: в какой момент как валидировать используя эти аннотации?
    //       (этот тип возвращается из методов, а не является аргументом метода,
    //       куда ставить @Validated ?)
    //       - решение - сделал CustomValidator, норм?

    @NotNull
    private Long id;
    @NotEmpty
    private String username;
    @Email
    private String email;
}
