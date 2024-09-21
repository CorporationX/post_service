package faang.school.postservice.dto.response;

import faang.school.postservice.exception.validation.Violation;

import java.util.List;

public record ConstraintErrorResponse(List<Violation> violations) {
}
