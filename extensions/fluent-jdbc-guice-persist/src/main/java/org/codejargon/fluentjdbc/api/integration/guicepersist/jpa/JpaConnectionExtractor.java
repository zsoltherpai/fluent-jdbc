package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa;

import javax.persistence.EntityManager;
import java.sql.Connection;

/**
 * <p>Extracts the underlying Connection from an EntityManager. There is no standard way defined in JPA to extract 
 * the underlying Connection in JPA, but it's possible with vendor-specific code. See examples below</p>
 *  <p>EclipseLink and JPA 2.0</p>
 *  <pre>
 *  entityManager.unwrap(java.sql.Connection.class);
 *  </pre>
 *  <p>Hibernate 4.x and JPA 2.0</p>
 *  <pre>
 *  Session session = entityManager.unwrap(Session.class);
 *  SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
 *  return sfi.getConnectionProvider().getConnection();
 *  </pre>  
 *  <p>Hibernate 3.x and JPA 2.0</p>
 *  <pre>
 *  return entityManager.unwrap(Session.class).connection();
 *  </pre>
 *  <p>Hibernate 3.x and JPA 1.0</p>
 *  <pre>
 *  Session session = (Session) entityManager.getDelegate();
 *  return session.connection();
 *  </pre>
 *  
 */
public interface JpaConnectionExtractor {
    Connection extract(EntityManager entityManager);
}
