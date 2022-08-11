package io.jpress.service;

import io.jboot.db.model.Columns;
import io.jpress.model.TemplateBlockOption;

public interface TemplateBlockOptionService {

    /**
     * 根据主键查找Model
     *
     * @param templateId
     * @param siteId
     * @return
     */
    TemplateBlockOption findById(Object templateId, Long siteId);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    TemplateBlockOption findFirstByColumns(Columns columns);


    /**
     * 保存到数据库
     *
     * @param model
     * @return id if success
     */
    Object save(TemplateBlockOption model);


    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(TemplateBlockOption model);


}