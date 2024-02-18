package faang.school.postservice.service;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.filter.ad.Filter;
import faang.school.postservice.mapper.AdMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {
    @InjectMocks
    AdService adService;

    @Mock
    AdRepository adRepository;
    @Mock
    AdValidator adValidator;
    @Mock
    List<Filter<Ad>> filters;

    @Spy
    @InjectMocks
    AdMapper adMapper = Mappers.getMapper(AdMapper.class);

    @Mock
    PostService postService;

    Ad ad;
    AdDto adDto;
    Post post;
    PostDto postDto;

    @BeforeEach
    public void setup() {
        LocalDateTime currentTime = LocalDateTime.now();
        post = Post.builder().id(1L).ad(ad).build();
        postDto = new PostDto();
        ad = Ad.builder()
                .id(1L).post(post).buyerId(1L)
                .startDate(currentTime).endDate(currentTime.plusHours(1))
                .build();
        adDto = AdDto.builder()
                .id(1L).postId(1L)
                .startDate(currentTime).endDate(currentTime.plusHours(1))
                .build();
    }

    @Test
    public void shouldCreateAd() {
        AdDto adDto = validate(true);
        Ad ad = adMapper.toEntity(adDto);

        when(adRepository.save(ad)).thenReturn(ad);
        AdDto result = adService.create(adDto);

        assertEquals(adDto.getId(), result.getId());
    }

    @Test
    public void shouldRemoveAd() {
        when(adRepository.findById(adDto.getId())).thenReturn(Optional.of(ad));

        AdDto result = adService.remove(adDto.getId());

        assertEquals(adDto.getId(), result.getId());
    }

    @Test
    public void shouldThrowExceptionOnRemove() {
        when(adRepository.findById(2L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> {
            adService.remove(2L);
        });
    }

    private AdDto validate(boolean validationPassed) {
        if (validationPassed) {
            doNothing()
                    .when(adValidator)
                    .validate(adDto);
        } else {
            doThrow(DataValidationException.class)
                    .when(adValidator)
                    .validate(adDto);
        }

        return adDto;
    }
}
