package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserAmountStatementService;
import io.jpress.model.UserAmountStatement;
import io.jboot.service.JbootServiceBase;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Bean
public class UserAmountStatementServiceProvider extends JbootServiceBase<UserAmountStatement> implements UserAmountStatementService {

    @Override
    public List<UserAmountStatement> findListByUserId(Object userId, int count) {
        return DAO.findListByColumns(Columns.create("user_id", userId), "id desc", count);
    }

    @Override
    public BigDecimal queryIncomeAmount(Long userId) {
        return Db.queryBigDecimal("select sum(change_amount) from user_amount_statement where user_id = ? and change_amount > 0 and created > ? "
                , userId
                , DateUtils.truncate(new Date(), Calendar.MONTH));
    }

    @Override
    public BigDecimal queryPayAmount(Long userId) {
        return Db.queryBigDecimal("select sum(change_amount) from user_amount_statement where user_id = ? and change_amount < 0 and action  != ?  and created > ?"
                , userId
                , UserAmountStatement.ACTION_PAYOUT
                , DateUtils.truncate(new Date(), Calendar.MONTH));
    }

    @Override
    public BigDecimal queryPayoutAmount(Long userId) {
        return Db.queryBigDecimal("select sum(change_amount) from user_amount_statement where user_id = ? and change_amount < 0  and action  = ? and created > ?"
                , userId
                , UserAmountStatement.ACTION_PAYOUT
                , DateUtils.truncate(new Date(), Calendar.MONTH));
    }

    @Override
    public UserAmountStatement findOneByUserIdAndRelative(Long userId, String relativeType, Long relativeId) {
        return findFirstByColumns(Columns.create("user_id", userId)
                .eq("action_relative_type", relativeType)
                .eq("action_relative_id", relativeId)
        );
    }
}