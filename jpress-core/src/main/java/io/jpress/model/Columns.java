package io.jpress.model;

import io.jboot.db.model.Column;

/**
 * @author Eric.Huang
 * @date 2019-05-17 16:05
 * @package io.jpress.model
 **/

public class Columns extends io.jboot.db.model.Columns {

    public Columns() {
    }

    public static Columns create() {
        return new Columns();
    }

    public io.jboot.db.model.Columns regexp(String name, Object value) {
        this.add(Column.create(name, value, " regexp "));
        return this;
    }

}
