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
package org.apache.isis.persistence.jdo.integration.metamodel.facets.entity;

import org.apache.isis.commons.internal.base._Lazy;
import org.apache.isis.core.interaction.session.InteractionTracker;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.entity.EntityFacet;
import org.apache.isis.persistence.jdo.integration.persistence.JdoPersistenceSession;

public abstract class JdoEntityFacetAbstract 
extends FacetAbstract 
implements EntityFacet {

    public static Class<? extends Facet> type() {
        return EntityFacet.class;
    }

    public JdoEntityFacetAbstract(
            final FacetHolder holder) {
        
        super(JdoEntityFacetAbstract.type(), holder, Derivation.NOT_DERIVED);
        super.setFacetAliasType(EntityFacet.class);
    }
    
    protected JdoPersistenceSession getJdoPersistenceSession() {
        return isisInteractionTrackerLazy.get().currentInteractionSession()
                .map(interactionSession->interactionSession.getAttribute(JdoPersistenceSession.class))
                .orElse(null);
    }
    
    // -- INTERACTION TRACKER LAZY LOOKUP
    
    // memoizes the lookup, just an optimization 
    private final _Lazy<InteractionTracker> isisInteractionTrackerLazy = _Lazy.threadSafe(
            ()->getServiceRegistry().lookupServiceElseFail(InteractionTracker.class));
    
}
