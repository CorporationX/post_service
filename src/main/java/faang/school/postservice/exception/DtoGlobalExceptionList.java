package faang.school.postservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DtoGlobalExceptionList {
    private List<DtoGlobalException> listException;
}
