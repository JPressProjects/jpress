package io.jpress.commons.service;


import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import io.jboot.db.model.JbootModel;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ClassUtil;
import io.jpress.SiteContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JPressServiceBase<M extends JbootModel<M>> extends JbootServiceBase<M> {

    @Override
    protected M initDao() {
        Type type = ClassUtil.getUsefulClass(getClass()).getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<M> modelClass = (Class<M>) ((ParameterizedType) type).getActualTypeArguments()[0];
            Table table = TableMapping.me().getTable(modelClass);
            if (table != null && table.hasColumnLabel("site_id")) {
                return SiteModelProxy.get(modelClass);
            }
            return ClassUtil.newInstance(modelClass, false);
        }

        LogKit.warn("Not define Model class in Servlce: " + ClassUtil.getUsefulClass(getClass()));
        return null;
    }


    @Override
    public Object save(M model) {
        Table table = TableMapping.me().getTable(model.getClass());
        if (table != null && table.hasColumnLabel("site_id") && model.get("site_id") == null) {
            model.setOrPut("site_id", SiteContext.getSiteId());
        }
        return super.save(model);
    }


}
