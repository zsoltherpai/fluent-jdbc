package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.SqlAndParamsForNamed;

import java.util.Optional;

class SingleQuerySpecification {
    final SingleQueryBase singleQueryBase;
    final Optional<SelectQueryInternal> select;

    private SingleQuerySpecification (
            SingleQueryBase singleQueryBase,
            Optional<SelectQueryInternal> select
    ) {
        this.singleQueryBase = singleQueryBase;
        this.select = select;
    }

    static SingleQuerySpecification forSelect(
            SelectQueryInternal selectQueryInternal
    ) {
        return new SingleQuerySpecification(
                selectQueryInternal,
                Optional.of(selectQueryInternal)
        );
    }

    static SingleQuerySpecification forUpdate(
            SingleQueryBase singleQueryBase
    ) {
        return new SingleQuerySpecification(
                singleQueryBase,
                Optional.<SelectQueryInternal>empty()
        );
    }

    SqlAndParams sqlAndParams(QueryConfig config) {
        return singleQueryBase.namedParams.isEmpty() ?
                new SqlAndParams(singleQueryBase.sql, singleQueryBase.params) :
                SqlAndParamsForNamed.create(config.namedTransformedSql(singleQueryBase.sql), singleQueryBase.namedParams);
    }
}
