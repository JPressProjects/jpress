package io.jpress.module.form.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.module.form.service.FormDatasourceItemService;
import io.jpress.module.form.model.FormDatasourceItem;
import io.jpress.commons.service.JPressServiceBase;

import javax.validation.constraints.NotNull;

@Bean
public class FormDatasourceItemServiceProvider extends JPressServiceBase<FormDatasourceItem> implements FormDatasourceItemService {



    /**
     * 根据 dict id 删除数据
     *
     * @param dictId
     * @return boolean
     */
    @Override
    public boolean deleteByDictId(@NotNull Long dictId) {

        return  DAO.deleteByColumns(Columns.create().eq("dict_id",dictId));
    }

}