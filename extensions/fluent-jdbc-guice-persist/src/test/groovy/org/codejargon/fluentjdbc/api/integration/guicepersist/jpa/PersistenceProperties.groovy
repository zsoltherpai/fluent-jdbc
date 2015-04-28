package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa;

public class PersistenceProperties {
    public static final Properties props
    
    static {
        props = new Properties()
        props.putAll(
                "javax.persistence.jdbc.url": "jdbc:h2:mem:test/test;",
                "javax.persistence.jdbc.user": "sa",
                "javax.persistence.jdbc.password": "sa",
                "javax.persistence.jdbc.driver": "org.h2.Driver",
        );

    }
}
