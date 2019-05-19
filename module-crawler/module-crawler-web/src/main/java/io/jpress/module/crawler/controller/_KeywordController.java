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
package io.jpress.module.crawler.controller;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.exception.JbootException;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.crawler.model.Keyword;
import io.jpress.module.crawler.model.KeywordCategory;
import io.jpress.module.crawler.service.KeywordCategoryService;
import io.jpress.module.crawler.service.KeywordService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RequestMapping(value = "/admin/crawler/keyword", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _KeywordController extends AdminControllerBase {

    private static final Log _LOG = Log.getLog(_KeywordController.class);
    private static final String TXT_SUFFIX = ".txt";

    @Inject
    private KeywordService service;
    @Inject
    private KeywordCategoryService categoryService;

    @AdminMenu(text = "关键词", groupId = "crawler", order = 4)
    public void index() {

        List<KeywordCategory> list = categoryService.findAll();
        set("categoryList", list);

        render("crawler/keyword_list.html");
    }

    public void paginate() {

        String inputKeywords = getInputKeyword();
        String categoryIds = getPara("categoryIds");

        /** 随机导出关键词数量 */
        Integer minNum = getParaToInt("minNum");
        Integer maxNum = getParaToInt("maxNum");

        /** 关键词最小长度与最大长度 */
        Integer minLength = getParaToInt("minLength");
        Integer maxLength = getParaToInt("maxLength");

        /** 在搜索引擎中有效 */
        String searchTypes = getPara("searchTypes");
        /** 在搜索引擎是否检查 */
        String disableSearchTypes = getPara("disableSearchTypes");

        /** 选中的关键词 */
        String selectedKeywords = getPara("selectedKeywords");
        String orderBy = getPara("orderBy");

        Page<Keyword> page = service.paginate(getPagePara(), getPageSizePara(), inputKeywords, categoryIds, searchTypes,
                disableSearchTypes, minLength, maxLength, minNum, maxNum, orderBy);
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
    }

    public void edit() {
        int id = getParaToInt(0, 0);
        Keyword keyword = id > 0 ? service.findById(id) : new Keyword();
        setAttr("keyword", keyword);
        List<KeywordCategory> list = categoryService.findAll();

        set("categoryList", list);
        render("crawler/keyword_edit.html");
    }
   
    public void doSave() {
        Keyword entry = getModel(Keyword.class,"keyword");

        service.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }

    public void doDel() {
        Long id = getIdPara();
        renderJson(service.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    public void doBatchDel() {
        String ids = getPara("ids");
        Set<String> set = StrUtil.splitToSet(ids, ",");
        renderJson(service.deleteByIds(set.toArray()) ? Ret.ok() : Ret.fail());
    }

    public void upload() {
        List<KeywordCategory> list = categoryService.findAll();
        set("categoryList", list);
        render("crawler/keyword_import.html");
    }

    /**
     * 导出分为两种情况
     *  1. 勾选关键词导出
     *  2. 按条件查询导出
     *
     * @date  2019-05-17 16:23
     * @return void
     */
    public void export() {

        String orderBy = getPara("orderBy");
        String categoryIds = getPara("categoryIds");
        /** 随机导出关键词数量 */
        Integer minNum = getParaToInt("minNum");
        Integer maxNum = getParaToInt("maxNum");

        /** 关键词最小长度与最大长度 */
        Integer minLength = getParaToInt("minLength");
        Integer maxLength = getParaToInt("maxLength");

        /** 在搜索引擎中有效 */
        String validSearchTypes = getPara("validSearchTypes");
        /** 在搜索引擎是否检查 */
        String checkedSearchTypes = getPara("checkedSearchTypes");

        /** 选中的关键词 */
        String selectedKeywords = getPara("selectedKeywords");

        String inputKeywords = getInputKeyword();
        String title = StrKit.isBlank(inputKeywords) ? "关键词" : inputKeywords.replaceAll("\\|", "-");
        String txtFilePath = PathKit.getWebRootPath() + JFinal.me().getConstants().getBaseUploadPath() + "/keyword.txt";
        File txtFile = new File(txtFilePath);

        try {
            if (txtFile.exists()) {
                txtFile.delete();
            }
            File pfile = txtFile.getParentFile();
            if (!pfile.exists()) {
                pfile.mkdirs();
            }
            txtFile.createNewFile();
        } catch (IOException e) {
            _LOG.error("创建文件出错", e);
            renderError(500);
        }

        if (StrKit.notBlank(selectedKeywords)) {
            List<String> list = Splitter.on(",").trimResults().splitToList(selectedKeywords);
            exportToTxt(txtFilePath, null, list);
        } else {
            // 按条件导出关键词
            List<String> list = service.findListByParams(inputKeywords, categoryIds, validSearchTypes,
                    checkedSearchTypes, minLength, maxLength, minNum, maxNum, orderBy);
            exportToTxt(txtFilePath, list, null);
        }
        renderFile(txtFile, title + ".txt");
    }

    /**
     * 导出全部关键词
     *
     * @date  2019-05-17 16:23
     * @return void
     */
    public void exportAll() {

        // 创建导出文件存放目录目录
        String outputPath = PathKit.getWebRootPath() + "/export";
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        int pageNum = 1;
        Page<Keyword> page = service.paginate(pageNum, 1000);
        int totalPage = page.getTotalPage();

        for (int i = 0; i < totalPage; i++) {
            String txtFilePath = outputPath + "/关键词-000" + i + ".txt";
            List<String> list = service.findListByPage(i, 1000);
            exportToTxt(txtFilePath, list, null);
        }

        // 导出的关键词进行打包
        String zipPath = PathKit.getWebRootPath() + JFinal.me().getConstants().getBaseUploadPath() + "/keyword.zip";
        ZipUtil.zip(outputPath, zipPath);
        File zipFile = new File(zipPath);

        if (zipFile.exists()) {
            renderFile(zipFile, "所有关键词.zip");
        } else {
            renderError(500);
        }
    }

    /**
     * 导出关键词到TXT
     * @date  2019-05-17 00:28
     * @param filePath          文件路径
     * @param selectedList        按条件搜索关键词
     * @param inputKeywordList  输入关键词
     * @return void
     */
    private void exportToTxt(String filePath, List<String> selectedList, List<String> inputKeywordList) {
        File file = new File(filePath);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, true);
            if (selectedList != null) {
                for (String keyword : selectedList) {
                    fileWriter.write(keyword + "\r\n");
                }
            }

            if (inputKeywordList != null) {
                for (String keyword : inputKeywordList) {
                    fileWriter.write(keyword + "\r\n");
                }
            }
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                    fileWriter = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doImport() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderJson(Ret.fail().set("message", "请选择要上传的文件"));
            return;
        }

        File file = uploadFile.getFile();
        if (!AttachmentUtils.isSafe(file)){
            file.delete();
            renderJson(Ret.fail().set("message", "不支持此类文件上传"));
            return;
        }

        Integer categoryId = getParaToInt("categoryId");
        String suffix = FileUtil.getSuffix(file.getName());
        String fileName = uploadFile.getOriginalFileName();

        if (TXT_SUFFIX.equals(suffix) || TXT_SUFFIX.toUpperCase().equals(suffix)) {
            uploadTxt(file, fileName, suffix, categoryId);
        } else {
            uploadZip(file);
        }

        try {
            if (file.exists() && !file.delete()) {
                _LOG.error(this.getClass().getName() + "删除TXT文件或压缩包失败!");
            }
        } catch (Exception e) {
            _LOG.error(this.getClass().getName() + "删除TXT文件或压缩包失败!");
        }
        renderOkJson();
    }

    private void uploadTxt(File file, String fileName, String suffix, Integer categoryId) {
        long startTime = System.currentTimeMillis();
        FileReader fileReader = new FileReader(file, "UTF-8");
        List<String> keywordList = fileReader.readLines();

        if (keywordList != null && keywordList.size() > 0) {
            String name = fileName.replace(suffix, "");
            if (categoryId != null) {
                KeywordCategory category = categoryService.findById(categoryId);
                name = category.getName();
            }
            service.batchSave(keywordList, categoryId, name);
        }

        long endTime = System.currentTimeMillis();
        _LOG.info("上传文件 " + fileName + " 总耗时：" + (endTime - startTime) + "毫秒");
    }

    private void uploadZip(File file) {

        boolean isCreated = true;
        String fileName = file.getName();
        String suffix = FileUtil.getSuffix(fileName);
        String outputPath = PathKit.getWebRootPath() + "/" + JFinal.me().getConstants().getBaseUploadPath() + "/" + fileName.replace(suffix, "");

        File outputFile = new File(outputPath);

        try {
            if (!outputFile.exists()) {
                isCreated = outputFile.mkdirs();
            }

            if (!isCreated) {
                _LOG.error(this.getClass().getName() + "解压文件夹错误!");
                if (isAjaxRequest()) {
                    renderJson(Ret.fail().set("msg", "创建解压文件夹错误"));
                } else {
                    new JbootException("解压文件夹错误");
                }
                return;
            }

            long startTime = System.currentTimeMillis();
            List<File> files = Lists.newArrayList();
            List<String> categoryList = Lists.newArrayList();
            ZipUtil.unzip(file, outputFile, CharsetUtil.CHARSET_GBK);

            recursive(outputFile, files);
            List<Map<String, List<String>>> keywordsList = Lists.newArrayList();
            files.stream().forEach(tmpFile -> {

                Map<String, List<String>> kMap = Maps.newHashMap();
                String tmpFileName = tmpFile.getName();
                String tmpSuffix = FileUtil.getSuffix(tmpFileName);
                String typeName = tmpFileName.replaceAll(tmpSuffix, "");

                categoryList.add(typeName);
                FileReader fileReader = new FileReader(tmpFile, CharsetUtil.CHARSET_UTF_8);
                List<String> keywordList = fileReader.readLines();
                keywordList.stream().distinct().collect(Collectors.toList());

                if (keywordList != null && keywordList.size() > 0) {
                    kMap.put(typeName, keywordList);
                    keywordsList.add(kMap);
                }
            });

            categoryList.stream().distinct().collect(Collectors.toList());
            service.batchSave(keywordsList, categoryList);
            long endTime = System.currentTimeMillis();
            _LOG.info("批量导入关键词耗时：" + (endTime - startTime) + "毫秒");

            if (outputFile.isDirectory()) {
                File[] tmpFiles = outputFile.listFiles();
                Arrays.stream(tmpFiles).forEach(tmpFile -> {
                    FileUtils.deleteQuietly(tmpFile);
                });
            }
        } catch (Exception e) {
            FileUtils.deleteQuietly(file);
            _LOG.error(this.getClass().getName() + "解压文件夹错误!", e);
            if (isAjaxRequest()) {
                renderJson(Ret.fail().set("msg", "创建解压文件夹错误"));
            } else {
                new JbootException("解压Zip压缩文件错误", e);
            }
            return;
        }
    }

    /**
     * 递归遍历文件夹中的所有文件
     * @param file      文件夹
     * @param list      文件列表
     * @return void
     */
    private void recursive (File file, List<File> list) {
        File[] files = file.listFiles();
        if (file != null) {
            _LOG.info("压缩包中文件总个数 = " + files.length);
            Arrays.stream(files).forEach(tmp -> {
                if (tmp.isDirectory()) {
                    recursive(tmp, list);
                } else {
                    list.add(tmp);
                }
            });
        }
    }

    /** 用户输入的关键词 */
    private String getInputKeyword() {

        String inputKeywords = getPara("inputKeywords", "");
        List<String> list = Splitter.on(",").splitToList(inputKeywords);
        StringBuilder keywordsText = new StringBuilder();

        for (String keyword : list) {
            if (StrKit.notBlank(keyword)) {
                keywordsText.append(keyword).append("|");
            }
        }

        String keywords = null;
        int length = keywordsText.length();
        if (length > 0) {
            keywords = keywordsText.toString().substring(0, keywordsText.length() - 1);
        }
        return keywords;
    }
}