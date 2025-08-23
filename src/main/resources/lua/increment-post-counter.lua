-- increment-post-counter-v2.lua
local postKey = KEYS[1]
local incrementType = ARGV[1]

-- 1. Получаем JSON-строку поста
local postJson = redis.call('GET', postKey)

if not postJson then
    -- Пост не найден, возможно, он ещё не кэширован.
    -- Мы не можем обновить то, чего нет.
    return -1
end

-- 2. Десериализуем JSON-строку
local post = cjson.decode(postJson)

-- 3. Инкрементируем нужный счетчик
if incrementType == 'like_event' then
    post.likeCount = (post.likeCount or 0) + 1
elseif incrementType == 'comment_event' then
    post.commentCount = (post.commentCount or 0) + 1
elseif incrementType == 'post_viewed_event' then
    post.viewCount = (post.viewCount or 0) + 1
else
    -- Неизвестный тип события.
    return -2
end

-- 4. Инкрементируем версию поста.
-- Этот шаг критически важен, чтобы избежать сброса счетчика.
post.version = (post.version or 0) + 1

-- 5. Сериализуем обновленный пост и сохраняем
local updatedPostJson = cjson.encode(post)
redis.call('SET', postKey, updatedPostJson)

return 1 -- Успех!