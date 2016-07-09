package org.codejargon.fluentjdbc.api.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.codejargon.fluentjdbc.internal.mappers.ObjectMapper;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.internal.mappers.DefaultObjectMapperRsExtractors;
import org.codejargon.fluentjdbc.internal.support.Maps;

/**
 * <p>Constructs Mappers for mapping a ResultSet row into a plain java object based on object field vs ResultSet column
 * match. Default matching is case insensitive and ignores '_' characters in database fields. The target class must have
 * a no-arg constructor, fields can be private, no need for accessors.</p>
 *
 * <p>In addition to JDBC types, supports java.time types out of the box. Able to support any custom type.</p>
 *
 * @see org.codejargon.fluentjdbc.api.query.Mapper
 */
public class ObjectMappers {
    private final Map<Class, ObjectMapperRsExtractor> extractors;
    private final Map<Class, Mapper<?>> mappers;
    private final Function<String, String> converter;
    
    private ObjectMappers(Map<Class, ObjectMapperRsExtractor> extractors, Function<String, String> converter) {
        this.extractors = Maps.merge(DefaultObjectMapperRsExtractors.extractors(), extractors);
        this.converter = converter;
        mappers = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Mapper<T> forClass(Class<T> clazz) {
        if(!mappers.containsKey(clazz)) {
            mappers.put(clazz, new ObjectMapper<>(clazz, extractors, converter));
        }
        return (Mapper<T>) mappers.get(clazz);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Map<Class, ObjectMapperRsExtractor> extractors = Collections.emptyMap();
        private Function<String, String> converter = f -> f.toLowerCase().replace("_", "");
        private Builder() {
            
        }

        /**
         * Sets extractors to support custom (non-JDBC) fields.
         * @param extractors Map of field class / ResultSet extractors
         * @return this
         */
        public Builder extractors(Map<Class, ObjectMapperRsExtractor> extractors) {
            this.extractors = Maps.copyOf(extractors);
            return this;
        }
        
        /**
         * Sets custom converter for object field to ResultSet column matching.
         * @param converter function to convert field names
         * @return this
         */
        public Builder fieldNameConverter(Function<String, String> converter) {
            this.converter = converter;
            return this;
        }
        
        public ObjectMappers build() {
            return new ObjectMappers(Maps.copyOf(extractors), converter);
        }
    }
}
