local limit = tonumber(ARGV[#ARGV])
local count = (#ARGV - 1) / 2

for i = 1, count do
    local member = ARGV[(i - 1) * 2 + 1]
    local score = tonumber(ARGV[(i - 1) * 2 + 2])
    redis.call('ZADD', KEYS[1], score, member)
end

redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -limit - 1)
return true
