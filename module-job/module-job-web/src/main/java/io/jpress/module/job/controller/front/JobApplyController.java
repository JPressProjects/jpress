package io.jpress.module.job.controller.front;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import com.jfinal.template.source.FileSource;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jboot.web.validate.Regex;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.EmailKit;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.commons.sms.SmsKit;
import io.jpress.core.template.TemplateManager;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobApply;
import io.jpress.module.job.service.JobApplyService;
import io.jpress.module.job.service.JobService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/job/apply")
public class JobApplyController extends TemplateControllerBase {

    @Inject
    private JobService jobService;

    @Inject
    private JobApplyService jobApplyService;

    @Inject
    private OptionService optionService;

    @Inject
    private CaptchaService captchaService;

    public void index() {

        Long id = getLong();

        Job job = jobService.findByIdWithInfo(id);

        //获取 招聘设置中  是否允许简历投递
        Boolean isApplyEnable = optionService.findAsBoolByKey(JobApply.JOB_APPLY_ENABLE);

        if (id == null || job == null || !isApplyEnable || !job.getWithApply()) {
            renderError(404);
            return;
        }

        setAttr("job", job);


        render("job_apply.html");
    }


//    public void uploadFile() {
//
//        UploadFile file = getFile();
//
//        if (file == null) {
//            renderFailJson("文件上传失败，请重新上传");
//            return;
//        }
//
//        Map<String, Object> map = new HashMap<>();
//
//        String path = AttachmentManager.me().saveFile(file.getFile());
//        map.put("state", true);
//        map.put("filePath", path);
//        map.put("fileName", file.getOriginalFileName());
//
//        renderJson(map);
//    }


    public void doSave() {

        JobApply entry = getModel(JobApply.class, "jobApply");

        //如果提交为空 404
        if (entry == null) {
            renderError(404);
            return;
        }

        //获取岗位信息
        Job job = jobService.findById(entry.getJobId());
        //获取 招聘设置中  是否允许简历投递
        Boolean isApplyEnable = optionService.findAsBoolByKey(JobApply.JOB_APPLY_ENABLE);
        //如果此岗位已经关闭 在线投递 那么 404 如果招聘设置中 简历投递被关闭 那么 404
        if (job == null || !job.getWithApply() || !isApplyEnable) {
            renderError(404);
            return;
        }


        //填写必填信息
        if (entry.getMobile() == null || entry.getJobId() == null || entry.getUserName() == null) {
            renderFailJson("请填写重要信息");
            return;
        }

        //确认是否开启了手机验证和邮箱验证
        Boolean mobileEnable = optionService.findAsBoolByKey(JobApply.MOBILE_ENABLE);
        Boolean emailEnable = optionService.findAsBoolByKey(JobApply.EMAIL_ENABLE);


        //如果开启了手机验证 对验证码进行验证
        if (mobileEnable) {

            boolean matches = entry.getMobile().matches(Regex.MOBILE);

            if (!matches) {
                renderFailJson("请正确输入手机号");
                return;
            }

        }

        //如果开启了邮箱验证 对邮箱进行验证
        if (emailEnable) {

            boolean matches = entry.getEmail().matches(Regex.EMAIL);

            if (!matches) {
                renderFailJson("邮箱不正确");
                return;
            }

        }


        //文件上传 接收
        List<UploadFile> files = getFiles("file");



        //通过job_id  mobile 来确定是否已经 申请
        JobApply jobApply = jobApplyService.findFirstByJobIdWhitMobile(entry.getJobId(), entry.getMobile());
        if (jobApply != null) {
            renderFailJson("您已经申请过了,请勿重复申请");
            return;
        }

        entry.setWithViewed(false);
        entry.setWithDisposed(false);

        jobApplyService.saveOrUpdate(entry);


        //job 如果设置了 有新申请 通知之后发送邮件
        if (job.getWithNotify() && job.getWithNotify() != null) {
            sendEmail(job);
        }

        renderOkJson();

    }


    //发送邮件
    public void sendEmail(@NotNull Job job) {

        SimpleEmailSender ses = new SimpleEmailSender();

        if (!ses.isEnable()) {
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
