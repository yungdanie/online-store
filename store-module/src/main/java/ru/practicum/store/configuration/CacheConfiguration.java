package ru.practicum.store.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import ru.practicum.store.dto.response.ItemDTO;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
public class CacheConfiguration {

    @Bean
    public RedisCacheManagerBuilderCustomizer weatherCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "items",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(ItemDTO.class)
                                )
                        )
        );
    }

    @Bean(name = "itemsTemplate")
    public ReactiveRedisTemplate<String, List<ItemDTO>> reactiveItemRedisTemplate(ReactiveRedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        RedisSerializer<List<ItemDTO>> valueSerializer = new RedisSerializer<>() {
            @Override
            public byte[] serialize(List<ItemDTO> items) throws SerializationException {
                try {
                    return objectMapper.writeValueAsBytes(items);
                } catch (JsonProcessingException e) {
                    throw new SerializationException("Serialization error", e);
                }
            }

            @Override
            public List<ItemDTO> deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) return null;
                try {
                    return objectMapper.readValue(bytes, new TypeReference<List<ItemDTO>>() {});
                } catch (IOException e) {
                    throw new SerializationException("Deserialization error", e);
                }
            }
        };

        RedisSerializationContext<String, List<ItemDTO>> context = RedisSerializationContext
                .<String, List<ItemDTO>>newSerializationContext(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())
                )
                .value(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
