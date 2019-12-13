/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.commons.finance;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import io.jpress.core.finance.PaymentSuccessListener;
import io.jpress.model.Member;
import io.jpress.model.MemberGroup;
import io.jpress.model.MemberJoinedRecord;
import io.jpress.model.PaymentRecord;
import io.jpress.service.MemberGroupService;
import io.jpress.service.MemberJoinedRecordService;
import io.jpress.service.MemberService;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * @author michael yang
 */
public class MemberPaymentSuccessListener implements PaymentSuccessListener {

    public static final Log LOG = Log.getLog(MemberPaymentSuccessListener.class);


    @Inject
    private MemberService memberService;

    @Inject
    private  MemberGroupService groupService;

    @Inject
    private MemberJoinedRecordService joinedRecordService;

    @Override
    public void onSuccess(PaymentRecord payment) {

        if (PaymentRecord.TRX_TYPE_MEMBER.equals(payment.getTrxType())) {

            boolean updateSucess = Db.tx(() -> {

                MemberGroup group = groupService.findById(payment.getProductRelativeId());

                Member member = memberService.findByGroupIdAndUserId(group.getId(), payment.getPayerUserId());
                if (member == null) {
                    member = new Member();
                    member.setGroupId(group.getId());
                    member.setUserId(payment.getPayerUserId());
                    member.setDuetime(DateUtils.addDays(new Date(), group.getValidTerm()));
                    member.setSource(Member.SOURCE_BUY);
                    member.setStatus(Member.STATUS_NORMAL);
                    member.setCreated(new Date());
                    member.setModified(new Date());
                } else {

                    Date oldDuetime = member.getDuetime();

                    //如果该会员之前有记录，但是会员早就到期了，重新续费应该按现在时间开始计算
                    if (oldDuetime.getTime() < System.currentTimeMillis()) {
                        oldDuetime = new Date();
                    }

                    member.setDuetime(DateUtils.addDays(oldDuetime, group.getValidTerm()));
                    member.setModified(new Date());
                }


                MemberJoinedRecord joinedRecord = new MemberJoinedRecord();
                joinedRecord.setUserId(payment.getPayerUserId());
                joinedRecord.setGroupId(group.getId());
                joinedRecord.setGroupName(group.getName());
                joinedRecord.setJoinPrice(payment.getPaySuccessAmount());
                joinedRecord.setJoinCount(1);
                joinedRecord.setCreated(new Date());
                joinedRecord.setValidTerm(group.getValidTerm());
                joinedRecord.setJoinType(MemberJoinedRecord.JOIN_TYPE_BUY);
                joinedRecord.setJoinFrom(MemberJoinedRecord.JOIN_FROM_BUY);

                if (memberService.saveOrUpdate(member) == null) {
                    return false;
                }

                if (joinedRecordService.save(joinedRecord) == null) {
                    return false;
                }

                return true;
            });

            if (!updateSucess) {
                LOG.error("update user member fail in pay success。");
            }

        }

    }
}
