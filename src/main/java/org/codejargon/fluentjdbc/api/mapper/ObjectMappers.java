package org.codejargon.fluentjdbc.api.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codejargon.fluentjdbc.internal.mappers.ObjectMapper;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.internal.mappers.DefaultObjectMapperRsExtractors;
import org.codejargon.fluentjdbc.internal.support.Maps;

/**
 * Constructs Mappers for mapping a ResultSet row into a Java bean. The bean must have a no-arg constructor.
 * In addition to types in JDBC, supports java.time
 *
 * Supports custom types field types through ObjectMapperRsExtractors.
 *
 * Matching of fields is case insensitive and excludes '_' characters
 */
public class ObjectMappers {
    private final Map<Class, ObjectMapperRsExtractor> extractors;
    private final Map<Class, Mapper<?>> mappers;

    private ObjectMappers(Map<Class, ObjectMapperRsExtractor> extractors) {
        this.extractors = Maps.merge(DefaultObjectMapperRsExtractors.extractors(), extractors);
        mappers = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Mapper<T> forClass(Class<T> clazz) {
        if(!mappers.containsKey(clazz)) {
            mappers.put(clazz, new ObjectMapper<>(clazz, extractors));
        }
        return (Mapper<T>) mappers.get(clazz);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Map<Class, ObjectMapperRsExtractor> extractors = Maps.copyOf(new HashMap<>());
        
        private Builder() {
            
        }
        
        public Builder converters(Map<Class, ObjectMapperRsExtractor> extractors) {
            this.extractors = Maps.copyOf(extractors);
            return this;
        }
        
        public ObjectMappers build() {
            return new ObjectMappers(Maps.copyOf(extractors));
        }
    }
}
