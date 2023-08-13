package faang.school.postservice.controller.spellChecker;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.spellChecker.SpellErrorDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.spellChecker.SpellCheckerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spell-checker")
@Tag(name = "Spelling Checker", description = "Spelling checker API")
public class SpellCheckerController {
    private final SpellCheckerService spellCheckerService;

    @GetMapping("/post/{postId}")
    @Operation(summary = "Create Post")
    @ResponseStatus(HttpStatus.OK)
    public List<SpellErrorDto> checkSpelling(@PathVariable @Valid Long postId) {
        validateId(postId);
        return spellCheckerService.spellingTextCorrection(postId);
    }

    private void validateId(Long id) {
        if (id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }
}
