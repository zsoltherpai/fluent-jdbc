package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa;

import java.util.Properties;

public class PersistenceProperties {
    public static final Properties props;
    
    static {
        props = new Properties();
        props.put("javax.persistence.jdbc.url", "jdbc:h2:mem:test/test;");
        props.put("javax.persistence.jdbc.user", "sa");
        props.put("javax.persistence.jdbc.password", "sa");
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
    }
}
