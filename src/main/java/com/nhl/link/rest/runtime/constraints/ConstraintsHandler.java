package com.nhl.link.rest.runtime.constraints;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.core.Response.Status;

import org.apache.cayenne.di.Inject;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhl.link.rest.DataResponse;
import com.nhl.link.rest.Entity;
import com.nhl.link.rest.EntityUpdate;
import com.nhl.link.rest.ImmutableTreeConstraints;
import com.nhl.link.rest.LinkRestException;
import com.nhl.link.rest.SizeConstraints;
import com.nhl.link.rest.TreeConstraints;
import com.nhl.link.rest.UpdateResponse;
import com.nhl.link.rest.annotation.ClientReadable;
import com.nhl.link.rest.annotation.ClientWritable;
import com.nhl.link.rest.runtime.cayenne.ICayennePersister;
import com.nhl.link.rest.runtime.parser.PathConstants;

/**
 * An {@link IConstraintsHandler} that ensures that no target attributes exceed
 * the defined bounds.
 * 
 * @since 1.5
 */
public class ConstraintsHandler implements IConstraintsHandler {

	public static final String DEFAULT_READ_CONSTRAINTS_LIST = "linkrest.constraints.read.list";
	public static final String DEFAULT_WRITE_CONSTRAINTS_LIST = "linkrest.constraints.write.list";

	private static final TreeConstraints<Object> NO_CONSTRAINTS = TreeConstraints.excludeAll(Object.class);

	private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintsHandler.class);

	private ICayennePersister persister;
	private ConcurrentMap<Class<?>, TreeConstraints<?>> readDefault;
	private ConcurrentMap<Class<?>, TreeConstraints<?>> writeDefault;

	public ConstraintsHandler(@Inject ICayennePersister persister,
			@Inject(DEFAULT_READ_CONSTRAINTS_LIST) List<TreeConstraints<?>> defaultReadConstraints,
			@Inject(DEFAULT_WRITE_CONSTRAINTS_LIST) List<TreeConstraints<?>> defaultWriteConstraints) {

		this.persister = persister;

		// note that explicit defaults override annotations
		this.readDefault = new ConcurrentHashMap<>();
		for (TreeConstraints<?> c : defaultReadConstraints) {
			this.readDefault.put(c.getType(), c);
		}

		this.writeDefault = new ConcurrentHashMap<>();
		for (TreeConstraints<?> c : defaultWriteConstraints) {
			this.writeDefault.put(c.getType(), c);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> TreeConstraints<T> readDefault(Class<T> type) {

		TreeConstraints<?> c = readDefault.get(type);

		if (c == null) {
			// no synchronization.. hopefully compiling a single type of
			// constraints multiple times is not very expensive
			TreeConstraints<?> newC = compileRead(type);
			TreeConstraints<?> oldC = readDefault.putIfAbsent(type, newC);
			c = oldC != null ? oldC : newC;
		}

		return (TreeConstraints<T>) (c == NO_CONSTRAINTS ? null : c);
	}

	@SuppressWarnings("unchecked")
	private <T> TreeConstraints<T> writeDefault(Class<T> type) {

		TreeConstraints<?> c = writeDefault.get(type);

		if (c == null) {
			// no synchronization.. hopefully compiling a single type of
			// constraints multiple times is not very expensive
			TreeConstraints<?> newC = compileWrite(type);
			TreeConstraints<?> oldC = writeDefault.putIfAbsent(type, newC);
			c = oldC != null ? oldC : newC;
		}

		return (TreeConstraints<T>) (c == NO_CONSTRAINTS ? null : c);
	}

	private TreeConstraints<?> compileRead(Class<?> type) {
		ClientReadable a = type.getAnnotation(ClientReadable.class);
		return a != null ? compile(type, a.value(), a.id(), true) : NO_CONSTRAINTS;
	}

	private TreeConstraints<?> compileWrite(Class<?> type) {
		ClientWritable a = type.getAnnotation(ClientWritable.class);
		return a != null ? compile(type, a.value(), a.id(), false) : NO_CONSTRAINTS;
	}

	private TreeConstraints<?> compile(Class<?> type, String[] properties, boolean id, boolean read) {

		ObjEntity e = persister.entityResolver().getObjEntity(type);
		if (e == null) {
			throw new LinkRestException(Status.BAD_REQUEST, "Invalid entity: " + type.getName());
		}

		TreeConstraints<?> c = TreeConstraints.excludeAll(type).includeId(id);

		for (String p : properties) {
			ObjAttribute a = e.getAttribute(p);
			if (a != null) {
				c.attribute(p);
				continue;
			}

			ObjRelationship r = e.getRelationship(p);
			if (r != null) {

				Class<?> childType = r.getTargetEntity().getJavaClass();

				// TODO: java 8: instead of 'read' we need to use a lambda
				TreeConstraints<?> childC = read ? compileRead(childType) : compileWrite(childType);

				// no annotation - allow all attributes...
				if (childC == NO_CONSTRAINTS) {
					childC = TreeConstraints.idAndAttributes(childType);
				}

				c.path(p, childC);

				continue;
			}

			throw new LinkRestException(Status.INTERNAL_SERVER_ERROR, "Invalid property: " + e.getName() + "." + p);

		}
		return c;
	}

	@Override
	public <T> void constrainUpdate(UpdateResponse<T> response, TreeConstraints<T> writeConstraints) {

		TreeConstraints<T> c = writeConstraints != null ? writeConstraints : writeDefault(response.getType());

		if (c != null && !response.getUpdates().isEmpty()) {
			ImmutableTreeConstraints compiled = c.build(response);
			applyWriteTreeConstraints(response, compiled);
		}
	}

	@Override
	public <T> void constrainResponse(DataResponse<T> response, SizeConstraints sizeConstraints,
			TreeConstraints<T> readConstraints) {

		if (sizeConstraints != null) {
			applyReadSizeConstraints(response, sizeConstraints);
		}

		TreeConstraints<T> c = readConstraints != null ? readConstraints : readDefault(response.getType());

		// entity - ensure attribute/relationship tree span of source is not
		// exceeded in target. Null target means we don't need to worry about
		// unauthorized attributes and relationships
		if (c != null && response.getEntity() != null) {
			ImmutableTreeConstraints constraints = c.build(response);
			applyReadTreeConstraints(response.getEntity(), constraints);
		}
	}

	protected void applyReadSizeConstraints(DataResponse<?> response, SizeConstraints constraints) {
		// fetchOffset - do not exceed source offset
		int upperOffset = constraints.getFetchOffset();
		if (upperOffset > 0 && response.getFetchOffset() > upperOffset) {
			LOGGER.info("Reducing fetch offset from " + response.getFetchOffset() + " to max allowed value of "
					+ upperOffset);
			response.withFetchOffset(upperOffset);
		}

		// fetchLimit - do not exceed source limit
		int upperLimit = constraints.getFetchLimit();
		if (upperLimit > 0 && response.getFetchLimit() > upperLimit) {
			LOGGER.info("Reducing fetch limit from " + response.getFetchLimit() + " to max allowed value of "
					+ upperLimit);
			response.withFetchLimit(upperLimit);
		}
	}

	protected void applyReadTreeConstraints(Entity<?> target, ImmutableTreeConstraints constraints) {

		if (!constraints.isIdIncluded()) {
			target.excludeId();
		}

		Iterator<String> ait = target.getAttributes().iterator();
		while (ait.hasNext()) {

			String a = ait.next();
			if (!constraints.hasAttribute(a)) {

				// do not report default properties, as this wasn't a client's
				// fault it go there..
				if (!target.isDefault(a)) {
					LOGGER.info("Attribute not allowed, removing: " + a);
				}

				ait.remove();
			}
		}

		Iterator<Entry<String, Entity<?>>> rit = target.getChildren().entrySet().iterator();
		while (rit.hasNext()) {

			Entry<String, Entity<?>> e = rit.next();
			ImmutableTreeConstraints sourceChild = constraints.getChild(e.getKey());
			if (sourceChild != null) {

				// removing recursively ... the depth or recursion depends on
				// the depth of target, which is server-controlled. So it should
				// be a reasonably safe operation in regard to stack overflow
				applyReadTreeConstraints(e.getValue(), sourceChild);
			} else {

				// do not report default properties, as this wasn't a client's
				// fault it go there..
				if (!target.isDefault(e.getKey())) {
					LOGGER.info("Relationship not allowed, removing: " + e.getKey());
				}

				rit.remove();
			}
		}

		if (constraints.getQualifier() != null) {
			target.andQualifier(constraints.getQualifier());
		}

		// process 'mapByPath' ... treat it as a regular relationship/attribute
		// path.. Ignoring 'mapBy', presuming it matches the path. This way we
		// can simply check for one single path, not for all attributes in the
		// entities involved.

		if (target.getMapByPath() != null && !allowedMapBy(constraints, target.getMapByPath())) {
			LOGGER.info("'mapBy' not allowed, removing: " + target.getMapByPath());
			target.mapBy(null, null);
		}
	}

	protected void applyWriteTreeConstraints(UpdateResponse<?> response, ImmutableTreeConstraints constraints) {

		if (!constraints.isIdIncluded()) {
			response.disallowIdUpdates();
		}

		// updates are not hierarchical yet, so simply check attributes...
		// TODO: updates may contain FKs ... need to handle that

		for (EntityUpdate u : response.getUpdates()) {

			// exclude disallowed attributes
			Iterator<Entry<String, Object>> it = u.getValues().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> e = it.next();
				if (!constraints.hasAttribute(e.getKey())) {

					// do not report default properties, as this wasn't a
					// client's fault it go there..
					if (!response.getEntity().isDefault(e.getKey())) {
						LOGGER.info("Attribute not allowed, removing: " + e.getKey() + " for id " + u.getId());
					}

					it.remove();
				}
			}
		}
	}

	protected boolean allowedMapBy(ImmutableTreeConstraints source, String path) {

		int dot = path.indexOf(PathConstants.DOT);

		if (dot == 0) {
			throw new LinkRestException(Status.BAD_REQUEST, "Path starts with dot: " + path);
		}

		if (dot == path.length() - 1) {
			throw new LinkRestException(Status.BAD_REQUEST, "Path ends with dot: " + path);
		}

		if (dot > 0) {
			// process intermediate component
			String property = path.substring(0, dot);
			ImmutableTreeConstraints child = source.getChild(property);
			return child != null && allowedMapBy(child, path.substring(dot + 1));

		} else {
			return allowedMapBy_LastComponent(source, path);
		}
	}

	protected boolean allowedMapBy_LastComponent(ImmutableTreeConstraints source, String path) {

		// process last component
		String property = path;

		if (property == null || property.length() == 0 || property.equals(PathConstants.ID_PK_ATTRIBUTE)) {
			return source.isIdIncluded();
		}

		if (source.hasAttribute(property)) {
			return true;
		}

		ImmutableTreeConstraints child = source.getChild(property);
		return child != null && allowedMapBy_LastComponent(child, null);
	}
}
