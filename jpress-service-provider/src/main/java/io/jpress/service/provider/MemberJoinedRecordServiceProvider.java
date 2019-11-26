package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.MemberGroupService;
import io.jpress.service.MemberJoinedRecordService;
import io.jpress.model.MemberJoinedRecord;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.UserService;

@Bean
public class MemberJoinedRecordServiceProvider extends JbootServiceBase<MemberJoinedRecord> implements MemberJoinedRecordService {

    @Inject
    private UserService userService;

    @Inject
    private MemberGroupService groupService;

    @Override
    public Page<MemberJoinedRecord> paginateByGroupId(int page, int pageSize, Long groupId) {
        return join(paginateByColumns(page,pageSize, Columns.create("group_id",groupId),"id desc"));
    }

    private Page<MemberJoinedRecord> join(Page<MemberJoinedRecord> page){
        userService.join(page,"user_id");
        groupService.join(page,"group_id","group");
        return page;
    }
}