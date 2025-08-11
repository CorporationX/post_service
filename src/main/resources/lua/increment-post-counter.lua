-- increment-post-counter.lua
-- Атомарно инкрементирует счетчик поста с использованием оптимистической блокировки.
-- KEYS[1]: Ключ поста в Redis (например, post_cache:{postId})
-- ARGV[1]: Тип счетчика для инкремента ('like_event', 'comment_event', 'post_viewed_event')
-- ARGV[2]: Ожидаемая версия поста для проверки на конкурентные изменения

local postKey = KEYS[1]
local incrementType = ARGV[1]
local expectedVersion = tonumber(ARGV[2])

-- 1. Получаем JSON-строку поста из кэша
local postJson = redis.call('GET', postKey)

if not postJson then
    -- Если пост не найден, возвращаем 0. Обновление невозможно.
    return 0
end

-- 2. Десериализуем JSON-строку в Lua-таблицу
local post = cjson.decode(postJson)

-- 3. Проверяем версию для оптимистической блокировки.
-- Если текущая версия в кэше не совпадает с ожидаемой,
-- значит, кто-то другой уже изменил пост.
if post.version ~= expectedVersion then
    return 0 -- Возвращаем 0, сигнализируя о неудаче.
end

-- 4. Инкрементируем нужный счетчик в зависимости от типа события
if incrementType == 'like_event' then
    post.likeCount = (post.likeCount or 0) + 1
elseif incrementType == 'comment_event' then
    post.commentCount = (post.commentCount or 0) + 1
elseif incrementType == 'post_viewed_event' then
    post.viewsCount = (post.viewsCount or 0) + 1
else
    -- Неизвестный тип события.
    return 0
end

-- 5. Инкрементируем версию поста, чтобы пометить его как измененный
post.version = post.version + 1

-- 6. Сериализуем обновленный пост обратно в JSON и сохраняем
local updatedPostJson = cjson.encode(post)
redis.call('SET', postKey, updatedPostJson)

return 1 -- Возвращаем 1 в случае успешного обновления.