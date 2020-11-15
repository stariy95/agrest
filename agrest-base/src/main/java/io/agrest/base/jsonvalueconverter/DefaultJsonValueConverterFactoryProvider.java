package io.agrest.base.jsonvalueconverter;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.cayenne.di.DIRuntimeException;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.agrest.base.reflect.Types.typeForName;

/**
 * @since 2.10
 */
public class DefaultJsonValueConverterFactoryProvider implements Provider<IJsonValueConverterFactory> {

    private static final JsonValueConverter<JsonNode> DO_NOTHING_CONVERTER = node -> node;

    private Map<String, JsonValueConverter<?>> injectedConverters;

    public DefaultJsonValueConverterFactoryProvider(@Inject Map<String, JsonValueConverter<?>> injectedConverters) {
        this.injectedConverters = injectedConverters;
    }

    @Override
    public IJsonValueConverterFactory get() throws DIRuntimeException {

        Map<Class<?>, JsonValueConverter<?>> converters =
                appendInjectedConverters(
                        appendKnownConverters(new HashMap<>()));

        return new DefaultJsonValueConverterFactory(converters, defaultConverter());
    }

    protected JsonValueConverter<?> defaultConverter() {
        return GenericConverter.converter();
    }

    protected Map<Class<?>, JsonValueConverter<?>> appendKnownConverters(Map<Class<?>, JsonValueConverter<?>> converters) {

        converters.put(Object.class, GenericConverter.converter());
        converters.put(Float.class, FloatConverter.converter());
        converters.put(float.class, FloatConverter.converter());
        converters.put(Double.class, DoubleConverter.converter());
        converters.put(double.class, DoubleConverter.converter());
        converters.put(Long.class, LongConverter.converter());
        converters.put(long.class, LongConverter.converter());
        converters.put(Date.class, UtcDateConverter.converter());
        converters.put(java.sql.Date.class, UtcDateConverter.converter(java.sql.Date.class));
        converters.put(java.sql.Time.class, UtcDateConverter.converter(java.sql.Time.class));
        converters.put(java.sql.Timestamp.class, UtcDateConverter.converter(java.sql.Timestamp.class));
        converters.put(byte[].class, Base64Converter.converter());
        converters.put(LocalDate.class, ISOLocalDateConverter.converter());
        converters.put(LocalTime.class, ISOLocalTimeConverter.converter());
        converters.put(LocalDateTime.class, ISOLocalDateTimeConverter.converter());
        converters.put(OffsetDateTime.class, ISOOffsetDateTimeConverter.converter());
        converters.put(JsonNode.class, DO_NOTHING_CONVERTER);

        return converters;
    }

    protected Map<Class<?>, JsonValueConverter<?>> appendInjectedConverters(Map<Class<?>, JsonValueConverter<?>> converters) {
        injectedConverters.forEach((k, v) -> converters.put(typeForName(k), v));
        return converters;
    }
}
