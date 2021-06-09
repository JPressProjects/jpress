//参考：
// https://github.com/vuejs/vuepress/blob/master/packages/docs/docs/.vuepress/config.js
// https://vuepress-theme-reco.recoluan.com/views/1.x/
module.exports = {
    title: 'JPress 官方文档',
    description: 'JPress，一个专业的建站神器，已有超过10万+网站使用 JPress 搭建',
    // base:'/docs/',

    theme: 'vuepress-theme-reco',
    themeConfig: {
        //腾讯 404 公益配置
        noFoundPageByTencent: false,

        mode: 'light', // 默认 auto，auto 跟随系统，dark 暗色模式，light 亮色模式
        modePicker: false, // 默认 true，false 不显示模式调节按钮，true 则显示

        // author
        author: 'jpress',

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
            },
        ],

        sidebar: {
            '/manual/': [{
                    title: '快速入门',
                    collapsable: false,
                    children: [
                        {title: 'Jboot 简介', path: '/docs/'},
                        {title: '快速开始', path: '/docs/start'}
                    ],
                },
                {
                    title: '安装启动',
                    collapsable: false,
                    children: [
                        {title: '性能测试', path: '/docs/benchmark'},
                    ],
                },
                {
                    title: '产品手册',
                    collapsable: false,
                    children: [
                        {title: '性能测试', path: '/docs/benchmark'},
                    ],
                }
            ],


            '/development/': [{
                title: '模板开发',
                collapsable: false,
                children: [
                    {title: '简介', path: '/jbootadmin/'},
                    {title: '功能介绍', path: '/jbootadmin/feature'},
                    {title: '我要购买', path: '/jbootadmin/buy'}
                ],
            },
                {
                    title: '二次开发',
                    collapsable: false,
                    children: [
                        {title: '开始', path: '/jbootadmin/start'},
                        {title: '数据库设计', path: '/jbootadmin/db'},
                        {title: '后台菜单', path: '/jbootadmin/menu'},
                        {title: '权限设计', path: '/jbootadmin/permission'},
                        {title: '前端组件', path: '/jbootadmin/front'},
                        {title: '安全防护', path: '/jbootadmin/safety_precautions'},
                    ],
                },
                {
                    title: '微信开发',
                    collapsable: false,
                    children: [
                        {title: '部署', path: '/jbootadmin/deploy'},
                        {title: 'CDN配置', path: '/jbootadmin/cdn'},
                        {title: '文件同步', path: '/jbootadmin/attachment'},
                        {title: '配置中心', path: '/jbootadmin/config'},
                        {title: '门户网关', path: '/jbootadmin/gateway'},
                        {title: '服务器管理', path: '/jbootadmin/server'},
                    ],
                },
                {
                    title: 'API接口',
                    collapsable: false,
                    children: [
                        {title: '开始', path: '/jbootadmin/start'},
                        {title: '数据库设计', path: '/jbootadmin/db'},
                        {title: '后台菜单', path: '/jbootadmin/menu'},
                        {title: '权限设计', path: '/jbootadmin/permission'},
                        {title: '前端组件', path: '/jbootadmin/front'},
                        {title: '安全防护', path: '/jbootadmin/safety_precautions'},
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
        `]
    ]
}
