package io.jpress.module.job.controller.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import io.jboot.db.model.Columns;
import io.jboot.web.attachment.AttachmentManager;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobApply;
import io.jpress.module.job.service.JobApplyService;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.TemplateControllerBase;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/job/apply")
public class JobApplyController extends TemplateControllerBase {

    @Inject
    private JobService jobService;

    @Inject
    private JobApplyService jobApplyService;


    public void index(){

        Long id = getLong();

        Job job = jobService.findByIdWithInfo(id);

        if(id == null || job == null){
            renderError(404);
            return;
        }

        setAttr("job",job);



        render("job_user_apply.html");
    }


    public void uploadFile(){

        UploadFile file = getFile("file");

        if(file == null){
            renderFailJson("文件上传失败，请重新上传");
            return;
        }

        Map<String,Object> map = new HashMap<>();

        String path = AttachmentManager.me().saveFile(file.getFile());
        map.put("state", true);
        map.put("filePath", path);
        map.put("fileName", file.getOriginalFileName());

        renderJson(map);
    }


    public void doSave(){

        JobApply entry = getModel(JobApply.class, "jobApply");

        if(entry == null){
            renderError(404);
            return;
        }

        //通过job_id  mobile 来确定是否已经 申请
        Columns columns = new Columns();
        columns.eq("mobile",entry.getMobile());
        columns.eq("job_id",entry.getJobId());
        JobApply jobApply = jobApplyService.findFirstByColumns(columns);


        if(jobApply != null){
            renderFailJson("您已经申请过了,请勿重复申请");
            return;
        }

        entry.setWithViewed(false);
        entry.setWithDisposed(false);

        jobApplyService.saveOrUpdate(entry);

        Job job = jobService.findById(entry.getJobId());

        //job 不为 null 并且设置了 有新申请 通知之后发送邮件
        if(job != null && job.getWithNotify() && job.getWithNotify() != null){
            sendEmail(job);
        }

        renderOkJson();

    }


    //发送邮件
    public void sendEmail(@NotNull Job job){

        SimpleEmailSender ses = new SimpleEmailSender();

        if(!ses.isEnable()){
            Ret.fail().set("message", "您未开启邮件功能，无法发送。");
            return;
        }

        if (!ses.isConfigOk()) {
            Ret.fail().set("message", "未配置正确，smtp 或 用户名 或 密码 为空。");
            return;
        }

        Email email = Email.create();
        email.content(job.getNotifyContent());
        email.subject(job.getNotifyTitle());
        email.to(job.getNotifyEmails());

        ses.send(email);

        Ret.ok();
    }

}
