package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.SqlAndParamsForNamed;

import java.util.Optional;

class SingleQuerySpecification {
    final SingleQueryBase singleQueryBase;
    final Optional<SelectQueryInternal> select;
    final Boolean fetchGenerated;

    private SingleQuerySpecification (
            SingleQueryBase singleQueryBase,
            Optional<SelectQueryInternal> select,
            Boolean fetchGenerated
    ) {
        this.singleQueryBase = singleQueryBase;
        this.select = select;
        this.fetchGenerated = fetchGenerated;
    }

    static SingleQuerySpecification forSelect(
            SelectQueryInternal selectQueryInternal
    ) {
        return new SingleQuerySpecification(
                selectQueryInternal,
                Optional.of(selectQueryInternal),
                false
        );
    }

    static SingleQuerySpecification forUpdate(
            SingleQueryBase singleQueryBase,
            Boolean fetchGenerated
    ) {
        return new SingleQuerySpecification(
                singleQueryBase,
                Optional.<SelectQueryInternal>empty(),
                fetchGenerated
        );
    }

    SqlAndParams sqlAndParams(QueryConfig config) {
        return singleQueryBase.namedParams.isEmpty() ?
                new SqlAndParams(singleQueryBase.sql, singleQueryBase.params) :
                SqlAndParamsForNamed.create(config.namedTransformedSql(singleQueryBase.sql), singleQueryBase.namedParams);
    }
}
