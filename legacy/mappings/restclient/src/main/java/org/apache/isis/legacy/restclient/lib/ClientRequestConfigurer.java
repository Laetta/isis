/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.legacy.restclient.lib;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.RestfulHttpMethod;
import org.apache.isis.viewer.restfulobjects.applib.RestfulRequest;
import org.apache.isis.viewer.restfulobjects.applib.RestfulRequest.RequestParameter;
import org.apache.isis.viewer.restfulobjects.applib.util.UrlEncodingUtils;

import lombok.Getter;
import lombok.val;

/**
 * Configures the body, query string etc of a {@link ClientRequest}.
 *
 * <p>
 * Needed because, unfortunately, {@link ClientRequest} does not seem to allow
 * the query string to be set directly (only
 * {@literal ClientRequest#getQueryParameters() query parameters}). Instead, it is
 * necessary to {@literal ResteasyUriBuilder#replaceQuery(String) use} its underlying
 * {@literal ResteasyUriBuilder}.
 */
public class ClientRequestConfigurer {

    public static ClientRequestConfigurer create(
            final ClientExecutor executor,
            final String uriTemplate) {
        
        val uriBuilder = UriBuilder.fromUri(uriTemplate);
        val clientRequest = executor.createRequest(uriBuilder);
        return new ClientRequestConfigurer(clientRequest, uriBuilder);
    }

    @Getter private final ClientRequest clientRequest;
    private final UriBuilder uriBuilder;

    ClientRequestConfigurer(final ClientRequest clientRequest, final UriBuilder uriBuilder) {
        this.clientRequest = clientRequest;
        this.uriBuilder = uriBuilder;
    }

    public ClientRequestConfigurer accept(final MediaType mediaType) {
        clientRequest.accept(mediaType);
        return this;
    }

    public ClientRequestConfigurer header(final String name, final String value) {
        clientRequest.header(name, value);
        return this;
    }

    /**
     * Prerequisite to {@link #configureArgs(JsonRepresentation)} or
     * {@link #configureArgs(Map)}.
     */
    public ClientRequestConfigurer setHttpMethod(final RestfulHttpMethod httpMethod) {
        clientRequest.setHttpMethod(httpMethod.name());
        return this;
    }
    
    public ClientRequestConfigurer setHttpMethod(final RestfulHttpMethodHelper httpMethod) {
        clientRequest.setHttpMethod(httpMethod.name());
        return this;
    }

    /**
     * Used when creating a request with arguments to execute.
     *
     * <p>
     * Typical flow is:
     * <ul>
     * <li> RestfulClient#createRequest(RestfulHttpMethod, String)
     * <li> {@link RestfulRequest#withArg(RequestParameter, Object)} for each arg
     * <li> {@literal RestfulRequest#execute()} - which calls this method.
     * </ul>
     */
    public ClientRequestConfigurer configureArgs(final Map<RequestParameter<?>, Object> args) {
        if (clientRequest.getHttpMethod() == null) {
            throw new IllegalStateException("Must set up http method first");
        }

        final JsonRepresentation argsAsMap = JsonRepresentation.newMap();
        for (final RequestParameter<?> requestParam : args.keySet()) {
            put(args, requestParam, argsAsMap);
        }
        setUpArgs(argsAsMap);
        return this;
    }

    private <P> void put(final Map<RequestParameter<?>, Object> args, final RequestParameter<P> requestParam, final JsonRepresentation argsAsMap) {
        @SuppressWarnings("unchecked")
        final P value = (P) args.get(requestParam);
        final String valueStr = requestParam.getParser().asString(value);
        argsAsMap.mapPut(requestParam.getName(), valueStr);
    }

    /**
     * Used when following links - RestfulClient#follow(LinkRepresentation)
     */
    public ClientRequestConfigurer configureArgs(final JsonRepresentation requestArgs) {
        if (clientRequest.getHttpMethod() == null) {
            throw new IllegalStateException("Must set up http method first");
        }

        setUpArgs(requestArgs);
        return this;
    }

    /**
     * Called back from
     * {@literal RestfulHttpMethod#setUpArgs(ClientRequestConfigurer, JsonRepresentation)}
     */
    public ClientRequestConfigurer body(final JsonRepresentation requestArgs) {
        clientRequest.jsonPayload(requestArgs.toString());
        return this;
    }

    /**
     * Called back from
     * {@literal RestfulHttpMethod#setUpArgs(ClientRequestConfigurer, JsonRepresentation)}
     */
    public ClientRequestConfigurer queryString(final JsonRepresentation requestArgs) {
        if (requestArgs.size() == 0) {
            return this;
        }
        final String queryString = UrlEncodingUtils.urlEncode(requestArgs.toString());
        uriBuilder.replaceQuery(queryString);
        return this;
    }

    /**
     * Called back from
     * {@literal RestfulHttpMethod#setUpArgs(ClientRequestConfigurer, JsonRepresentation)}
     */
    public ClientRequestConfigurer queryArgs(final JsonRepresentation requestArgs) {

        requestArgs.streamMapEntries()
        .forEach(entry->{
            final String param = entry.getKey();
            final JsonRepresentation argRepr = entry.getValue();
            final String arg = UrlEncodingUtils.urlEncode(argRepr.asArg());
            clientRequest.addQueryParameter(param, arg);
        });

        return this;
    }

    private void setUpArgs(JsonRepresentation requestArgs) {
        val httpMethodHelper = RestfulHttpMethodHelper.valueOf(clientRequest.getHttpMethod());
        httpMethodHelper.setUpArgs(this, requestArgs);
    }

}
