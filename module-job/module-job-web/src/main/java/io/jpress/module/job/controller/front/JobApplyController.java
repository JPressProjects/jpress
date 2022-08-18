package io.jpress.module.job.controller.front;


import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.FileUtil;
import io.jboot.web.attachment.AttachmentManager;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.Regex;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.SimpleEmailSender;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobApply;
import io.jpress.module.job.service.JobApplyService;
import io.jpress.module.job.service.JobService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;

import javax.validation.constraints.NotNull;
import java.util.List;


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

        if (id == null || job == null) {
            renderError(404);
            return;
        }

        //如果招聘的全局设置中 设置了 不允许投递 则404
        if (isApplyEnable != null && !isApplyEnable) {
            renderError(404);
            return;
        }

        //如果该岗位 设定了不予许 投递 那么 404
        if (job.getWithApply() != null && !job.getWithApply()) {
            renderError(404);
            return;
        }

        setAttr("job", job);
        render("job_apply.html");
    }


    public void postApply() {

        //文件上传 接收
        List<UploadFile> uploadFiles = getFiles();

        if (uploadFiles == null || uploadFiles.size() == 0) {
            renderFailJson("请上传简历或者附件");
            return;
        }

        JobApply apply = getModel(JobApply.class, "jobApply");

        //如果提交为空 404
        if (apply == null || apply.size() == 0) {
            deleteFiles(uploadFiles);
            renderError(404);
            return;
        }




        //获取岗位信息
        Job job = jobService.findById(apply.getJobId());
        //获取 招聘设置中  是否允许简历投递
        Boolean isApplyEnable = optionService.findAsBoolByKey(JobApply.JOB_APPLY_ENABLE);
        //如果此岗位已经关闭 在线投递 那么 404 如果招聘设置中 简历投递被关闭 那么 404
        if (job == null || (job.getWithApply() != null && !job.getWithApply()) || (isApplyEnable != null && !isApplyEnable)) {
            deleteFiles(uploadFiles);
            renderError(404);
            return;
        }


        //填写必填信息
        if (apply.getUserName() == null || apply.getWorkYears() == null || apply.getEducation() == null) {
            deleteFiles(uploadFiles);
            renderFailJson("请填写重要信息");
            return;
        }

        //手机号和邮箱填写判断
        if (apply.getMobile() == null || apply.getEmail() == null) {
            deleteFiles(uploadFiles);
            renderFailJson("请填写手机号或者邮箱");
            return;
        }

        //手机验证
        boolean matchesByMobile = apply.getMobile().matches(Regex.MOBILE);

        if (!matchesByMobile) {
            deleteFiles(uploadFiles);
            renderFailJson("请正确输入手机号");
            return;
        }


        //邮箱验证
        boolean matchesByEmail = apply.getEmail().matches(Regex.EMAIL);

        if (!matchesByEmail) {
            deleteFiles(uploadFiles);
            renderFailJson("邮箱不正确");
            return;
        }



        //简历上传
        for (UploadFile uploadFile : uploadFiles) {
            String fileParameterName = uploadFile.getParameterName();
            //如果是简历
            if (fileParameterName.equals(JobApply.FILE_RESUME)) {
                String path = AttachmentManager.me().saveFile(uploadFile.getFile());
                apply.setCvPath(path);
            }
            //如果是附件
            else if (fileParameterName.equals(JobApply.FILE_ATTACHMENT)) {
                String path = AttachmentManager.me().saveFile(uploadFile.getFile());
                apply.setAttachment(path);
            }
            //如果都不是
            else {
                FileUtil.delete(uploadFile.getFile());
            }
        }

        apply.remove("id");
        apply.setWithViewed(false);
        apply.setWithDisposed(false);

        jobApplyService.save(apply);


        //job 如果设置了 有新申请 通知之后发送邮件
        if (job.getWithNotify() && job.getWithNotify() != null) {
            sendEmail(job);
        }

        renderOkJson();

    }

    private void deleteFiles(List<UploadFile> files){
        if (files != null && !files.isEmpty()){
            files.forEach(uploadFile -> FileUtil.delete(uploadFile.getFile()));
        }
    }


    //发送邮件
    private void sendEmail(@NotNull Job job) {

        SimpleEmailSender ses = new SimpleEmailSender();

        if (!ses.isEnable()) {
            return;
        }

        if (!ses.isConfigOk()) {
            LogKit.error("未配置正确，smtp 或 用户名 或 密码 为空。");
            return;
        }

        Email email = Email.create();
        email.content(job.getNotifyContent());
        email.subject(job.getNotifyTitle());
        email.to(job.getNotifyEmails());

        ses.send(email);
    }

}
