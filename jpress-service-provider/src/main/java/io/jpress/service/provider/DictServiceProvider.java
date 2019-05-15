package io.jpress.service.provider;

import com.google.common.base.Splitter;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Dict;
import io.jpress.service.DictService;

import java.util.List;

@Bean
public class DictServiceProvider extends JbootServiceBase<Dict> implements DictService {

    @Override
    public List<Dict> findByType(String type) {
        Columns columns = Columns.create();
        columns.eq("type", type);
        return DAO.findListByColumns(columns, "value asc");
    }

    @Override
    public boolean deleteByIds(Object ids) {

        List<String> list = Splitter.on(",").splitToList(ids.toString());
        list.stream().forEach(id -> {
            deleteById(id);
        });

        return true;
    }


}