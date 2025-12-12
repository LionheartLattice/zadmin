package io.github.lionheartlattice.hello.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Redisson测试控制器
 * 用于测试Redisson的基本功能，如缓存操作和分布式锁
 */
@Tag(name = "Redisson测试", description = "测试Redisson缓存和锁功能")
@RestController
@RequestMapping("test/redisson")
@RequiredArgsConstructor
public class RedissonTestController {

    private final RedissonClient redissonClient;

    /**
     * 设置缓存值
     */
    @Operation(summary = "设置缓存", description = "设置Redis缓存键值对")
    @GetMapping("set")
    public String setCache(@RequestParam String key, @RequestParam String value) {
        redissonClient.getBucket(key)
                      .set(value);
        return "设置成功: " + key + " = " + value;
    }

    /**
     * 获取缓存值
     */
    @Operation(summary = "获取缓存", description = "获取Redis缓存值")
    @GetMapping("get")
    public String getCache(@RequestParam String key) {
        Object value = redissonClient.getBucket(key)
                                     .get();
        return "获取成功: " + key + " = " + (value != null ? value.toString() : "null");
    }

    /**
     * 测试分布式锁
     */
    @Operation(summary = "测试分布式锁", description = "使用Redisson分布式锁执行操作")
    @GetMapping("lock")
    public String testLock(@RequestParam String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                // 模拟业务操作
                Thread.sleep(2000);
                return "锁获取成功，执行完毕: " + lockKey;
            } else {
                return "锁获取失败: " + lockKey;
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            return "操作中断: " + lockKey;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
