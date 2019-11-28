package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.UserTag;
import io.jpress.service.UserTagService;

import java.util.List;
import java.util.stream.Collectors;

@Bean
public class UserTagServiceProvider extends JbootServiceBase<UserTag> implements UserTagService {

    @Override
    public UserTag findFirstByTag(String tag) {
        return findFirstByColumns(Columns.create("slug",tag));
    }

    @Override
    public List<UserTag> findListByUserId(Object userId) {
        List<Record> mapping = Db.find("select * from user_tag_mapping where user_id = ?",userId);
        if (mapping == null || mapping.isEmpty()){
            return null;
        }

        return mapping.stream()
                .map(record -> findById(record.get("tag_id")))
                .collect(Collectors.toList());

    }
}