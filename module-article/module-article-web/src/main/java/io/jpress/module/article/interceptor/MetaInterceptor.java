//package io.jpress.module.article.interceptor;
//
//
//import com.jfinal.aop.Inject;
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//import com.jfinal.core.Controller;
//import io.jboot.utils.StrUtil;
//import io.jpress.core.support.smartfield.SmartField;
//import io.jpress.module.article.model.Article;
//import io.jpress.module.article.model.ArticleMetaInfo;
//import io.jpress.module.article.model.ArticleMetaRecord;
//import io.jpress.module.article.service.ArticleMetaInfoService;
//import io.jpress.module.article.service.ArticleMetaRecordService;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MetaInterceptor implements Interceptor {
//
//    @Inject
//    private ArticleMetaInfoService metaInfoService;
//
//    @Inject
//    private ArticleMetaRecordService metaRecordService;
//
//    @Override
//    public void intercept(Invocation inv) {
//
//        inv.invoke();
//
//        if (inv.getActionKey().equals("/admin/article/write")) {
//            doReadMetaInfos(inv);
//        } else if (inv.getActionKey().equals("/admin/article/doWriteSave")) {
//            doSaveMetaInfos(inv);
//        }
//    }
//
//    private void doSaveMetaInfos(Invocation inv) {
//
//        Controller ctr = inv.getController();
//        List<ArticleMetaInfo> infs = metaInfoService.findAll();
//        if (infs == null || infs.size() == 0) {
//            return;
//        }
//        Article article = ctr.getAttr("article");
//        if (article == null || article.getId() == null) {
//            return;
//        }
//        List<ArticleMetaRecord> records = new ArrayList<>();
//        for (ArticleMetaInfo inf : infs) {
//
//            if (!inf.isEnable()) {
//                continue;
//            }
//
//            String key = ArticleMetaInfo.buildFieldName(inf);
//            String value = SmartField.TYPE_CHECKBOX.equals(inf.getType())
//                    ? getValue(ctr.getParaValues(key))
//                    : ctr.getPara(key);
//
//            ArticleMetaRecord record = new ArticleMetaRecord();
//            record.setArticleId(article.getId());
//            record.setFieldName(inf.getFieldName());
//            record.setValue(value);
//            records.add(record);
//        }
//
//        metaRecordService.batchSaveOrUpdate(records);
//    }
//
//    private static String getValue(String[] values) {
//        if (values == null) {
//            return null;
//        }
//        if (values.length == 1) {
//            return values[0];
//        }
//
//        int iMax = values.length - 1;
//        StringBuilder b = new StringBuilder();
//        for (int i = 0; ; i++) {
//            b.append(values[i]);
//            if (i == iMax) {
//                return b.toString();
//            }
//            b.append(",");
//        }
//    }
//
//    private void doReadMetaInfos(Invocation inv) {
//        List<ArticleMetaInfo> infs = metaInfoService.findAll();
//        String articleId = inv.getController().getPara();
//        if (StrUtil.isBlank(articleId)) return;
//
//        if (infs != null && infs.size() > 0) {
//            Map<String, String> values = new HashMap<>();
//            for (ArticleMetaInfo inf : infs) {
//                ArticleMetaRecord record = metaRecordService.findByArticleIdAndFieldName(articleId, inf.getFieldName());
//                if (record != null && StrUtil.isNotBlank(record.getValue())) {
//                    values.put(record.getFieldName(), record.getValue());
//                }
//            }
//
//            if (!values.isEmpty()) {
//                //必须保证 articleMeta 和 ArticleMetaInfo.toSmartField() 里的setName 一致
//                //具体原因在 SmartField.doGetDataFromControllerByName 的时候，自动去找到这个map
//                inv.getController().set("articleMeta", values);
//            }
//        }
//
//    }
//}
//
