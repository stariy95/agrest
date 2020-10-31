package io.agrest.provider;

import io.agrest.AgException;
import io.agrest.EntityUpdate;
import io.agrest.base.reflect.Types;
import io.agrest.meta.AgDataMap;
import io.agrest.runtime.AgRuntime;
import io.agrest.runtime.protocol.IEntityUpdateParser;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * A provider of {@link MessageBodyReader} for Collections of {@link EntityUpdate} parameters.
 *
 * @since 1.20
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class EntityUpdateCollectionReader<T> implements MessageBodyReader<Collection<EntityUpdate<T>>> {

    private EntityUpdateReaderProcessor reader;

    public EntityUpdateCollectionReader(@Context Configuration config) {
        this.reader = new EntityUpdateReaderProcessor(
                AgRuntime.service(IEntityUpdateParser.class, config),
                AgRuntime.service(AgDataMap.class, config));
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!Collection.class.isAssignableFrom(type) || !MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
            return false;
        }

        return Types.unwrapTypeArgument(genericType)
                .filter(collectionParam -> collectionParam instanceof ParameterizedType)
                .map(collectionParam -> (ParameterizedType) collectionParam)
                .filter(collectionParam -> EntityUpdate.class.equals((collectionParam).getRawType()))
                .isPresent();
    }

    @Override
    public Collection<EntityUpdate<T>> readFrom(
            Class<Collection<EntityUpdate<T>>> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws WebApplicationException {

        Type entityUpdateType = Types.unwrapTypeArgument(genericType)
                .orElseThrow(() -> new AgException(Status.INTERNAL_SERVER_ERROR,
                        "Invalid request entity collection type: " + genericType));

        return reader.read(entityUpdateType, entityStream);
    }
}
