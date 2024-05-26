package faang.school.postservice.controller;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;


    @GetMapping
    public List<RedisPost> getFeed(@RequestParam Long postId){
        return feedService.getFeed(Optional.ofNullable(postId));
    }
}
//Создать эндпоит /feed, в который передается опциональный параметр запрос (не PathVariable),
// содержащий id поста, после которого нужно подгрузить следующую пачку постов фида
//
//Если этот параметр отсутствует, достаем первые 20 постов из Redis-фида данного пользователя,
// который запрашивает фид. Текущего пользователя можно получить из userContext
//
//Если фид в Redis кончился, за остальными постами фида идем в БД.
//
//Доставая фид из Redis, мы достаем лишь id постов. Собрать все остальные из двух дополнительных
// коллекций в Redis: posts и users. Из полученных данных сгенерировать полный dto каждого поста,
// который и будет возвращен пользователю. Если каких-то данных нет в коллекциях в Redis, то честно идем за ними в БД