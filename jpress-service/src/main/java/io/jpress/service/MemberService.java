package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.Member;

import java.util.List;

public interface MemberService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Member findById(Object id);


    /**
     * find all model
     *
     * @return all <Member
     */
    public List<Member> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Member model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Member model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Member model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Member model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Member> paginate(int page, int pageSize);


    public List<Member> findListByUserId(Object userId);


    public Member findByGroupIdAndUserId(Long groupId, Long payerUserId);

    boolean isMember(long userid);
}
