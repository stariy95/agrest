package io.agrest.cayenne.cayenne.main.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import io.agrest.cayenne.cayenne.main.E8;

/**
 * Class _E7 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _E7 extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<E8> E8 = Property.create("e8", E8.class);

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setE8(E8 e8) {
        setToOneTarget("e8", e8, true);
    }

    public E8 getE8() {
        return (E8)readProperty("e8");
    }


}