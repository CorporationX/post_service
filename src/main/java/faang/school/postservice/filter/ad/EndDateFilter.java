package faang.school.postservice.filter.ad;

import faang.school.postservice.model.ad.Ad;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class EndDateFilter implements Filter<Ad> {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Override
    public Stream<Ad> apply(Stream<Ad> ads) {
        return ads.filter(
                ad -> ad.getEndDate().isAfter(CURRENT_DATE)
        );
    }
}
