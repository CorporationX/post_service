-- Add post to a sorted set and trim it
-- KEY[1]: The key of the sorted set (e.g., feed_cache:{userId})
-- ARGV[1]: The maximum number of elements to keep (feedSizeLimit)
-- ARGV[2]...: Scores and members to add (score, postId)

redis.call('ZADD', KEYS[1], unpack(ARGV, 2))
redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -tonumber(ARGV[1] + 1))
return redis.call('ZCARD', KEYS[1])