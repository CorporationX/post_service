package faang.school.postservice.validator;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdValidator {
    private final AdRepository adRepository;
    private final PostRepository postRepository;

    public void validate(AdDto adDto) {
        checkIfAdExists(adDto.getId());
        checkIfPostExists(adDto.getPostId());
    }

    private void checkIfAdExists(Long id) {
        if (adRepository.existsById(id)) {
            throw new DataValidationException("Ad by id " + id + " alreay exists.");
        }
    }

    private void checkIfPostExists(Long id) {
        if (!postRepository.existsById(id)) {
            throw new DataValidationException("Post with provided id not found.");
        }
    }
}
