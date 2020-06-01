/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.siesta.internal.mybatis;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.sonatype.nexus.common.app.FrozenException;
import org.sonatype.nexus.rest.ExceptionMapperSupport;

import org.apache.ibatis.exceptions.PersistenceException;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * MyBatis {@link PersistenceException} mapper.
 *
 * Handles exceptions wrapped by MyBatis, such as when writes fail due to NXRM being in read-only mode.
 *
 * @since 3.24
 */
@Named
@Singleton
@Provider
public class MyBatisPersistenceExceptionMapper
    extends ExceptionMapperSupport<PersistenceException>
{
  @Override
  protected Response convert(final PersistenceException exception, final String id) {
    if (exception.getCause() instanceof FrozenException) {
      return Response.serverError()
          .status(SERVICE_UNAVAILABLE)
          .entity(String.format("Nexus Repository Manager is in read-only mode: (ID %s)", id))
          .type(TEXT_PLAIN)
          .build();
    }

    return unexpectedResponse(exception, id);
  }
}
