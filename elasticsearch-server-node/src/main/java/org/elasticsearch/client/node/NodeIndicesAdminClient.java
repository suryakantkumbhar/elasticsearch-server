/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.client.node;

import com.google.common.collect.ImmutableMap;
import org.elasticsearch.action.*;
import org.elasticsearch.action.admin.indices.IndicesAction;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.client.ServerIndicesAdminClient;
import org.elasticsearch.client.support.AbstractIndicesAdminClient;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.util.Map;

/**
 *
 */
public class NodeIndicesAdminClient extends AbstractIndicesAdminClient implements ServerIndicesAdminClient {

    private final Settings settings;
    
    private final ImmutableMap<IndicesAction, TransportAction> actions;

    @Inject
    public NodeIndicesAdminClient(Settings settings, 
           Map<GenericAction, TransportAction> actions) {
        super();
        this.settings = settings;
        MapBuilder<IndicesAction, TransportAction> actionsBuilder = new MapBuilder<IndicesAction, TransportAction>();
        for (Map.Entry<GenericAction, TransportAction> entry : actions.entrySet()) {
            if (entry.getKey() instanceof IndicesAction) {
                actionsBuilder.put((IndicesAction) entry.getKey(), entry.getValue());
            }
        }
        this.actions = actionsBuilder.immutableMap();
    }

    @Override
    public Settings settings() {
        return this.settings;
    }

    @Override
    public void close() {
        // nothing really to do
    }


    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> 
            ActionFuture<Response> execute(IndicesAction<Request, Response, RequestBuilder> action, Request request) {
        TransportAction<Request, Response> transportAction = actions.get(action);
        return transportAction.execute(request);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> 
            void execute(IndicesAction<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
        TransportAction<Request, Response> transportAction = actions.get(action);
        transportAction.execute(request, listener);
    }
}