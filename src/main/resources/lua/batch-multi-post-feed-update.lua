-- batch-multi-post-feed-update.lua
-- Атомарно добавляет несколько постов в ленты нескольких пользователей.
-- KEYS[1]: TTL (время жизни) для ключей лент в секундах
-- KEYS[2]: Максимальный размер ленты (feedSizeLimit)
-- ARGV: Список аргументов, разделенный специальной строкой 'POSTS_START'
--       Первая часть ARGV - это userIds.
--       Вторая часть ARGV - это пары (score, postId).

local ttl = tonumber(KEYS[1])
local feedSizeLimit = tonumber(KEYS[2])
local feedKeyPrefix = 'feed_cache:'

local userIds = {}
local postArgs = {}
local isPostArgs = false

-- 1. Разбираем входные аргументы на два списка: userIds и postArgs
for i = 1, #ARGV do
    if ARGV[i] == 'POSTS_START' then
        isPostArgs = true
    elseif not isPostArgs then
        table.insert(userIds, ARGV[i])
    else
        table.insert(postArgs, ARGV[i])
    end
end

-- 2. Проходим по каждому пользователю
for i = 1, #userIds do
    local userId = userIds[i]
    local feedKey = feedKeyPrefix .. userId

    -- 3. Атомарно добавляем все посты из списка postArgs в отсортированное множество
    -- unpack(postArgs) превращает массив в список аргументов для ZADD.
    redis.call('ZADD', feedKey, unpack(postArgs))

    -- 4. Обрезаем множество, оставляя N самых новых постов.
    redis.call('ZREMRANGEBYRANK', feedKey, 0, -feedSizeLimit - 1)

    -- 5. Устанавливаем срок жизни для ключа ленты.
    redis.call('EXPIRE', feedKey, ttl)
end

return 1 -- Возвращаем 1, если операция выполнена.