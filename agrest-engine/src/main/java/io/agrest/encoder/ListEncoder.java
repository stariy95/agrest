package io.agrest.encoder;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.List;

public class ListEncoder implements CollectionEncoder {

    private final Encoder elementEncoder;

    private int offset;
    private int limit;

    public ListEncoder(Encoder elementEncoder) {
        this.elementEncoder = elementEncoder;
    }

    @Override
    public int visitEntities(Object root, EncoderVisitor visitor) {
        if (root == null) {
            return 0;
        }

        List<?> objects = toList(root);

        Counter counter = new Counter();

        rewind(counter, objects, offset);
        return visit(counter, objects, limit > 0 ? limit : Integer.MAX_VALUE, visitor);
    }

    public ListEncoder withOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public ListEncoder withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * @since 2.0
     */
    public int encodeAndGetTotal(String propertyName, Object object, JsonGenerator out) throws IOException {
        if (propertyName != null) {
            out.writeFieldName(propertyName);
        }

        List<?> objects = toList(object);

        out.writeStartArray();

        Counter counter = new Counter();

        // to get valid counts and offsets, we need to do the following:
        // rewind head -> encode -> rewind tail
        rewind(counter, objects, offset);
        encode(counter, objects, limit > 0 ? limit : Integer.MAX_VALUE, out);
        rewind(counter, objects, objects.size());

        out.writeEndArray();

        return counter.getTotal();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<?> toList(Object object) {

        if (object == null) {
            throw new IllegalStateException("Unexpected null list");
        }

        if (!(object instanceof List)) {
            throw new IllegalStateException(
                    "Unexpected object type. Should be a List, got: " + object.getClass().getName());
        }

        return (List) object;
    }

    private void rewind(Counter c, List<?> objects, int limit) {

        int length = objects.size();

        // if no filtering is in effect, "position" and "rewound" would increment together
        int delta = Math.min(length - c.position, limit - c.rewound);
        c.rewound += delta;
        c.position += delta;
    }

    private void encode(Counter c, List<?> objects, int limit, JsonGenerator out) throws IOException {

        int length = objects.size();

        for (; c.position < length && c.encoded < limit; c.position++) {
            if (elementEncoder.encode(null, objects.get(c.position), out)) {
                c.encoded++;
            }
        }
    }

    private int visit(Counter c, List<?> objects, int limit, EncoderVisitor visitor) {
        int length = objects.size();

        for (; c.position < length && c.encoded < limit; c.position++) {
            Object o = objects.get(c.position);
            int bitmask = elementEncoder.visitEntities(o, visitor);
            c.encoded++;

            if ((bitmask & VISIT_SKIP_ALL) != 0) {
                return VISIT_SKIP_ALL;
            }
        }

        return VISIT_CONTINUE;
    }

    final class Counter {
        int position;

        int encoded;
        int rewound;

        int getTotal() {
            return encoded + rewound;
        }
    }
}
