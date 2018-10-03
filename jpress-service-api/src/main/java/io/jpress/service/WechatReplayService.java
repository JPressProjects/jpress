//package io.jpress.service;
//
//import com.jfinal.plugin.activerecord.Model;
//import com.jfinal.plugin.activerecord.Page;
//import io.jpress.model.WechatReplay;
//
//import java.util.List;
//
//public interface WechatReplayService {
//
//    /**
//     * find model by primary key
//     *
//     * @param id
//     * @return
//     */
//    public WechatReplay findById(Object id);
//
//
//    /**
//     * find all model
//     *
//     * @return all <WechatReplay
//     */
//    public List<WechatReplay> findAll();
//
//
//    /**
//     * delete model by primary key
//     *
//     * @param id
//     * @return success
//     */
//    public boolean deleteById(Object id);
//
//    /**
//     * 删除多个id
//     *
//     * @param ids
//     * @return
//     */
//    public boolean deleteByIds(Object... ids);
//
//
//
//    /**
//     * delete model
//     *
//     * @param model
//     * @return
//     */
//    public boolean delete(WechatReplay model);
//
//
//    /**
//     * save model to database
//     *
//     * @param model
//     * @return
//     */
//    public boolean save(WechatReplay model);
//
//
//    /**
//     * save or update model
//     *
//     * @param model
//     * @return if save or update success
//     */
//    public boolean saveOrUpdate(WechatReplay model);
//
//
//    /**
//     * update data model
//     *
//     * @param model
//     * @return
//     */
//    public boolean update(WechatReplay model);
//
//
//    public Page<WechatReplay> _paginate(int page, int pagesize, String keyword, String content);
//
//
//    public void join(Page<? extends Model> page, String joinOnField);
//
//    public void join(Page<? extends Model> page, String joinOnField, String[] attrs);
//
//    public void join(Page<? extends Model> page, String joinOnField, String joinName);
//
//    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);
//
//    public void join(List<? extends Model> models, String joinOnField);
//
//    public void join(List<? extends Model> models, String joinOnField, String[] attrs);
//
//    public void join(List<? extends Model> models, String joinOnField, String joinName);
//
//    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);
//
//    public void join(Model model, String joinOnField);
//
//    public void join(Model model, String joinOnField, String[] attrs);
//
//    public void join(Model model, String joinOnField, String joinName);
//
//    public void join(Model model, String joinOnField, String joinName, String[] attrs);
//
//    public void keep(Model model, String... attrs);
//
//    public void keep(List<? extends Model> models, String... attrs);
//}