/*
* $Id: $
*
* Copyright (c) 2021, CGI. All rights reserved.
* CGI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package org.codejargon.fluentjdbc.api.query;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * The {@code CallableResultMapper} class implements ... 
 * TODO write proper class description
 * 
 * @author wiedermanna
 */
public interface CallableMapper<T> {
    T map(CallableStatement statement) throws SQLException;
}
