package com.nhl.link.rest.it.fixture.cayenne.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _E6 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _E6 extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String CHAR_ID_PK_COLUMN = "char_id";

    public static final Property<String> CHAR_COLUMN = new Property<String>("charColumn");

    public void setCharColumn(String charColumn) {
        writeProperty("charColumn", charColumn);
    }
    public String getCharColumn() {
        return (String)readProperty("charColumn");
    }

}
