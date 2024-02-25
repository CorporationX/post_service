package faang.school.postservice.filter.ad;

import faang.school.postservice.model.ad.Ad;

import java.util.stream.Stream;

public class AppearancesLeftFilter implements Filter<Ad> {
    @Override
    public Stream<Ad> apply(Stream<Ad> ads) {
        return ads.filter(
                ad -> ad.getAppearancesLeft() == 0
        );
    }
}
