package io.jpress.module.job.controller.front;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import com.jfinal.template.source.FileSource;
import com.jfinal.upload.UploadFile;
import io.jboot.web.attachment.AttachmentManager;
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

        if (id == null || job == null) {
            renderError(404);
            return;
        }

        setAttr("job", job);


        render("job_apply.html");
    }


    public void uploadFile() {

        UploadFile file = getFile("file");

        if (file == null) {
            renderFailJson("文件上传失败，请重新上传");
            return;
        }

        Map<String, Object> map = new HashMap<>();

        String path = AttachmentManager.me().saveFile(file.getFile());
        map.put("state", true);
        map.put("filePath", path);
        map.put("fileName", file.getOriginalFileName());

        renderJson(map);
    }


    public void doSave() {

        JobApply entry = getModel(JobApply.class, "jobApply");

        Long jobId = entry.getJobId();


        String mobileCode = getPara("mobileCode");
        String emailCode = getPara("emailCode");

        if (entry == null) {
            renderError(404);
            return;
        }

        if (entry.getMobile() == null || entry.getJobId() == null || entry.getUserName() == null) {
            renderFailJson("请填写重要信息");
            return;
        }

        //确认是否开启了手机验证和邮箱验证
        Boolean mobileEnable = optionService.findAsBoolByKey(JobApply.MOBILE_ENABLE);
        Boolean emailEnable = optionService.findAsBoolByKey(JobApply.EMAIL_ENABLE);

        //如果开启了手机验证 对验证码进行验证
        if (mobileEnable) {

            boolean mobile = validationMobile(entry.getMobile(), mobileCode);
            if (!mobile) {
                renderFailJson("手机验证码不正确");
                return;
            }

        }

        //如果开启了邮箱验证 对邮箱进行验证
        if (emailEnable) {

            boolean email = validationEmail(entry.getEmail(), emailCode);
            if (!email) {
                renderFailJson("邮箱验证码不正确");
                return;
            }

        }


        //通过job_id  mobile 来确定是否已经 申请
        JobApply jobApply = jobApplyService.findFirstByJobIdWhitMobile(entry.getJobId(), entry.getMobile());
        if (jobApply != null) {
            renderFailJson("您已经申请过了,请勿重复申请");
            return;
        }

        entry.setWithViewed(false);
        entry.setWithDisposed(false);

        jobApplyService.saveOrUpdate(entry);

        Job job = jobService.findById(entry.getJobId());

        //job 不为 null 并且设置了 有新申请 通知之后发送邮件
        if (job != null && job.getWithNotify() && job.getWithNotify() != null) {
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

    //获取手机号验证码
    public void getMobileCode(@JsonBody("mobile") String mobile) {

        Map<String, Object> map = new HashMap<>();

        if (mobile == null) {
            map.put("state", false);
            map.put("message", "短信发送失败,请核对手机号码");
            renderJson(map);
            return;
        }

        //验证手机号 是否正确
        boolean matches = mobile.matches(Regex.MOBILE);

        if (!matches) {
            map.put("state", false);
            map.put("message", "短信发送失败,请核对手机号码");
            renderJson(map);
            return;
        }

        //获取模板和签名
        String template = optionService.findByKey(JobApply.CONNECTION_SMS_TEMPLATE);
        String sign = optionService.findByKey(JobApply.CONNECTION_SMS_SIGN);

        //发送短信
        if (template != null || sign != null) {

            boolean sendCode = SmsKit.sendCode(mobile, SmsKit.generateCode(), template, sign);

            //如果短信发送成功
            if (sendCode) {
                map.put("state", true);
                map.put("message", "短信发送成功");
            }

        }

        renderJson(map);
        //TODO
    }

    //手机验证码验证
    public boolean validationMobile(String mobile, String code) {

        if (code == null || mobile == null) {
            return false;
        }

        return SmsKit.validateCode(mobile, code);

    }

    //获取邮箱验证码
    public void getEmailCode(@JsonBody("email") String email,@JsonBody CaptchaVO captchaVO) {

        Long id = getLong();

        Job job = jobService.findById(id);

        //进行前端滑块 参数验证
        ResponseModel validResult = captchaService.verification(captchaVO);

        Map<String, Object> map = new HashMap<>();

        if (validResult == null || !validResult.isSuccess()) {
            map.put("state", false);
            map.put("message", "邮件发送失败,请核对邮箱");
            renderJson(map);
            return;
        }

        if (email == null || job == null) {
            map.put("state", false);
            map.put("message", "邮件发送失败,请核对邮箱");
            renderJson(map);
            return;
        }

        //验证邮箱 是否正确
        boolean matches = email.matches(Regex.EMAIL);
        if (!matches) {
            map.put("state", false);
            map.put("message", "邮件发送失败,请核对邮箱");
            renderJson(map);
            return;
        }
        //生成验证码
        String code = EmailKit.generateCode();

        //获取 发送邮件的 html 模板路劲
        File absolutePathFile = TemplateManager.me().getCurrentTemplate().getAbsolutePathFile();
        //获取渲染器对象
        Engine engine = RenderManager.me().getEngine();
        //获取到 html 文件
        FileSource fileSource = new FileSource(absolutePathFile.getAbsolutePath(), "jobemail.html");
        //将code 放入map中
        HashMap<String,Object> htmlMap = new HashMap<>();
        htmlMap.put("code", code);
        //将html文件渲染为 为 string 格式 并传入参数
        String renderResult = engine.getTemplate(fileSource).renderToString(htmlMap);

        Email sendEmail = Email.create();
        sendEmail.subject("邮件验证码");
        sendEmail.content(renderResult);
        sendEmail.to(email);

        boolean sendEmailCode = EmailKit.sendEmailCode(email, code, sendEmail);

        if (sendEmailCode) {
            map.put("state", true);
            map.put("message", "邮件发送成功");
        }

        renderJson(map);

    }

    //邮箱验证
    public boolean validationEmail(String email, String code)  {

        if (code == null || email == null) {
            return false;
        }

        return EmailKit.validateCode(email, code);

    }

}
