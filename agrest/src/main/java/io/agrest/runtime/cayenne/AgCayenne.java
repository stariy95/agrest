package io.agrest.runtime.cayenne;

import io.agrest.resolver.NestedDataResolver;
import io.agrest.resolver.RootDataResolver;
import io.agrest.runtime.AgRuntime;
import io.agrest.runtime.cayenne.processor.select.CayenneQueryAssembler;
import io.agrest.runtime.cayenne.processor.select.ViaParentPrefetchResolver;
import io.agrest.runtime.cayenne.processor.select.ViaQueryResolver;
import io.agrest.runtime.cayenne.processor.select.ViaQueryWithParentIdsResolver;
import io.agrest.runtime.cayenne.processor.select.ViaQueryWithParentQualifierResolver;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.query.PrefetchTreeNode;

import javax.ws.rs.core.Configuration;

/**
 * Provides access to Cayenne backend services within Agrest / JAX RS runtume.
 *
 * @since 3.4
 */
public class AgCayenne {

    public static ICayennePersister persister(Configuration config) {
        return AgRuntime.service(ICayennePersister.class, config);
    }

    public static <T extends DataObject> RootDataResolver<T> rootResolverViaQuery(Configuration config) {
        return (RootDataResolver<T>) new ViaQueryResolver(
                AgRuntime.service(CayenneQueryAssembler.class, config),
                persister(config));
    }

    public static <T extends DataObject> NestedDataResolver<T> resolverViaDisjointParentPrefetch(Configuration config) {
        return (NestedDataResolver<T>) new ViaParentPrefetchResolver(
                AgRuntime.service(CayenneQueryAssembler.class, config),
                persister(config),
                PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS);
    }

    public static <T extends DataObject> NestedDataResolver<T> resolverViaJointParentPrefetch(Configuration config) {
        return (NestedDataResolver<T>) new ViaParentPrefetchResolver(
                AgRuntime.service(CayenneQueryAssembler.class, config),
                persister(config),
                PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);
    }

    public static <T extends DataObject> NestedDataResolver<T> resolverViaQueryWithParentQualifier(Configuration config) {
        return (NestedDataResolver<T>) new ViaQueryWithParentQualifierResolver(
                AgRuntime.service(CayenneQueryAssembler.class, config),
                persister(config));
    }

    public static <T extends DataObject> NestedDataResolver<T> resolverViaQueryWithParentIds(Configuration config) {
        return (NestedDataResolver<T>) new ViaQueryWithParentIdsResolver(
                AgRuntime.service(CayenneQueryAssembler.class, config),
                persister(config));
    }
}