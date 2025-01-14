package com.project.forde.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class SseRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);

        emitter.onCompletion(() -> this.deleteById(emitterId));
        emitter.onTimeout(() -> this.deleteById(emitterId));
        emitter.onError(throwable -> {
            log.error("Error in emitter", throwable);
            this.deleteById(emitterId);
            emitter.completeWithError(throwable);
        });

        return emitter;
    }

    public void saveEventCache(String eventId, Object event) {
        eventCache.put(eventId, event);
    }

    public ConcurrentHashMap<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(ConcurrentHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), ConcurrentHashMap::putAll);
    }

    public ConcurrentHashMap<String, Object> findAllEventCacheStartWithByUserId(String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(ConcurrentHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), ConcurrentHashMap::putAll);
    }

    public void deleteById(String id) {
        emitters.remove(id);
    }

    public void deleteEventCacheById(String id) { eventCache.remove(id); }
}
