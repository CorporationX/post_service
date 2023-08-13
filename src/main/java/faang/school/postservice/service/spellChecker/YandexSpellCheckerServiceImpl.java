package faang.school.postservice.service.spellChecker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.YandexSpellerClient;
import faang.school.postservice.dto.spellChecker.SpellErrorDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YandexSpellCheckerServiceImpl implements SpellCheckerService {

    private final YandexSpellerClient spellerClient;
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<SpellErrorDto> spellingTextCorrection(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        String result = spellerClient.checkText(post.getContent());
        List<SpellErrorDto> errorDtoList;

        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            errorDtoList = objectMapper.readValue(result, new TypeReference<>() {});

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return errorDtoList;
    }
}