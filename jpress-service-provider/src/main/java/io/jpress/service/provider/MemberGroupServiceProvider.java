package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.MemberGroup;
import io.jpress.service.MemberGroupService;

import java.util.List;
import java.util.stream.Collectors;

@Bean
public class MemberGroupServiceProvider extends JbootServiceBase<MemberGroup> implements MemberGroupService {

    @Override
    public List<MemberGroup> findUcenterList() {
        return findAll().stream()
                .filter(memberGroup -> memberGroup.isNormal() && memberGroup.isShowInUcenter())
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberGroup> findNormalList() {
        return findAll().stream()
                .filter(memberGroup -> memberGroup.isNormal())
                .collect(Collectors.toList());
    }
}