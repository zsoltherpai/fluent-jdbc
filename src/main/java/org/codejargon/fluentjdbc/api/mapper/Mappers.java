package org.codejargon.fluentjdbc.api.mapper;

import org.codejargon.fluentjdbc.api.query.Mapper;

public abstract class Mappers {
    private static final Mapper<Integer> singleInteger = (rs) -> rs.getInt(1);
    private static final Mapper<Long> singleLong = (rs) -> rs.getLong(1);
    private static final Mapper<String> singleString = (rs) -> rs.getString(1);
  
    public static Mapper<Integer> singleInteger() {
        return singleInteger;
    }
    
    public static Mapper<Long> singleLong() {
        return singleLong;
    }
    
    public static Mapper<String> singleString() {
        return singleString;
    }
}
