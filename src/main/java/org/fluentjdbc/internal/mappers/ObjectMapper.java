package org.fluentjdbc.internal.mappers;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.fluentjdbc.api.FluentJdbcException;
import org.fluentjdbc.api.mapper.ObjectMapperRsExtractor;
import org.fluentjdbc.api.query.Mapper;
import org.fluentjdbc.internal.support.Maps;

public class ObjectMapper<T> implements Mapper<T> {

    private final Map<Class, ObjectMapperRsExtractor> extractors;
    private final Class<T> type;
    private final Map<String, Field> fields;

    public ObjectMapper(Class<T> type, Map<Class, ObjectMapperRsExtractor> extractors) {
        this.extractors = extractors;
        this.type = type;
        this.fields = discoverFields(type);
    }

    private Map<String, Field> discoverFields(Class<T> aType) throws SecurityException {
        Map<String, Field> allFields = new HashMap<>();
        Class inspectedClass = aType;
        while (inspectedClass != null) {
            for (Field field : inspectedClass.getDeclaredFields()) {
                field.setAccessible(true);
                allFields.put(field.getName().toLowerCase(), field);
            }
            inspectedClass = inspectedClass.getSuperclass();
        }
        return Maps.copyOf(allFields);
    }

    @Override
    public T map(ResultSet rs) throws SQLException {
        T result = newInstance();
        ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); ++i) {
            mapColumn(fieldName(metadata, i), i, rs, result);
        }
        return result;

    }

    private void mapColumn(String fieldName, int i, ResultSet rs, T result) throws IllegalArgumentException, FluentJdbcException, SQLException {
        Field field = fields.get(fieldName);
        if (field != null) {
            Object value = value(field.getType(), rs, i);
            setField(field, result, value);
        }
    }

    private void setField(Field field, T result, Object value) {
        try {
            field.set(result, value);
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

    private Object value(Class fieldType, ResultSet rs, Integer index) throws SQLException {
        ObjectMapperRsExtractor converter = extractors.get(fieldType);
        Object value = converter != null ? converter.extract(rs, index) : rs.getObject(index);
        return (rs.wasNull() && !fieldType.isPrimitive()) ? null : value;
    }

    private T newInstance() throws IllegalArgumentException {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new FluentJdbcException(
                    String.format("Cannot instantiate %s with default constructor", type.getName()),
                    ex
            );
        }
    }

    private String fieldName(ResultSetMetaData metadata, int i) throws SQLException {
        return metadata.getColumnName(i).toLowerCase();
    }
}
