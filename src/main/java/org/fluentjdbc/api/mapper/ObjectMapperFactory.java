package org.fluentjdbc.api.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.fluentjdbc.api.query.Mapper;
import org.fluentjdbc.internal.mappers.DefaultObjectMapperRsExtractors;
import org.fluentjdbc.internal.mappers.ObjectMapper;
import org.fluentjdbc.internal.support.Maps;

/**
 * Constructs Mappers for mapping a ResultSet row into a Java bean. The bean must have a no-arg constructor.
 * In addition to types in JDBC, supports java.time
 *
 * Supports custom types field types through ObjectMapperRsExtractors.
 */
public class ObjectMapperFactory {
    private final Map<Class, ObjectMapperRsExtractor> extractors;
    private final Map<Class, Mapper<?>> mappers;

    private ObjectMapperFactory(Map<Class, ObjectMapperRsExtractor> extractors) {
        this.extractors = Maps.merge(DefaultObjectMapperRsExtractors.extractors(), extractors);
        mappers = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Mapper<T> create(Class<T> clazz) {
        if(!mappers.containsKey(clazz)) {
            mappers.put(clazz, new ObjectMapper<>(clazz, extractors));
        }
        return (Mapper<T>) mappers.get(clazz);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Map<Class, ObjectMapperRsExtractor> extractors = Maps.immutableCopyOf(new HashMap<>());
        
        private Builder() {
            
        }
        
        public Builder converters(Map<Class, ObjectMapperRsExtractor> extractors) {
            this.extractors = Maps.immutableCopyOf(extractors);
            return this;
        }
        
        public ObjectMapperFactory build() {
            return new ObjectMapperFactory(Maps.immutableCopyOf(extractors));
        }
    }
}
