-- batch-feed-update.lua
-- Атомарно добавляет один пост в ленты нескольких пользователей.
-- KEYS[1]: ID поста
-- KEYS[2]: Оценка (score) для поста (например, временная метка)
-- KEYS[3]: TTL (время жизни) для ключей лент в секундах
-- KEYS[4]: Максимальный размер ленты (feedSizeLimit)
-- ARGV[1]...: Список ID пользователей, в чью ленту нужно добавить пост

local postId = KEYS[1]
local score = tonumber(KEYS[2])
local ttl = tonumber(KEYS[3])
local feedSizeLimit = tonumber(KEYS[4])
local feedKeyPrefix = 'feed_cache:'

-- Проходим по каждому userId, переданному в ARGV
for i = 1, #ARGV, 1 do
    local userId = ARGV[i]
    local feedKey = feedKeyPrefix .. userId

    -- 1. Атомарно добавляем пост в отсортированное множество (ZADD)
    redis.call('ZADD', feedKey, score, postId)

    -- 2. Обрезаем множество, оставляя только N самых новых постов.
    -- ZREMRANGEBYRANK с отрицательными индексами удаляет элементы с конца.
    -- -feedSizeLimit - 1 означает, что мы удаляем все, кроме последних N.
    redis.call('ZREMRANGEBYRANK', feedKey, 0, -feedSizeLimit - 1)

    -- 3. Устанавливаем срок жизни (TTL) для ключа ленты.
    redis.call('EXPIRE', feedKey, ttl)
end

return 1 -- Возвращаем 1, если операция выполнена.