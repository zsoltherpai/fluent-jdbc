package org.codejargon.fluentjdbc.internal.mappers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.mapper.ObjectMapperRsExtractor;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.internal.support.Arrs;
import org.codejargon.fluentjdbc.internal.support.Maps;

public class ObjectMapper<T> implements Mapper<T> {

    private final Map<Class, ObjectMapperRsExtractor> extractors;
    private final Class<T> type;
    private final Map<String, Field> fields;
    private final Constructor<T> noargConstructor;

    private final Function<String, String> converter;
    
    public ObjectMapper(Class<T> type, Map<Class, ObjectMapperRsExtractor> extractors, Function<String, String> converter) {
        this.extractors = extractors;
        this.type = type;
        this.converter = converter;
        this.fields = discoverFields(type);
        noargConstructor = noargConstructor();
    }

    private Map<String, Field> discoverFields(Class<T> aType) throws SecurityException {
        Map<String, Field> allFields = new HashMap<>();
        Class inspectedClass = aType;
        while (inspectedClass != null) {
            Arrs.stream(inspectedClass.getDeclaredFields()).forEach(
                    field -> {
                        field.setAccessible(true);
                        allFields.put(converter.apply(field.getName()), field);
                    }
            );
            inspectedClass = inspectedClass.getSuperclass();
        }
        return Maps.copyOf(allFields);
    }

    @Override
    public T map(ResultSet rs) throws SQLException {
        T result = newInstance();
        ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); ++i) {
            mapColumn(converter.apply(metadata.getColumnLabel(i)), i, rs, result);
        }
        return result;

    }

    private void mapColumn(String fieldName, int i, ResultSet rs, T result) throws IllegalArgumentException, FluentJdbcException, SQLException {
        Field field = fields.get(fieldName);
        if (field != null) {
            Object value = value(typeOfField(field), rs, i);
            setField(field, result, value);
        }
    }

    private void setField(Field field, T result, Object value) {
        try {
            Object valueToBeSet = (field.getType().equals(Optional.class)) ?
                    optionalOf(field, value) :
                    value;
            field.set(result, valueToBeSet);
        } catch (IllegalAccessException e) {
            throw new FluentJdbcException(
                    String.format(
                            "Unable to set field %s in %s with value %s",
                            field.getName(),
                            field.getDeclaringClass(),
                            value != null ? value.getClass().getName() : "null"
                    ),
                    e
            );
        }
    }

    private Object optionalOf(Field field, Object value) {
        if (value == null) {
            return Optional.empty();
        } else {
            Class<?> typeOfField = typeOfField(field);
            if (!typeOfField.isAssignableFrom(value.getClass())) {
                throw new FluentJdbcException(String.format("Can't map value of class %s to Optional<%s>", value.getClass(), field));
            }
            return Optional.of(value);
        }
    }

    private Object value(Class fieldType, ResultSet rs, Integer index) throws SQLException {
        ObjectMapperRsExtractor converter = extractors.get(fieldType);
        Object value = converter != null ? converter.extract(rs, index) : rs.getObject(index);
        return (rs.wasNull() && !fieldType.isPrimitive()) ? null : value;
    }

    private T newInstance() throws IllegalArgumentException {
        try {
            return noargConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new FluentJdbcException(
                    String.format("Cannot instantiate %s with the no-arg constructor", type.getName()),
                    ex
            );
        }
    }

    private Constructor<T> noargConstructor() {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException ex) {
            throw new FluentJdbcException(
                    String.format("Cannot find no-arg constructor in %s", type.getName()),
                    ex
            );
        }

    }

    private Class<?> typeOfField(Field field) {
        return field.getType().equals(Optional.class) ?
                (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0] :
                field.getType();
    }
}
