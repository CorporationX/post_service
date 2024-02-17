package faang.school.postservice.service;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.filter.ad.Filter;
import faang.school.postservice.mapper.AdMapper;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AdValidator adValidator;
    private final List<Filter<Ad>> filters;
    private final Environment env;

    public AdDto create(AdDto adDto) {
        adValidator.validate(adDto);

        var ad = adMapper.toEntity(adDto);
        var savedAd = adRepository.save(ad);

        return adMapper.toDto(savedAd);
    }

    public AdDto remove(Long id) {
        Ad adToRemove = adRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Ad not found")
        );

        return adMapper.toDto(adToRemove);
    }

    public void removeExpiredAds() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        var BATCH_SIZE = Integer.parseInt(
                env.getProperty(
                        "post.ad-remover.scheduler.batchSize",
                        "0"));
        var expiredAds = getExpiredAds();
        var size = expiredAds.size();
        var batch = (size - 1) / BATCH_SIZE;
        List<List<AdDto>> partitions = new ArrayList<>();

        if (size > BATCH_SIZE) {
            for (int i = 0; i < batch + 1; i++) {
                partitions.add(expiredAds
                        .subList(i * BATCH_SIZE, i == batch ? size : (i + 1) * BATCH_SIZE)
                );
            }
        } else {
            partitions.add(expiredAds);
        }

        List<CompletableFuture<Void>> futures = partitions
                .stream()
                .map(list -> CompletableFuture
                        .runAsync(() -> {
                            log.info(list.toString());
                            removeAllById(list);
                        }, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }

    private void removeAllById(List<AdDto> adDtos) {
        var ids = adDtos.stream().map(AdDto::getId).toList();
        adRepository.deleteAllById(ids);
    }

    private List<AdDto> getExpiredAds() {
        var ads = adMapper.toEntityList(getAds()).stream();
        var filteredAds = filters.stream()
                .reduce(ads,
                        (stream, filter) -> filter.apply(stream),
                        Stream::concat)
                .toList();

        return adMapper.toDtoList(filteredAds);
    }

    private List<AdDto> getAds() {
        var ads = StreamSupport
                .stream(adRepository.findAll().spliterator(),
                        false)
                .toList();

        return adMapper.toDtoList(ads);
    }
}
