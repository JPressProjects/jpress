package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.model.UserTag;
import io.jpress.service.UserTagService;

import java.util.ArrayList;
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

    @Override
    public List<UserTag> findOrCreateByTagString(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        boolean needClearCache = false;
        List<UserTag> userTags = new ArrayList<>();
        for (String tag : tags) {

            if (StrUtil.isBlank(tag)) {
                continue;
            }

            //slug不能包含字符串点 " . "，否则url不能被访问
            String slug = tag.contains(".")
                    ? tag.replace(".", "_")
                    : tag;

            Columns columns = Columns.create(Column.create("slug", slug));
            UserTag userTag = findFirstByColumns(columns);

            if (userTag == null) {
                userTag = new UserTag();
                userTag.setTitle(tag);
                userTag.setSlug(slug);
                userTag.setType("tag");
                userTag.save();

                needClearCache = true;
            }

            userTags.add(userTag);
        }

        if (needClearCache){
//            AopCache.removeAll("articleCategory");
        }

        return userTags;
    }

    @Override
    public List<UserTag> findHotList(int count) {
        return findListByColumns(Columns.EMPTY,"count desc",count);
    }

    @Override
    public void doUpdateTags(long userId, Long[] tagIds) {
        Db.tx(() -> {
            Db.update("delete from user_tag_mapping where user_id = ?", userId);

            if (tagIds != null && tagIds.length > 0) {
                List<Record> records = new ArrayList<>();
                for (long tagId : tagIds) {
                    Record record = new Record();
                    record.set("user_id", userId);
                    record.set("tag_id", tagId);
                    records.add(record);
                }
                Db.batchSave("user_tag_mapping", records, records.size());
            }

            return true;
        });
    }
}