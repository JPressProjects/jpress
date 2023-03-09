//参考：
// https://github.com/vuejs/vuepress/blob/master/packages/docs/docs/.vuepress/config.js
// https://vuepress-theme-reco.recoluan.com/views/1.x/
module.exports = {
    title: 'JPress 官方文档',
    description: 'JPress，一个专业的建站神器，已有超过10万+网站使用 JPress 搭建',
    // base:'/docs/',
    markdown: {
        lineNumbers: true
    },
    theme: 'vuepress-theme-reco',
    themeConfig: {
        //腾讯 404 公益配置
        noFoundPageByTencent: false,

        mode: 'light', // 默认 auto，auto 跟随系统，dark 暗色模式，light 亮色模式
        modePicker: false, // 默认 true，false 不显示模式调节按钮，true 则显示

        // author
        //author: 'jpress',
        subSidebar: 'auto',//在所有页面中启用自动生成子侧边栏，原 sidebar 仍然兼容

        // if your docs are in a different repo from your main project:
        docsRepo: 'JPressProjects/jpress',
        // if your docs are in a specific branch (defaults to 'master'):
        docsBranch: 'master',
        // if your docs are not at the root of the repo:
        docsDir: 'doc',
        // defaults to false, set to true to enable
        editLinks: true,
        // custom text for edit link. Defaults to "Edit this page"
        editLinkText: '编辑此页面',

        /**
         * support for
         * 'default'
         * 'funky'
         * 'okaidia'
         * 'solarizedlight'
         * 'tomorrow'
         */
        codeTheme: 'tomorrow',



        lastUpdated: '更新时间', // string | boolean

        nav: [
            {text: '首页', link: '/'},
            {text: '用户手册', link: '/manual/'},
            {text: '开发文档', link: '/development/'},
            {text: '提问', link: 'https://gitee.com/JPressProjects/jpress/issues'},
            {
                text: '源码下载', items: [
                    {text: 'Gitee', link: 'https://gitee.com/JPressProjects/jpress'},
                    {text: 'Github', link: 'https://github.com/JPressProjects/jpress'}
                ]
            }
        ],

        sidebar: {

            '/manual/': [{
                    title: '快速入门',
                    collapsable: false,
                    children: [
                        {title: 'JPress 简介', path: '/manual/'},
                        {title: '快速开始', path: '/manual/start'}
                    ],
                },
                {
                    title: '安装启动',
                    collapsable: false,
                    children: [
                        {title: 'JAVA环境配置', path: '/manual/jdk_config'},
                        {title: 'Maven环境配置', path: '/manual/maven_config'},
                        {title: 'IDEA开发环境准备', path: '/manual/idea_environment_config'},
                        {title: 'JPress源码获取', path: '/manual/jpress_download_source'},
                        {title: '导入JPress源码到编辑器', path: '/manual/jpress_open_compiler'},
                        {title: '编译并运行JPress', path: '/manual/jpress_compile_with_run'},
                        {title: '在Windows上安装JPress(tomcat部署)', path: '/manual/windows-tomcat-deploy'},
                        {title: '在Windows上安装JPress(undertow部署)', path: '/manual/windows_undertow_deploy'},
                        {title: '在Linux上安装JPress(tomcat部署)', path: '/manual/linux-tomcat-deploy'},
                        {title: '在Linux上安装JPress(undertow部署)', path: '/manual/linux_undertow_deploy'},
                        {title: '在宝塔上安装JPress', path: '/manual/ces_bt_config'},
                        {title: '在 Docker 上安装JPress', path: '/manual/install_docker'},
                        {title: '一键安装', path: '/manual/install_oneclick'},
                        {title: '如何迁移', path: '/manual/project_transfer'},
                        {title: '如何升级', path: '/manual/project_upgrade'}
                    ],
                },
                {
                    title: '产品手册',
                    collapsable: false,
                    children: [
                        {title: '文章', path: '/manual/article'},
                        {title: '产品', path: '/manual/product'},
                        {title: '招聘', path: '/manual/job'},
                        {title: '页面', path: '/manual/page'},
                        {title: '附件', path: '/manual/attachment'},
                        {title: '模板', path: '/manual/template'},
                        {title: '插件', path: '/manual/addon'},
                        {title: '用户', path: '/manual/user'},
                        {title: '微信', path: '/manual/wechat'},
                        {title: '设置相关', path: '/manual/setting'},
                        {title: '小工具箱', path: '/manual/kits'},
                        {title: '常见问题', path: '/manual/faq'},
                    ],
                }
            ],


            '/development/': [{
                    title: '概述',
                    path: '/development/',
                },
                {
                    title: '模板开发',
                    collapsable: true,
                    children: [
                        {title: '模板简介', path: '/development/template/template_introduce'},
                        {title: '目录结构', path: '/development/template/template_directory'},
                        {title: '模板语法', path: '/development/template/template_grammar'},
                        {title: '全局变量', path: '/development/template/template_global_variable'},
                        {title: '共享方法', path: '/development/template/template_global_method'},
                        {title: '模板指令', path: '/development/template/template_command'},
                        {title: '拖拽设计', path: '/development/template/template_block'},
                        {title: '设置页面', path: '/development/template/template_setting'},

                    ],
                },
                {
                    title: '插件开发',
                    collapsable: true,
                    children: [
                        {title: '插件简介', path: '/development/addon/start'},
                        {title: 'Hello World', path: '/development/addon/helloworld'},
                        {title: '插件安装', path: '/development/addon/install'},
                        {title: '插件升级', path: '/development/addon/upgrade'},
                        {title: '插件资源', path: '/development/addon/resource'},
                        {title: '插件代码生成器', path: '/development/addon/codegen'},
                        {title: '微信插件', path: '/development/addon/wechat'},
                        {title: '常见问题', path: '/development/addon/faq'},
                    ],
                },
                {
                    title: '模块开发',
                    collapsable: true,
                    children: [
                        {title: 'JPress源码获取', path: '/development/jpress_download_source'},
                        {title: 'JAVA环境配置', path: '/development/jdk_config'},
                        {title: 'Maven环境配置', path: '/development/maven_config'},
                        {title: 'IDEA开发环境准备', path: '/development/idea_environment_config'},
                        {title: '导入JPress源码到编辑器', path: '/development/jpress_open_compiler'},
                        {title: '编译并运行JPress', path: '/development/jpress_compile_with_run'},
                        {title: '开始前注意事项', path: '/development/module/note'},
                        {title: '表设计', path: '/development/module/table_design'},
                        {title: '模块代码生成器的使用', path: '/development/module/moduleGenerator'},
                        {title: '模块导入编辑器', path: '/development/module/module_import_compiler'},
                        {title: '后台菜单配置', path: '/development/module/admin_menu_setting'},
                        {title: '面板配置', path: '/development/module/panel_setting'},
                        {title: '工具箱配置', path: '/development/module/toolkit'},
                        {title: 'JPress架构', path: '/development/module/structure'},
                    ],
                },
                {
                    title: 'API 接口',
                    collapsable: true,
                    children: [
                        // {title: '概述', path: '/development/api/start'},
                        {title: '文章', path: '/development/api/api_article'},
                        {title: '文章分类', path: '/development/api/api_article_category'},
                        {title: '文章评论', path: '/development/api/api_article_comment'},
                        {title: '产品', path: '/development/api/api_product'},
                        {title: '产品分类', path: '/development/api/api_product_category'},
                        {title: '产品评论', path: '/development/api/api_product_comment'},
                        {title: '页面', path: '/development/api/api_page'},
                        {title: '用户', path: '/development/api/api_user'},
                        {title: '微信小程序', path: '/development/api/api_wechat_mp'},
                        {title: '系统设置', path: '/development/api/api_option'},
                    ],
                },
            ]
        },
        sidebarDepth: 1
    },

    head: [
        ['link', {rel: 'icon', href: '/logo.png'}],
        ['script', {}, `
            var _hmt = _hmt || [];
            (function() {
              var hm = document.createElement("script");
              hm.src = "https://hm.baidu.com/hm.js?13a39a1b1e7fb17e8f806d1fb6207796";
              var s = document.getElementsByTagName("script")[0]; 
              s.parentNode.insertBefore(hm, s);
            })();
        `],
    ]
}
