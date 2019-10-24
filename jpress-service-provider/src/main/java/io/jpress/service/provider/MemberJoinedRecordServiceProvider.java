package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MemberJoinedRecordService;
import io.jpress.model.MemberJoinedRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberJoinedRecordServiceProvider extends JbootServiceBase<MemberJoinedRecord> implements MemberJoinedRecordService {

}