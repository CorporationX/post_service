-- Add a new comment to a sorted set and trim it to a specified limit.
-- The comments are stored as JSON strings.
-- KEY[1]: The key of the sorted set (e.g., comments_cache:{postId})
-- ARGV[1]: The maximum number of comments to keep (commentsLimit)
-- ARGV[2]: The score for the comment (timestamp)
-- ARGV[3]: The comment as a serialized JSON string

local key = KEYS[1]
local limit = tonumber(ARGV[1])
local score = tonumber(ARGV[2])
local member = ARGV[3]

-- ZADD is idempotent.
redis.call('ZADD', key, score, member)
-- ZREMRANGEBYRANK 0, -limit-1 deletes all elements except the last 'limit'.
redis.call('ZREMRANGEBYRANK', key, 0, -limit-1)
return redis.call('ZCARD', key)