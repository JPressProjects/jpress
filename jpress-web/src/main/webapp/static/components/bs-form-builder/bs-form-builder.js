/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
(function ($) {

    var modal = '<div class="modal">' +
        '  <div class="modal-dialog modal-lg">' +
        '    <div class="modal-content shadow-lg">' +
        '      <div class="modal-header">' +
        '        <h5 class="modal-title">{{title}}</h5>' +
        '        <button type="button" class="close" data-dismiss="modal" aria-label="Close" >' +
        '          <span aria-hidden="true">&times;</span>' +
        '        </button>' +
        '      </div>' +
        '      <div class="modal-body" style="word-break: break-all">' +
        '        <p>{{content}}</p>' +
        '      </div>' +
        '      <div class="modal-footer">' +
        '        <button type="button" class="btn btn-secondary" data-dismiss="modal"> 关闭 </button>' +
        '      </div>' +
        '    </div>' +
        '  </div>' +
        '</div>';

    //默认配置
    var defaultOptions = {
        mode: "builder", // 模式 builder 工具模式,  view 预览模式
        templateEngine: new Fasty(),
        bsFormContainerSelector: ".bsFormContainer", // 设计容器
        bsFormContainerFilterSelector: ".bsFormFilter", // 设计容器里，不允许拖动的组件 class
        bsFormContainerSortableGroup: "shared", // 配置主容器里的 group 名称
        bsFormContainerPlaceHolderSelector: ".bsFormContainer-placeholder", // 设计容器里的提示内容
        bsFormPropsSelector: ".bsFormProps", // 面板内容
        bsFormPropsTitleSelector: ".bsFormPropsTitle", // 面板标题
        bsFormPropsFilter: null, //function 自定义属性过滤器
        bsFormPropsItemAppendBefore: null, //监听 props html 内容被追加时，可以返回指定的 html 内容，替换本身的 html，亦或者返回空字符串
        bsFormPropsItemAppended: null, //监听 props html 内容被追加，可以通其设置 propsPanel 里的表单内容或者事件
        customBuilderStructure: false, // 自定义容器面板
        onDataChange: null, //数据更新的监听器
        onDataChanged: null, //数据更新的监听器
        renderEmptyDrags: null,//当左边的拖动按钮分类找不到任何组件时，调用该方法
        components: [], //初始化时自定义的组件
        componentIdPrefix: "",// "id_" 组件的 id 前缀
        useComponents: [], //使用的组件 use components
        unUseComponents: [], //不使用的组件
        customRender: null, //自定义渲染方法，支持后端 url，同步方法 和 异步方法
        optionsDatasources: null, // {group:array}
        actionButtons: [
            {
                text: '导出 JSON',
                mainClass: 'btn-primary',
                icon: 'bi bi-arrow-up pr-1',
                onclick: function (event, builder) {
                    var json = builder.exportToJson();
                    var html = builder._renderTemplate(modal, ["title", "content"], ["json内容", json]);

                    var $el = $(html);
                    $el.appendTo($('body')).show();
                    $el.find('.close,.btn').on('click', function () {
                        $el.remove();
                    })
                }
            },
            {
                text: '下载 HTML',
                mainClass: 'btn-primary',
                icon: 'bi bi-arrow-up pr-1',
                onclick: function (event, builder) {
                    var text = builder.exportToHtml();

                    var anchor = document.createElement('a');
                    anchor.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
                    anchor.setAttribute('download', "bsFormBuilder.html");
                    anchor.style.display = 'none';

                    document.body.appendChild(anchor);
                    anchor.click();
                    document.body.removeChild(anchor);
                }
            },
            {
                text: '获取源码',
                mainClass: 'btn-primary',
                icon: 'bi bi-eye pr-1',
                onclick: function () {
                    alert('这里的所有按钮，按钮功能都是可以自定义的~~~')
                    window.open("https://gitee.com/fuhai/bsFormBuilder")
                }
            },
            {
                text: '清空',
                mainClass: 'btn-danger',
                icon: 'bi bi-trash pr-1',
                onclick: function (event, builder) {
                    builder.clear();
                }
            },
        ],
        actionButtonTemplate: '<button type="button" class="btn btn-sm {{mainClass}}" >' +
            '  <i class="{{icon}}"></i>{{text}}' +
            '</button>',
        templateLoadUrl: '',
        templateItemTemplate: '<div class="bs-template-item" id="{{id}}">' +
            '  <div class="bs-template-item-image">' +
            '    <img src="{{imageUrl}}" class="img-fluid" />' +
            '  </div>' +
            '  <div class="bs-template-item-desc">' +
            '    <span class="bs-template-item-title">{{title}}</span>' +
            '    <button type="button" class="btn btn-link">加载此模板</button>' +
            '  </div>' +
            '</div>',
    };

    //每个组件(component) 的默认属性
    var defaultProps = [
        //tag 类型
        {
            name: "tag",
            type: "input",
            label: "组件类型",
            placeholder: "",
            disabled: true,
            required: true,
            index: 10,
        },
        {
            name: "id",
            type: "input",
            label: "id",
            placeholder: "",
            disabled: false,
            required: true,
            index: 20,
        },
        {
            name: "name",
            type: "input",
            label: "name",
            placeholder: "",
            disabled: false,
            required: true,
            index: 30,
        },
        {
            name: "label",
            type: "input",
            label: "标签名",
            placeholder: "",
            disabled: false,
            required: false,
            index: 40,
        },
        {
            name: "required",
            type: "switch",
            label: "是否必填",
            placeholder: "",
            disabled: false,
            required: false,
            index: 40,
        },
    ]


    //每个属性类型（type）对于的渲染模板
    // key: prop.type  value:html
    var defaultPropTemplates = {
        input: function () {
            return '<div class="form-group clearfix">' +
                '       <div class="form-label-left">' +
                '             {{~ if(required)}}' +
                '             <span class="red required">*</span>' +
                '             {{~end}}' +
                '              <label for="{{id}}">{{label}}</label>' +
                '        </div>' +
                '        <div class="flex-auto">' +
                '             <input id="{{id}}" {{~if (disabled)}}disabled{{~end}} type="text" data-attr="{{name}}" placeholder="{{placeholder}}" class="onkeyup form-control" value="{{value}}">' +
                '        </div>' +
                '    </div>';
        },
        textarea: function () {
            return '<div class="form-group clearfix">' +
                '       <div class="form-label-left">' +
                '             {{~ if(required)}}' +
                '             <span class="red required">*</span>' +
                '             {{~end}}' +
                '              <label for="{{id}}">{{label}}</label>' +
                '        </div>' +
                '        <div class="flex-auto">' +
                '             <textarea id="{{id}}" {{~if (disabled)}}disabled{{~end}} rows="3" data-attr="{{name}}" placeholder="{{placeholder}}" class="onkeyup form-control">{{value}}</textarea>' +
                '        </div>' +
                '    </div>';
        },
        select: function () {
            return '<div class="form-group clearfix">' +
                '       <div class="form-label-left">' +
                '            <legend class="col-form-label pt-0">{{label}}</legend>' +
                '        </div>' +
                '        <div class="flex-auto">' +
                '            <select class="custom-select onchange" {{~if (disabled)}}disabled{{~end}} data-attr="{{name}}">' +
                '                   {{~for (let option of options)}}' +
                '                   <option value="{{option.value}}" {{~if(option.value == value)}}selected=""{{~end}}>{{option.text}}</option>' +
                '                   {{~end}}' +
                '             </select>' +
                '        </div>' +
                '    </div>'
        },
        number: function () {
            return '<div class="form-group clearfix">' +
                '  <div class="form-label-left">' +
                '    <label for="{{id}}">{{label}}</label>' +
                '  </div>' +
                '  <div class="flex-auto">' +
                '    <input type="number" {{~if (disabled)}}disabled{{~end}} data-attr="{{name}}" class="form-control onchange onkeyup" value="{{value}}" />' +
                '  </div>' +
                '</div>';
        },
        switch: function () {
            return '<div class="form-group clearfix">' +
                '  <div class="form-label-left">' +
                '    <legend class="col-form-label pt-0" for="{{id}}">{{label}}</legend>' +
                '  </div>' +
                '  <div class="flex-auto">' +
                '    <div class="custom-control custom-switch">' +
                '      <input type="checkbox" {{~if (disabled)}}disabled{{~end}} value="true" {{~if(value)}} checked {{~end}} data-attr="{{name}}" ' +
                '           class="custom-control-input onchange" id="{{id}}" />' +
                '      <label class="custom-control-label" for="{{id}}"></label>' +
                '    </div>' +
                '  </div>' +
                '</div>';
        },
        checkbox: function () {
            return '<div class="form-group clearfix">' +
                '  <div class="form-label-left">' +
                '    <legend class="col-form-label pt-0">{{label}}</legend>' +
                '  </div>' +
                '  <div class="flex-auto">' +
                '    {{~ for(let option of options)}}' +
                '    <div class="form-check form-check-inline">' +
                '      <input class="form-check-input onchange" {{~if (disabled)}}disabled{{~end}} {{~ if(value.indexOf(option.value) >=0 )}} checked {{~end}} ' +
                '           type="checkbox" data-attr="{{name}}" data-type="array"' +
                '           id="{{option.value}}-{{id}}" value="{{option.value}}" />' +
                '      <label class="form-check-label" for="{{option.value}}-{{id}}">{{option.text}}</label>' +
                '    </div>' +
                '    {{~end}}' +
                '  </div>' +
                '</div>';
        },
        radio: function () {
            return '<div class="form-group clearfix">' +
                '  <div class="form-label-left">' +
                '    <legend class="col-form-label pt-0">{{label}}</legend>' +
                '  </div>' +
                '  <div class="flex-auto">' +
                '    {{~ for(let option of options)}}' +
                '    <div class="form-check form-check-inline">' +
                '      <input class="form-check-input onchange" name="{{id}}" type="radio" ' +
                '           {{~if (disabled)}}disabled{{~end}} ' +
                '           {{~ if(value == option.value )}} checked {{~end}} ' +
                '           data-attr="{{name}}" id="{{option.value}}-{{id}}" value="{{option.value}}" />' +
                '      <label class="form-check-label" for="{{option.value}}-{{id}}">{{option.text}}</label>' +
                '    </div>' +
                '    {{~end}}' +
                '  </div>' +
                '</div>';
        },
        options: function () {
            return '<div class="option-box options">' +
                '  <div class="divider-title option-filtered">{{title}}</div>' +
                '  {{~for (let option of options)}}' +
                '  <div class="form-group form-check-inline clearfix option-item" id="{{option.elementId}}">' +
                '    <i class="bi bi-arrows-move pointer pr-2 option-handle"></i>' +
                '    <input type="text" value="{{option.text}}" class="form-control mr-2 option-input text" />' +
                '    <input type="text" value="{{option.value}}" class="form-control mr-2 option-input value" />' +
                '    <i class="bi bi-dash-square red pointer option-delete"></i>' +
                '  </div>' +
                '  {{~end}}' +
                '  <div class="text-center option-filtered">' +
                '    <button type="button" class="btn btn-primary btn-sm option-add">添 加</button>' +
                '  </div>' +
                '</div>';
        },
    }

    //bsFormBuilder 内置组件
    var defaultComponents = [


        //自定义 DIV
        {
            "name": "DIV",
            "tag": "div",
            "drag": {
                "title": "DIV",
                "type": "container",
                "index": 100,
                "icon": "bi bi-fullscreen"
            },
            "props": [
                {
                    name: "className",
                    type: "input",
                    label: "Class样式",
                },
                {
                    name: "style",
                    type: "textarea",
                    label: "Style样式",
                }
            ],
            "template": '<div class="bsFormItem" id="{{id}}">' +
                '   <div class="bsItemContainer {{className}}" style="{{style}}">{{$children[0]}}</div>' +
                '</div>',
        },


        //等分栅格
        {
            "name": "等分栅格",
            "tag": "grid",
            "drag": {
                "title": "等分栅格",
                "type": "container",
                "index": 100,
                "icon": "bi bi-grid"
            },
            "props": [
                {
                    name: "grid",
                    type: "radio",
                    label: "栅格数",
                    defaultValue: 2,
                    options: [
                        {
                            value: 2,
                            text: 2
                        },
                        {
                            value: 3,
                            text: 3
                        },
                        {
                            value: 4,
                            text: 4
                        }
                    ],
                }
            ],
            // disableTools: true,
            "template": '<div class="bsFormItem">' +
                '  <div class="form-group clearfix">' +
                '    <div class="row pdlr-15">' +
                '      {{~for (var i=0;i<grid;i++)}}' +
                '      <div class="col-{{12/grid}} bsItemContainer">{{$children[i]}}</div>' +
                '      {{~end}}' +
                '    </div>' +
                '  </div>' +
                '</div>',
            "onPropChange": function (bsFormBuilder, data, propName, value) {
                if (propName !== "grid") {
                    return false;
                }

                var currentData = bsFormBuilder.currentData;
                var intValue = Number.parseInt(value);
                var $row = $('#' + currentData.elementId).children(".form-group").children();
                var gridCount = $row.children().length;

                // 当前存在 grid 数量大于设置的数据，需要移除最后的几个 grid
                if (gridCount > intValue) {
                    for (let i = 0; i < gridCount - intValue; i++) {
                        var $lastCol = $row.children(":last");
                        var bsItemSortable = $lastCol.data("bsItemSortable");

                        //销毁 sortable
                        if (bsItemSortable) {
                            bsItemSortable.destroy();
                        }

                        var index = bsFormBuilder._getItemContainerIndex($lastCol);

                        //移除 data 里的 children 数据
                        if (currentData.children && currentData.children[index]) {
                            delete currentData.children[index];
                        }

                        //销毁子 sortable
                        $lastCol.find('.bsItemContainer').each(function () {
                            var sortable = $(this).data('bsItemSortable');
                            if (sortable) {
                                sortable.destroy();
                            }
                        });
                        $lastCol.remove();
                    }
                }

                // 设置的 grid 数量大于当前存在的 grid 数量，需要在最后面添加 div
                if (intValue > gridCount) {
                    for (let i = 0; i < intValue - gridCount; i++) {
                        $row.append('<div class="bsItemContainer"></div>')
                    }
                }

                $row.children().each(function (index, item) {
                    var sortable = $(this).data('bsItemSortable');
                    if (!sortable) {
                        sortable = new Sortable($(this)[0], {
                            group: 'shared',
                            animation: 150,
                            onAdd: function (evt) {
                                bsFormBuilder._onDragAdd(evt);
                            },
                            onEnd: function (evt) {
                                bsFormBuilder._onDragEnd(evt);
                            },
                        });
                        $(this).data('bsItemSortable', sortable);
                    }
                });

                $row.children().attr("class", "bsItemContainer col-" + 12 / Number.parseInt(value));
                return true;
            }
        },


        //灵活栅格
        {
            "name": "灵活栅格",
            "tag": "sgrid",
            "drag": {
                "title": "灵活栅格",
                "type": "container",
                "index": 100,
                "icon": "bi bi-grid-1x2"
            },
            optionsCounter: 2,
            withOptions: true,
            optionsTitle: '栅格配置',
            optionsTypes: 'custom', //options 类型值支持自定义一种方式
            opiontsAdd: function () {
                return {
                    text: "栅格" + (++this.optionsCounter),
                    value: 12
                }
            },
            defaultOptions: [
                {
                    text: "栅格1",
                    value: 6
                },
                {
                    text: "栅格2",
                    value: 6
                }
            ],
            "template": '<div class="bsFormItem">' +
                '  <div class="form-group clearfix">' +
                '    <div class="row pdlr-15">' +
                '      {{~for (var i = 0;i<options.length;i++)}}' +
                '      <div class="col-{{options[i].value}} bsItemContainer">{{$children[i]}}</div>' +
                '      {{~end}}' +
                '    </div>' +
                '  </div>' +
                '</div>',
            "onPropChange": function (bsFormBuilder, data, propName, value) {

                if (propName !== "options" || !data.children) {
                    return false;
                }

                //修改顺序的时候，保证顺序下的 data 跟着修改
                var idDataMapping = {};

                let oldOptions = data.options;
                let newOptions = value;

                for (let i = 0; i < oldOptions.length; i++) {
                    let oldOption = oldOptions[i];
                    idDataMapping[oldOption.elementId] = data.children[i];
                }

                for (let i = 0; i < newOptions.length; i++) {
                    let newOption = newOptions[i];
                    data.children[i] = idDataMapping[newOption.elementId];
                }
            }
        },

        //tab布局
        {
            "name": "Tab选项卡",
            "tag": "tab",
            "drag": {
                "title": "Tab选项卡",
                "type": "container",
                "index": 100,
                "icon": "bi bi-menu-button"
            },
            withOptions: true,
            optionsTypes: 'custom', //options 类型值支持自定义一种方式
            counter: 1,
            defaultOptions: function (bsFormBuilder, data) {
                let counter1 = this.counter++;
                let counter2 = this.counter++;
                return [
                    {
                        text: "标签" + counter1,
                        value: "tab_id_" + counter1
                    },
                    {
                        text: "标签" + counter2,
                        value: "tab_id_" + counter2
                    }
                ]
            },
            "template": '<div class="bsFormItem">' +
                '  <div class="form-group clearfix">' +
                '    <div class="pdlr-15">' +
                '      <ul class="nav nav-tabs" role="tablist">' +
                '        {{~for (var i = 0;i<options.length;i++)}}' +
                '        <li class="nav-item">' +
                '          <a class="nav-link {{~if (i == 0)}}active {{~end}}" id="{{options[i].value}}-tab" data-toggle="tab" href="#{{options[i].value}}"' +
                '            role="tab" aria-controls="{{options[i].value}}" aria-selected="{{~if(i==0)}}true{{~else}}false{{~end}}" >{{options[i].text}}</a >' +
                '        </li>' +
                '        {{~end}}' +
                '      </ul>' +
                '      <div class="tab-content">' +
                '        {{~for (var i = 0;i<options.length;i++)}}' +
                '        <div class="bsItemContainer tab-pane fade {{~if (i == 0)}}active show {{~end}}"' +
                '          id="{{options[i].value}}" role="tabpanel" aria-labelledby="{{options[i].value}}-tab" >' +
                '          {{$children[i]}}' +
                '        </div>' +
                '        {{~end}}' +
                '      </div>' +
                '    </div>' +
                '  </div>' +
                '</div>',
            "onAdd": function (bsFormBuilder, data) {
                var el = $("#" + data.elementId);
                el.find(".nav-link").on("click", function (event) {
                    event.stopPropagation();
                    $(this).tab('show');
                    return false;
                });
            },
            "onPropChange": function (bsFormBuilder, data, propName, value) {
                if (propName !== "options" || !data.children) {
                    return false;
                }

                //修改顺序的时候，保证顺序下的 data 跟着修改
                var idDataMapping = {};

                let oldOptions = data.options;
                let newOptions = value;

                for (let i = 0; i < oldOptions.length; i++) {
                    let oldOption = oldOptions[i];
                    idDataMapping[oldOption.elementId] = data.children[i];
                }

                for (let i = 0; i < newOptions.length; i++) {
                    let newOption = newOptions[i];
                    data.children[i] = idDataMapping[newOption.elementId];
                }
            }
        },
    ];


    //BsFormBuilder 类的定义以及初始化
    var BsFormBuilder = function (element, options) {
        //bsFormBuilder 配置信息
        this.options = $.extend(defaultOptions, options);

        //每个组件的默认属性
        this.defaultProps = options.defaultProps || [];
        for (let defaultProp of defaultProps) {
            if (this.defaultProps.map(item => item.name).indexOf(defaultProp.name) === -1) {
                this.defaultProps.push(defaultProp);
            }
        }

        //每个属性类型渲染的 html
        this.propTemplates = $.extend(defaultPropTemplates, options.propTemplates);

        //当前的开启的组件
        this.useComponents = options.useComponents || [];

        //不使用的组件
        this.unUseComponents = options.unUseComponents || [];

        //根节点元素
        this.$rootEl = $(element);

        //设计容器 div
        this.$container = null;

        //设计的占位 div
        this.$containerPlaceHolder = null;

        //属性面板
        this.$propsPanel = null;

        //所有的组件, map(key== tag, value == component)
        this.components = {};

        //渲染的数据
        this.datas = [];

        //当前获得焦点的组件数据
        this.currentData = null;

        //组件序号记录器，用于在添加组件的时候，生成组件的 name + (componentCounter++)
        this.componentCounter = 1;

        //属性面板 options 添加值是的 index 记录器
        //默认值是 3 的原因是，一般带有 options 的组件，都会默认有两个 options 了
        this.optionsCounter = 3;

        //http 缓存
        this.ajaxCache = {};

        //初始化
        if (this.options.mode === "view") {
            this._initViewMode();
        } else {
            this._initBuilderMode();
        }
    }


    //BsFormBuilder 方法定义
    BsFormBuilder.prototype = {

        /**
         * 初始化 view
         * @private
         */
        _initViewMode: function () {
            //初始化 view 的 html 结构
            this._initViewStructure();

            this.$container = this.$rootEl.find(this.options.bsFormContainerSelector);

            if (this.$container.length === 0) {
                throw new Error("Can not file container by: " + this.options.bsFormContainerFilterSelector);
            }

            var bsFormBuilder = this;

            //初始化默认的组件库
            this._initComponents(function () {

                //初始化 data 数据
                bsFormBuilder._initData(this.options.datas, true);

                //渲染 view 的数据到 html
                bsFormBuilder._refreshViewContainer();

                //onInit 回调
                bsFormBuilder._invokeOnInitCallback();
            });

        },

        /**
         * 初始化 Builder
         */
        _initBuilderMode: function () {

            //若用户自定义结构，则无需初始化自己的结构
            if (!(this.options.customBuilderStructure === true)) {

                //初始化整个 html 结构
                this._initBuilderStructure();
            }

            this.$container = this.$rootEl.find(this.options.bsFormContainerSelector);

            if (this.$container.length === 0) {
                throw new Error("Can not file container by: " + this.options.bsFormContainerSelector);
            }

            this.$containerPlaceHolder = this.$rootEl.find(this.options.bsFormContainerPlaceHolderSelector);

            this.$propsPanelTitle = this.$rootEl.find(this.options.bsFormPropsTitleSelector);

            this.$propsPanel = this.$rootEl.find(this.options.bsFormPropsSelector);

            var bsFormBuilder = this;

            //初始化默认的组件库，必须 components 全部初始化完毕后
            //才能去初始化其他内容
            this._initComponents(function () {

                //初始化操作按钮
                bsFormBuilder._initActionButtons();

                //初始化拖动的组件
                bsFormBuilder._initDragComponents();


                var invoker = function (optionsDatas) {
                    //初始化 data 数据
                    bsFormBuilder._initData(optionsDatas, true);

                    //初始化 options 导入的 data 的数据
                    bsFormBuilder._refreshBuilderContainer();

                    //初始化表单事件监听
                    bsFormBuilder._initEvents();

                    //初始化拖动组件
                    bsFormBuilder._initSortables();

                    //onInit 回调
                    bsFormBuilder._invokeOnInitCallback();
                }


                //用户自定义组件
                var optionDatas = bsFormBuilder.options.datas;

                // url 请求
                if (typeof optionDatas === "string" && bsFormBuilder._isUrl(optionDatas)) {
                    bsFormBuilder._ajaxGet(optionDatas, "datas", invoker);
                }

                //直接配置数组
                else if (typeof optionDatas === "object" && Array.isArray(optionDatas)) {
                    invoker(optionDatas);
                }

                //方法
                else if (typeof optionDatas === "function") {

                    //执行该方法，该方法可以是异步加载，加载到数据后执行 initComponents 来初始化数据
                    optionDatas = optionDatas(invoker);

                    //若有返回数据，则初始化数据
                    if (typeof optionDatas === "object" && Array.isArray(optionDatas)) {
                        invoker(optionDatas);
                    }
                } else {
                    invoker()
                }

            });


        },

        /**
         * 回调 onInit
         * @private
         */
        _invokeOnInitCallback: function () {
            if (typeof this.options.onInit === "function") {
                this.options.onInit(this);
            }
        },


        /**
         * 初始化 options.datas 的 mode 的数据
         * @param array datas 数据
         * @param pushToRoot 是否要添加到根节点
         * @private
         */
        _initData: function (array, pushToRoot) {
            if (!array || array.length === 0) {
                return;
            }

            //根据 index 对 arra 进行升序排序
            //index 越小越靠前
            array.sort((a, b) => a.index - b.index);

            for (let data of array) {

                if (!data || !data.tag) {
                    continue;
                }

                //若系统没有此 data 定义的组件，忽略此 data 数据
                let component = this.components[data.tag];
                if (!component) {
                    console.warn("Can not find component by tag: " + data.tag);
                    continue;
                }


                //为 data 设置默认数据，默认数据来源于 data 对应的 component 的 defaultValue 属性
                if (component.props) {
                    for (const prop of component.props) {
                        if (typeof prop.defaultValue !== "undefined" && !data[prop.name]) {
                            data[prop.name] = prop.defaultValue;
                        }
                    }
                }

                //初次导入的 data 是没有 component 和 elementId 属性的
                //需要为 data 添加 component 和 elementId 属性
                data.component = component;
                data.elementId = this.genRandomId();

                if (data.children) {
                    for (let childArray of Object.values(data.children)) {
                        this._initData(childArray, false)
                    }
                }

                //把 data 数据添加到 bsFormBuilder 的 datas 属性里
                if (pushToRoot) {
                    this.datas.push(data);
                }
            }
        },


        /**
         * 渲染 view 的数据
         * 1、初始化的时候刷新
         * 2、用户通过 api 添加 data 的时候刷新
         * @private
         */
        _refreshViewContainer: function () {
            this.$container.children(".bsFormItem").remove();
            for (let data of this.datas) {
                var html = this.render(data, false).outerHTML;
                this.$container.append(html)
            }
        },


        /**
         * 初始化 view 的 html 结构
         * @private
         */
        _initViewStructure: function () {
            this.$rootEl.append('<div class="row bsFormViewRoot"><div class="col-12 bsFormContainer"></div></div>');
        },

        /**
         * 初始化 builder 的 html 结构
         * @private
         */
        _initBuilderStructure: function () {
            this.$rootEl.append('<div class="row bsFormBuilderRoot">' +
                '  <!--左侧拖拽区域-->' +
                '  <div class="col-md-3 col-sm-4">' +
                '    <div class="bsFormPanel pd10 border-right">' +
                '      <ul class="nav nav-tabs mb-2" id="formTab" role="tablist">' +
                '        <li class="nav-item w-50">' +
                '          <a class="nav-link active" id="component-tab" data-toggle="tab" href="#component"' +
                '            role="tab" aria-controls="component" aria-selected="true">表单组件</a>' +
                '        </li>' +
                '        <li class="nav-item w-50">' +
                '          <a class="nav-link" id="module-tab"  data-toggle="tab" href="#template"' +
                '            role="tab" aria-controls="module" aria-selected="false">表单模板</a>' +
                '        </li>' +
                '      </ul>' +
                '      <div class="tab-content">' +
                '        <div class="tab-pane fade show active" id="component" role="tabpanel" aria-labelledby="component-tab" >' +
                '          <div class="bsFormDrags-title">表单组件</div>' +
                '          <div class="bsFormDrags d-flex align-items-center" data-type="base"></div>' +
                '          <div class="bsFormDrags-title">辅助组件</div>' +
                '          <div class="bsFormDrags d-flex align-items-center" data-type="assist"></div>' +
                '          <div class="bsFormDrags-title">布局组件</div>' +
                '          <div class="bsFormDrags d-flex align-items-center" data-type="container"></div>' +
                '        </div>' +
                '        <div class="tab-pane fade" id="template"  role="tabpanel" aria-labelledby="module-tab">' +
                '          <div id="bs-template-item-list" class="bs-template-item-list"></div>' +
                '        </div>' +
                '      </div>' +
                '    </div>' +
                '  </div>' +
                '  <!-- 中间内容 -->' +
                '  <div class="bsFormPanel col-md-6 col-sm-4">' +
                '    <div class="w-100 pd10 border-bottom text-right pt-1 pb-1 bsFormActions"></div>' +
                '    <div style="width: 100%;" class="bsFormContainer">' +
                '      <div class="bsFormContainer-placeholder">从左侧拖入组件进行表单设计</div>' +
                '    </div>' +
                '  </div>' +
                '  <!-- 属性内容 -->' +
                '  <div class="col-md-3 col-sm-4">' +
                '    <div class="bsFormPanel pd10 border-left">' +
                '      <ul class="nav nav-tabs mb-2" id="formAttrTab" role="tablist">' +
                '        <li class="nav-item w-50">' +
                '          <a  class="nav-link active" id="component-props-tab" data-toggle="tab" href="#bsFormProps"' +
                '            role="tab" aria-controls="component" aria-selected="true">组件属性</a>' +
                '        </li>' +
                '      </ul>' +
                '      <div class="tab-content pt-3">' +
                '        <div class="tab-pane fade show active bsFormProps" id="bsFormProps" role="tabpanel" aria-labelledby="component-props-tab">' +
                '        </div>' +
                '      </div>' +
                '    </div>' +
                '  </div>' +
                '</div>')
        },


        /**
         * 初始化 操作按钮
         * @private
         */
        _initActionButtons: function () {
            let template = this.options.actionButtonTemplate;
            if (!template || template === "") {
                return;
            }

            let bsFormBuilder = this;

            let actionButtons = this.options.actionButtons || [];
            for (let btnObj of actionButtons) {
                let paras = ["$button", "text", "mainClass", "icon"];

                let values = paras.map(k => btnObj[k] || "");
                values[0] = btnObj;

                let html = this._renderTemplate(template, paras, values);

                let $btnEl = $(html).attr("id", this.genRandomId());
                $btnEl.appendTo(".bsFormActions");

                if (typeof btnObj.onclick === "function") {
                    $btnEl.on("click", function (event) {
                        btnObj.onclick(event, bsFormBuilder);
                    });
                }
            }
        },

        /**
         * 初始化系统组件
         * @private
         */
        _initComponents: function (initAfterFunc) {
            var useComponents = this.options.useComponents;
            if (!useComponents) useComponents = [];

            var unUseComponents = this.options.unUseComponents;
            if (!unUseComponents) unUseComponents = [];

            for (let component of defaultComponents) {
                if (component && (useComponents.length === 0 || useComponents.indexOf(component.tag) > -1) && unUseComponents.indexOf(component.tag) === -1) {
                    this.components[component.tag] = component;
                }
            }

            //插件定义的 components 定义
            if (window.bsComponentsDef) {
                for (let component of window.bsComponentsDef) {
                    if (component && (useComponents.length === 0 || useComponents.indexOf(component.tag) > -1) && unUseComponents.indexOf(component.tag) === -1) {
                        this.components[component.tag] = component;
                    }
                }
            }

            var bsFromBuilder = this;
            var initComponents = function (customComponents) {


                //用户自定义的 component 继承来自已经存在的 component
                //这样，用户可以不用配置系统已经存在的配置信息
                for (let component of customComponents) {
                    if (component && component.tag) {
                        component = $.extend(bsFromBuilder.components[component.tag], component);
                        bsFromBuilder.components[component.tag] = component;
                    }
                }

                initAfterFunc();
            }

            //用户自定义组件
            var customComponents = this.options.components;

            // url 请求
            if (typeof customComponents === "string" && this._isUrl(customComponents)) {
                this._ajaxGet(customComponents, "components", initComponents);
            }

            //直接配置数组
            else if (typeof customComponents === "object" && Array.isArray(customComponents)) {
                initComponents(customComponents);
            }

            //方法
            else if (typeof customComponents === "function") {

                //执行该方法，该方法可以是异步加载，加载到数据后执行 initComponents 来初始化数据
                customComponents = customComponents(initComponents);

                //若有返回数据，则初始化数据
                if (typeof customComponents === "object" && Array.isArray(customComponents)) {
                    initComponents(customComponents);
                }
            } else {
                initAfterFunc()
            }
        },


        /**
         * 判断是否是 url
         * @param text
         * @returns {boolean}
         * @private
         */
        _isUrl: function (text) {
            return text && (text.indexOf("/") === 0
                || text.indexOf("http://") === 0
                || text.indexOf("https://") === 0)
        },

        /**
         * 异步 ajax 获取数据，并回调 func 方法
         * @param url
         * @param attr
         * @param func
         * @private
         */
        _ajaxGet: function (url, attr, func) {
            var cacheData = this.ajaxCache[url];
            if (cacheData) {
                var needData = cacheData[attr];
                if (needData) {
                    func(needData);
                }
            }

            var bsFormBuilder = this;

            $.ajax({
                url: url,
                type: 'GET',
                success: function (data) {
                    bsFormBuilder.ajaxCache[url] = data;
                    var needData = data[attr];
                    if (needData) {
                        func(needData);
                    }
                }
            })
        },


        /**
         * 同步 ajax 获取数据
         * @param url
         * @returns {string|*}
         * @private
         */
        _ajaxSyncGet: function (url) {
            var cacheData = this.ajaxCache[url];
            if (cacheData) {
                return cacheData;
            }
            var ret = "";
            $.ajax({
                url: url,
                async: false,
                type: 'GET',
                success: function (data) {
                    ret = data;
                }
            })
            this.ajaxCache[url] = ret;
            return ret;
        },


        /**
         * 同步进行 post
         * @param url
         * @param data
         * @returns {string}
         * @private
         */
        _ajaxSyncPost: function (url, data) {
            var ret = "";
            $.ajax({
                url: url,
                async: false,
                type: "POST",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(data),
                success: function (resp) {
                    ret = resp;
                }
            })
            return ret;
        },


        /**
         * 初始化左侧拖动的组件库
         */
        _initDragComponents: function () {
            if (!this.components || this.components.length === 0) {
                return;
            }

            //key --> type , value --> component array
            var allDrags = {};

            for (let component of Object.values(this.components)) {

                // 支持未定义 drag 的组件
                // 这种组件无法被拖住，只能通过初始化 options.datas 的方式来显示
                if (!component.drag) {
                    continue;
                }

                if (component.drag.type) {
                    //copy component.tag to drag.tag
                    component.drag['tag'] = component.tag;

                    var drags = allDrags[component.drag.type];
                    if (!drags) {
                        drags = [];
                        allDrags[component.drag.type] = drags;
                    }
                    drags.push(component.drag);
                } else {
                    console.error("Component define error! it must defined drag.type in component. ", component);
                }
            }

            //根据 index 进行排序
            for (let dragArray of Object.values(allDrags)) {
                dragArray.sort((a, b) => a.index - b.index);
            }

            var bsFormBuilder = this;

            $('.bsFormDrags').each(function (index, element) {
                var $group = $(element);
                var type = $group.data('type');
                var dragArray = allDrags[type];
                if (dragArray && dragArray.length > 0) {

                    //移除容器里的 placeholder div
                    $group.find(".placeholder").remove();

                    for (let drag of dragArray) {
                        //icon is html
                        if (drag.icon && drag.icon.trim().charAt(0) === '<') {
                            $group.append('<ol data-tag="' + drag.tag + '"><div class="item-icon">'
                                + drag.icon + '"</div><div class="item-title">' + drag.title + '</div></ol>');
                        }
                        //icon is class
                        else {
                            $group.append('<ol data-tag="' + drag.tag + '"><div class="item-icon"><i class="'
                                + drag.icon + '"></i></div><div class="item-title">' + drag.title + '</div></ol>');
                        }
                    }
                }
                //没有找到该类型的任何组件
                else {
                    if (bsFormBuilder.options.renderEmptyDrags && typeof bsFormBuilder.options.renderEmptyDrags === "function") {
                        bsFormBuilder.options.renderEmptyDrags(bsFormBuilder, $group, type)
                    }
                }
            })
        },


        /**
         * 渲染初始化的数据
         */
        _refreshBuilderContainer: function () {
            this.$container.find('.bsItemContainer').each(function () {
                let sortable = $(this).data('bsItemSortable');
                if (sortable) sortable.destroy();
            });
            this.$container.children(".bsFormItem").remove();
            if (!this.datas || this.datas.length === 0) {
                this.$containerPlaceHolder.show();
            } else {
                this.$containerPlaceHolder.hide();
                for (let data of this.datas) {
                    var el = this.render(data, false);
                    this._invokeComponentOnAddBefore(data, el)
                    this.$container.append(el.outerHTML);
                    this._invokeComponentOnAdd(data);
                }
            }
        },

        /**
         * 初始化 bsFormBuilder 的事件机制
         * @private
         */
        _initEvents: function () {
            var bsFormBuilder = this;
            //container 下的每个 item 的点击事件
            this.$container.on("click", ".bsFormItem", function (event) {
                event.stopPropagation();
                bsFormBuilder.makeFormItemActive($(this).attr('id'));
            })


            //container 下的每个 item 的 复制按钮 的点击事件
            this.$container.on("click", ".bs-item-copy", function (event) {
                event.stopPropagation();
                var currentId = $(this).closest('.bsFormItem').attr('id');
                bsFormBuilder.copyFormItem(currentId);
            })

            //container 下的每个 item 的 复制按钮 的删除事件
            this.$container.on("click", ".bs-item-del", function (event) {
                event.stopPropagation();
                var $bsFormItem = $(this).closest('.bsFormItem');
                bsFormBuilder.deleteFormItem($bsFormItem.attr("id"));

                if (!bsFormBuilder.datas || bsFormBuilder.datas.length === 0) {
                    bsFormBuilder.$containerPlaceHolder.show();
                }
            })

            //props 事件
            var propsEventFunction = function (event) {
                var attr = $(this).attr('data-attr');
                var value = $(this).val();

                //数据类型
                var type = $(this).attr("data-type");

                //数据类型如果是数组
                if (type === "array") {
                    var oldValue = bsFormBuilder.currentData[attr] || [];
                    var index = oldValue.indexOf(value);
                    if (event.currentTarget.type === "checkbox") {
                        //添加
                        if (event.currentTarget.checked && index === -1) {
                            oldValue.push(value)
                        }
                        //移除
                        else if (!event.currentTarget.checked && index >= 0) {
                            oldValue.splice(index, 1)
                        }
                    }
                    value = oldValue;
                } else {
                    // 若是 checkbox，value 值是 checkbox 的选中状态
                    if (event.currentTarget.type === "checkbox") {
                        value = event.currentTarget.checked;
                    }
                }

                //没有选中的组件，理论上不存在这种情况
                if (!bsFormBuilder.currentData) {
                    console.error("error: Current data not exits!!!")
                } else {
                    bsFormBuilder.updateDataAttr(bsFormBuilder.currentData, attr, value)
                }
            }

            //监听属性面板的输入框的输入事件
            this.$propsPanel.on("keyup", ".onkeyup", propsEventFunction);
            this.$propsPanel.on("change", ".onchange", propsEventFunction);


            this.$propsPanel.on("keyup", ".option-input", function () {
                bsFormBuilder._syncCurrentDataOptionsFromPropSetting();
            });

            //删除属性面板里的 options 的 item
            this.$propsPanel.on("click", ".option-delete", function (event) {
                $(this).closest(".option-item").remove();
                bsFormBuilder._syncCurrentDataOptionsFromPropSetting();
            });

            //添加 item
            this.$propsPanel.on("click", ".option-add", function (event) {
                var options = bsFormBuilder.deepCopy(bsFormBuilder.currentData.options, false) || [];

                var index = bsFormBuilder.optionsCounter++;

                var newOption = null;
                if (bsFormBuilder.currentData.component.opiontsAdd) {
                    if (typeof bsFormBuilder.currentData.component.opiontsAdd === "function") {
                        newOption = bsFormBuilder.currentData.component.opiontsAdd(bsFormBuilder)
                    } else {
                        newOption = bsFormBuilder.currentData.component.opiontsAdd;
                    }
                }

                if (!newOption) {
                    newOption = {text: "选项" + index, value: "value" + index}
                }

                if (!newOption.elementId) {
                    newOption.elementId = bsFormBuilder.genRandomId();
                }

                options.push(newOption)

                bsFormBuilder.updateDataAttr(bsFormBuilder.currentData, "options", options);
                bsFormBuilder.refreshPropsPanel();
            });


        },


        /**
         * 初始化 sortable
         * @private
         */
        _initSortables: function () {
            var bsFormBuilder = this;
            $('.bsFormDrags').each(function (index, element) {
                var $element = $(element);
                if (!$element.data('sortable')) {
                    var group = $element.data('group');
                    if (!group) group = 'shared';
                    var sortable = new Sortable($element[0], {
                        group: {
                            name: group,
                            pull: 'clone',
                            put: false
                        },
                        filter: '.filtered,.placeholder', // 'filtered' class is not draggable
                        animation: 150,
                        sort: false,
                        onStart: function (evt) {
                            if (bsFormBuilder.$containerPlaceHolder) {
                                bsFormBuilder.$containerPlaceHolder.hide();
                            }
                        },
                        onEnd: function (evt) {
                            if (bsFormBuilder.datas.length === 0) {
                                bsFormBuilder.$containerPlaceHolder.show();
                            }
                        }
                    });
                    $element.data('sortable', sortable);
                }
            })

            if (!this.$container.data("sortable")) {
                var sortable = new Sortable(this.$container[0], {
                    group: this.options.bsFormContainerSortableGroup,
                    filter: this.options.bsFormContainerFilterSelector,
                    animation: 150,
                    onAdd: function (evt) {
                        bsFormBuilder._onDragAdd(evt);
                    },
                    onEnd: function (evt) {
                        bsFormBuilder._onDragEnd(evt);
                    },
                });
                this.$container.data('sortable', sortable);
            }
        },


        /**
         * 监听 container 添加组件
         * @param evt
         * @private
         */
        _onDragAdd: function (evt) {
            var data = null;

            var $item = $(evt.item);
            var tag = $item.attr("data-tag");

            //从左边的组件库移动过来的
            if (tag) {
                //根据 component 来创建 data
                data = this.createComponentData(this.components[tag]);

                //渲染 component template
                var el = this.render(data, false);

                this._invokeComponentOnAddBefore(data, el);
                $(evt.item).replaceWith(el);
                this._invokeComponentOnAdd(data);
            }

            //从一个容器移动到另一个容器
            else {
                data = this.getDataByElementId($item.attr("id"));
            }

            //从原有的数组中移除
            this.removeDataByElementId(data.elementId);

            var $to = $(evt.to);

            //拖动到 root 容器，则删除其父级数据
            if ($to.is(this.options.bsFormContainerSelector)) {
                delete data.parentDataId;
                delete data.parentDataIndex;
                this.datas.push(data);
            }

            //拖动到子容器
            else {
                var newParentId = $to.closest(".bsFormItem").attr("id");
                var dataIndex = this._getItemContainerIndex($to);

                var newParent = this.getDataByElementId(newParentId);

                if (typeof newParent.children === "undefined") {
                    newParent.children = {};
                }

                if (typeof newParent.children[dataIndex] === "undefined") {
                    newParent.children["" + dataIndex] = [];
                }

                data.parentDataId = newParent.id;
                data.parentDataIndex = dataIndex;
                newParent.children[dataIndex].push(data);
            }


            //让当前的组件处于选中状态
            this.makeFormItemActive(data.elementId);
            this.refreshDataIndex($to);
        },


        /**
         * 获取 bsItemContainer 的 index 位置
         * @param $el
         * @returns {number}
         * @private
         */
        _getItemContainerIndex: function ($el) {
            var dataIndex = 0;
            $el.parent().children(".bsItemContainer").each(function (index, el) {
                if (el === $el[0]) {
                    dataIndex = index;
                }
            });
            return dataIndex;
        },


        /**
         * 执行 component 的  onAddBefore 方法
         * @param data
         * @param el
         * @private
         */
        _invokeComponentOnAddBefore: function (data, el) {
            if (data.component && typeof data.component.onAddBefore === "function") {
                data.component.onAddBefore(this, data, el);
            }
        },


        /**
         * 执行 component 的 onAdd 方法
         * @private
         */
        _invokeComponentOnAdd: function (data) {
            let bsFormBuilder = this;
            //初始化 element 的 container 容器
            $("#" + data.elementId).find('.bsItemContainer').each(function () {
                var sortable = $(this).data('bsItemSortable');
                if (!sortable) {
                    sortable = new Sortable($(this)[0], {
                        group: 'shared',
                        animation: 150,
                        onAdd: function (evt) {
                            bsFormBuilder._onDragAdd(evt);
                        },
                        onEnd: function (evt) {
                            bsFormBuilder._onDragEnd(evt);
                        },
                    });
                    $(this).data('bsItemSortable', sortable);
                }
            });

            if (data.component && typeof data.component.onAdd === "function") {
                data.component.onAdd(this, data);
            }
        },


        /**
         * 执行 component 的 onDelete 方法
         * @param data
         * @private
         */
        _invokeComponentOnDelete: function (data) {

            //销毁 component 的 sortable
            $("#" + data.elementId).find('.bsItemContainer').each(function () {
                var sortable = $(this).data('bsItemSortable');
                if (sortable) {
                    sortable.destroy();
                }
            });

            if (data.component && typeof data.component.onDelete === "function") {
                data.component.onDelete(this, data);
            }
        },


        /**
         * 监听 container 拖动组件
         * @param evt
         * @private
         */
        _onDragEnd: function (evt) {
            this.refreshDataIndex($(evt.from))
        },


        /**
         * 获取渲染方法
         * @param template
         * @private
         */
        // _getRenderMethodBody: function (template) {
        //     let body = template.replace(/\'/g, "&#39;")
        //         .replace(/\"/g, "&quot;")
        //         .replace(/[\r\n\t]/g, "")
        //         .replace(/\{\{.+\&#39;*.\}\}/g, x => {
        //             return x.replace(/\&#39;/g, "\'")
        //         })
        //         .replace(/\{\{.+\&quot;*.\}\}/g, x => {
        //             return x.replace(/\&quot;/g, '"')
        //         })
        //         .replace(/\{\{~\s*end\s*\}\}/g, "\"}ret+=\"")
        //         .replace(/\{\{~\s*else\s*\}\}/g, () => {
        //             return '";}else{ ret+="';
        //         })
        //         .replace(/\{\{~\s*elseif.+?\}\}/g, x => {
        //             return x.replace("elseif", "}else if")
        //         })
        //         .replace(/\{\{~(.+?)\}\}/g, (_, x) => {
        //             return '";' + x + '{ ret+="';
        //         })
        //         .replace(/\{\{(.+?)\}\}/g, (_, x) => {
        //             return '"; ret+= ' + x + '; ret+="';
        //         });
        //     return 'let ret=""; ret += "' + body + '";return ret;';
        // },


        /**
         * 渲染 template， 生成 string 类型的 html 返回
         * @param template
         * @param paras
         * @param values
         * @returns {string|*}
         * @private
         */
        _renderTemplate: function (template, paras, values) {
            // try {
            //     let body = this._getRenderMethodBody(template);
            //     return new Function(...paras, body)(...values)
            //         .replace(/\&#39;/g, '\'').replace(/\&quot;/g, '"');
            // } catch (err) {
            //     console.error("template error  >>>", err);
            //     console.error("template paras  >>>", paras);
            //     console.error("template values >>>", values);
            //     console.error("template        >>>", template);
            //     return "";
            // }

            var data = {};
            for (let i = 0; i < paras.length; i++) {
                data[paras[i]] = values[i]
            }
            return this.options.templateEngine.render(template, data)
        },


        /**
         * 初始化 data 的 options
         * @param data
         * @private
         */
        _initDataOptionsIfNecessary: function (data) {
            if (data.component.withOptions && !data.options) {

                var defaultOptions = data.component.defaultOptions;
                if (typeof defaultOptions === "function") {
                    defaultOptions = data.component.defaultOptions(this, this.currentData)
                }


                if (!defaultOptions) {
                    var datasources = this._getOptionDatasources();
                    if (datasources && datasources.length > 0) {
                        defaultOptions = this._parseOptions(datasources[0].options);
                        data["optionsDatasource"] = datasources[0].value;
                    }
                }


                data.options = defaultOptions || [];

                //为每个 options 配置一个 id
                data.options.forEach(item => {
                    item.elementId = this.genRandomId()
                })

                data.optionsTitle = data.component.optionsTitle || "选项";
            }
        },

        /**
         * 获取属性模板
         * @param type
         * @returns {*}
         * @private
         */
        _getPropTemplateByType: function (type) {
            let template = this.propTemplates[type];
            if (typeof template === "function") {
                template = template();
            }

            if (!template && type !== "none") {
                throw new Error("Not define prop template for type: " + type);
            }
            return template;
        },


        /**
         * 同步 currentData 的 options
         * @private
         */
        _syncCurrentDataOptionsFromPropSetting: function () {
            var options = [];
            var optionItems = this.$propsPanel.children(".options").children(".option-item");
            optionItems.each(function (index, item) {
                var text = $(item).children(".option-input.text").val();
                var value = $(item).children(".option-input.value").val();
                var elementId = $(item).attr("id");
                options.push({text, value, elementId});
            });
            this.updateDataAttr(this.currentData, "options", options);
        },

        /**
         * 初始化 component 的 data 数据
         * @param component
         * @private
         */
        createComponentData: function (component) {
            var data = component.onDataCreate && typeof component.onDataCreate === "function"
                ? component.onDataCreate() : {};

            //组件的 id
            data.elementId = this.genRandomId();

            //input 表单的id，用户可以通过属性面板进行修改
            data.id = this.genRandomId();


            data.tag = component.tag;
            data.component = component;

            if (!data.label) {
                data.label = component.name;
            }

            if (!data.name) {
                data.name = component.tag + "_" + this.componentCounter++;
            }

            //组件定义的属性，为 data 配置上： data.属性名 = 属性默认值
            var allProps = this._getComponentAllProps(component, data);
            for (let prop of allProps) {
                if (typeof prop.defaultValue !== "undefined" && !data[prop.name]) {
                    data[prop.name] = prop.defaultValue;
                }
            }

            return data;
        },


        /**
         * 生成一个随机的 ID
         * @private
         */
        genRandomId: function () {
            var id = [];
            var hexDigits = "0123456789abcdef";
            for (var i = 0; i < 10; i++) {
                id[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
            }
            return this.options.componentIdPrefix + id.join("");
        },


        /**
         * 渲染模板，替换掉模板里的 {{xxx}} 数据
         * @param data 数据
         * @private
         */
        renderDefault: function (data) {

            let children = [];
            if (typeof data.children === "object") {
                for (let key of Object.keys(data.children)) {
                    let childArray = data.children ? data.children[key] : [];
                    let htmlContent = "";
                    if (childArray) {
                        for (let item of childArray) {
                            let html = this.render(item, false);
                            if (html) htmlContent += html.outerHTML;
                        }
                    }
                    children[key] = htmlContent;
                }
            }

            let childrenProxy = new Proxy(children, {
                get: function (target, attr) {
                    return target[attr] || "";
                }
            })


            //default props + component.props + "value" + "placeholder" +"options"
            var allPropNames = this.defaultProps.map(prop => prop.name).concat(["value", "placeholder", "options"]);
            if (data.component.props) {
                allPropNames = allPropNames.concat(data.component.props.map(prop => prop.name));
            }

            var paras = ["$builder", "$component", "$data", "$children"].concat(allPropNames);

            var values = paras.map(k => data[k] || "");
            values[0] = this;
            values[1] = data.component;
            values[2] = data;
            values[3] = childrenProxy;

            return this._renderTemplate(data.component.template, paras, values);
        },


        /**
         * 深度拷贝
         * @param target 拷贝目标
         * @param withNewElementIdAndId 是否为 elementId 和 id 设置新的值
         * @returns {*[]}
         * @private
         */
        deepCopy: function (target, withNewElementIdAndId) {
            var newObject = Array.isArray(target) ? [] : {};
            if (target && typeof target === "object") {
                for (let key in target) {
                    if (target.hasOwnProperty(key)) {
                        if (target[key] && typeof target[key] === "object") {
                            newObject[key] = this.deepCopy(target[key], withNewElementIdAndId);
                        } else {
                            var value = target[key];
                            if (withNewElementIdAndId && (key === "elementId" || key === "id")) {
                                value = this.genRandomId();
                            }
                            newObject[key] = value;
                        }
                    }
                }
            }
            return newObject;
        },


        /**
         * 渲染 component 的模板内容
         * @param data 组件内容
         * @param withActive 是否让当前的组件处于 "选中" 状态
         * @returns {*}
         * @private
         */
        render: function (data, withActive) {
            var component = data.component;
            var template = null;

            // 若 data 中不存在 options 数据，
            // 那么查看下组件是否有 defaultOptions 配置
            this._initDataOptionsIfNecessary(data);

            if (typeof component.template === "function") {
                template = component.template(this, data);
            } else if (this.options.customRender) {
                if (typeof this.options.customRender === "function") {
                    template = this.options.customRender(this, data);
                } else if (typeof this.options.customRender === "string"
                    && this._isUrl(this.options.customRender)) {
                    template = this._ajaxSyncPost(this.options.customRender, data)
                }
            }

            if (!template) {
                template = this.renderDefault(data);
            }

            var $template = $(template).attr("id", data.elementId).addClass("bsFormItem");

            if (withActive) {
                this._makeElementActive($template, this.currentData.component.disableTools)
            }

            //为 template 配置 id 属性，让 id 的值和 component 的 id 值一致
            //这样，用户点击这个 template div 的时候，才能通过其 id 去查找 component 数据
            return $template[0];
        },


        /**
         * 让某个组件处于选中状态
         * @param elementId
         */
        makeFormItemActive: function (elementId) {
            if (this.currentData && this.currentData.elementId === elementId) {
                return;
            }

            this.currentData = this.getDataByElementId(elementId);

            //个别组件禁止选中
            this.$container.find(".bsFormItem.active").removeClass("active");
            this.$container.find(".bs-item-tools").remove();

            this._makeElementActive($("#" + elementId), this.currentData.component.disableTools)
            this.refreshPropsPanel();
        },


        /**
         * 让 element 高亮
         * @param $el
         * @param disableTools
         */
        _makeElementActive: function ($el, disableTools) {
            if (disableTools !== true) {
                $el.append('<div class="bs-item-tools">' +
                    '               <i class="bi bi-stickies-fill bs-item-copy" title="复制"></i>' +
                    '               <i class="bi bi-trash-fill bs-item-del" title="删除"></i>' +
                    '           </div>')
                    .addClass('active');
            } else {
                $el.addClass('active');
            }
        },

        /**
         * 删除组件
         * @param id
         */
        deleteFormItem: function (elementId) {

            if (this.currentData && this.currentData.elementId === elementId) {
                this.currentData = null;
                this.refreshPropsPanel();
            }

            var data = this.getDataByElementId(elementId);
            if (!data) {
                return;
            }


            this._invokeComponentOnDelete(data);
            delete data;

            this.removeDataByElementId(elementId);
            $("#" + elementId).remove();

        },


        /**
         * 复制组件
         * @param elementId
         */
        copyFormItem: function (elementId) {
            var orignalData = this.getDataByElementId(elementId);
            if (!orignalData) {
                return;
            }

            //复制数据，并重新初始化数据的 elementId 和 id 属性
            var newData = this.deepCopy(orignalData, true);

            //通过 data 来渲染 html
            var newEL = this.render(newData, false);

            //复制的 element
            var $orignalElement = $("#" + elementId);

            this._invokeComponentOnAddBefore(newData, newEL)
            $orignalElement.after(newEL);
            this._invokeComponentOnAdd(newData);


            //追加数据到 array 里
            var parentArray = this.getParentArrayByElementId(elementId);
            parentArray.push(newData);

            this.refreshDataIndex($orignalElement.parent())
        },


        /**
         * 根据 elementId 来查找容器里的 data 数据
         * @param elementId
         */
        getDataByElementId: function (elementId) {
            return this.getDataByElementIdInArray(this.datas, elementId);
        },


        /**
         * 根据 elementId 来查询
         * @param array
         * @param elementId
         * @returns {null|*}
         */
        getDataByElementIdInArray: function (array, elementId) {
            if (!array || array.length === 0) {
                return null;
            }

            for (let item of array) {
                if (item && item.elementId === elementId) {
                    return item;
                } else if (item.children && typeof item.children === "object") {
                    for (let childArray of Object.values(item.children)) {
                        let ret = this.getDataByElementIdInArray(childArray, elementId);
                        if (ret) return ret;
                    }
                }
            }

            return null;
        },

        /**
         * 根据 elementId 来删除 data 里的数据
         * @param elementId
         */
        removeDataByElementId: function (elementId) {
            return this.removeDataByElementIdInArray(this.datas, elementId);
        },


        /**
         * 根据 elementId 来删除梳理里的 data 数据
         * @param array 要删除的数组
         * @param elementId 根据 elementId 来删除
         */
        removeDataByElementIdInArray: function (array, elementId) {
            if (!array || array.length === 0) {
                return;
            }

            for (let i = 0; i < array.length; i++) {
                let item = array[i];
                if (item && item.elementId === elementId) {
                    array.splice(i, 1);
                    break;
                } else if (item.children && typeof item.children === "object") {
                    for (let childArray of Object.values(item.children)) {
                        this.removeDataByElementIdInArray(childArray, elementId);
                    }
                }
            }
        },


        /**
         * 根据 elementId 来获取其所在的数组
         * @param elementId
         * @returns {*}
         */
        getParentArrayByElementId: function (elementId) {
            return this.getParentArrayByElementIdInArray(this.datas, elementId);
        },


        /**
         * 根据 elementId 来获取其所在的数组
         * @param array
         * @param elementId
         */
        getParentArrayByElementIdInArray: function (array, elementId) {
            if (!array || array.length === 0) {
                return null;
            }

            for (let i = 0; i < array.length; i++) {
                let item = array[i];
                if (item && item.elementId === elementId) {
                    return array;
                } else if (item.children && typeof item.children === "object") {
                    for (let childArray of Object.values(item.children)) {
                        let ret = this.getParentArrayByElementIdInArray(childArray, elementId);
                        if (ret) return ret;
                    }
                }
            }

            return null;
        },

        /**
         * 刷新 data 的 index 数据
         * @param $parentEl
         */
        refreshDataIndex: function ($parentEl) {
            var bsFormBuilder = this;
            $parentEl.children(".bsFormItem").each(function (index, item) {
                var id = $(item).attr("id");
                var data = bsFormBuilder.getDataByElementId(id);
                data['index'] = index;
            })
        },


        /**
         * 刷新右侧的属性面板
         */
        refreshPropsPanel: function () {
            //销毁旧的 sortable
            var oldSortable = this.$propsPanel.children(".options").data("sortable");
            if (oldSortable) oldSortable.destroy();


            //清空属性面板里的 html 内容
            this.$propsPanel.html('');


            if (!this.currentData) {
                return;
            }

            let component = this.currentData.component;

            //配置 title 和 icon
            if (this.$propsPanelTitle.length > 0) {
                this.$propsPanelTitle.find(".text").text(component.name);
                if (component.drag.icon) {
                    var icon = "";
                    if (component.drag.icon && component.drag.icon.trim().charAt(0) === '<') {
                        icon = component.drag.icon;
                    } else {
                        icon = '<i class="' + component.drag.icon + '">';
                    }
                    this.$propsPanelTitle.find(".icon").html(icon);
                } else {
                    this.$propsPanelTitle.find(".icon").html('');
                }
            }


            //组件自定义了自己的属性渲染方法
            if (typeof component.onRenderPropsPanel === "function") {
                var html = component.onRenderPropsPanel(this);
                this.$propsPanel.append(html);
                return;
            }

            //获取组件的所有属性
            var allProps = this._getComponentAllProps(component, this.currentData);

            for (let prop of allProps) {

                var template = this._getPropTemplateByType(prop.type);

                //支持没有模板的属性，有些属性不允许通过属性面板去配置
                //此时，可以配置这个 prop 的 type 为 none
                if (template) {
                    var newProp = this.deepCopy(prop, false);
                    newProp["id"] = this.genRandomId();

                    var value = this.currentData[prop.name];
                    if (typeof value === "undefined") {
                        value = prop.defaultValue;
                    }
                    newProp["value"] = value;

                    // var htmlString = this.renderPropTemplate(newProp, this.currentData, template);
                    // this.$propsPanel.append(htmlString);

                    this._appendPropsPanel(newProp, this.currentData, template);
                }
            }

            // 渲染 options 功能
            if (this.currentData.component.withOptions || this.currentData.options) {

                // 若 data 中不存在 options 数据，
                // 那么查看下组件是否有 defaultOptions 配置
                this._initDataOptionsIfNecessary(this.currentData);

                //组件支持的 options 类型
                var componentSupportTypes = this.currentData.component.optionsTypes;
                if (typeof componentSupportTypes === "string") {
                    componentSupportTypes = [componentSupportTypes];
                } else if (typeof componentSupportTypes === "function") {
                    componentSupportTypes = this.currentData.component.optionsTypes(this, this.currentData);
                }

                // 默认支持 custom 和 datasource
                if (!componentSupportTypes) {
                    componentSupportTypes = ['custom', 'datasource'];
                }

                //若 options 配置了数据源，那么显示 选项类型 让用户自己选择
                var datasources = this._getOptionDatasources();
                if (datasources && datasources.length > 0 && componentSupportTypes.length > 1) {
                    let prop = {
                        id: this.genRandomId(),
                        options: [{text: '自定义', value: 'custom'}, {text: '数据源', value: 'datasource'}],
                        label: "选项类型",
                        name: "optionsType",
                        value: this.currentData["optionsType"],
                    }

                    let template = this._getPropTemplateByType("select");
                    this._appendPropsPanel(prop, this.currentData, template);

                    // let htmlString = this.renderPropTemplate(prop, this.currentData, template);
                    // this.$propsPanel.append(htmlString);
                }


                //通过数据源的方式来渲染 options
                if (this.currentData.optionsType === "datasource") {
                    let prop = {
                        id: this.genRandomId(),
                        options: datasources || [{text: '未定义任何数据源', value: ''}],
                        label: "数据源",
                        name: "optionsDatasource",
                        value: this.currentData["optionsDatasource"],
                    }

                    let template = this._getPropTemplateByType("select");
                    this._appendPropsPanel(prop, this.currentData, template);
                    // let htmlString = this.renderPropTemplate(prop, this.currentData, template);
                    // this.$propsPanel.append(htmlString);
                }
                //通过 sortable 的方式来渲染 options
                else {
                    let prop = {
                        id: this.genRandomId(),
                        options: this.currentData.options || [],
                        title: this.currentData.optionsTitle || "选项",
                    }

                    let template = this._getPropTemplateByType("options");
                    this._appendPropsPanel(prop, this.currentData, template);
                    // let htmlString = this.renderPropTemplate(prop, this.currentData, template);
                    // this.$propsPanel.append(htmlString);

                    this._initOptionsSortable();
                }
            }
        },


        /**
         * 追加 html 内容到属性面板
         * @param prop
         * @param data
         * @param template
         * @private
         */
        _appendPropsPanel: function (prop, data, template) {
            let htmlString = this.renderPropTemplate(prop, data, template);

            if (this.options.bsFormPropsItemAppendBefore && typeof this.options.bsFormPropsItemAppendBefore === "function") {
                htmlString = this.options.bsFormPropsItemAppendBefore(this, prop, data, htmlString);
            }

            this.$propsPanel.append(htmlString);

            if (this.options.bsFormPropsItemAppended && typeof this.options.bsFormPropsItemAppended === "function") {
                this.options.bsFormPropsItemAppended(this, prop, data);
            }
        },

        /**
         * 获取 component 所有的属性
         * @param component
         * @param data
         * @private
         */
        _getComponentAllProps: function (component, data) {
            //所有默认属性
            var defaultProps = this.deepCopy(this.defaultProps, false);

            //组件定义使用的属性
            let useProps = typeof component.useProps === "function"
                ? component.useProps(this, data)
                : (typeof component.useProps === "object" ? component.useProps : []);


            if (useProps && useProps.length > 0) {
                var index = defaultProps.length;
                while (index--) {
                    if (useProps.indexOf(defaultProps[index].name) < 0) {
                        defaultProps.splice(index, 1);
                    }
                }
            }

            //组件定义的 "私有" 属性
            var componentProps = typeof component.props === "object" ?
                component.props : [];


            // 全部属性
            var allProps = this._mergeProps(componentProps, defaultProps);
            allProps.sort((a, b) => a.index - b.index);


            //过滤属性
            if (this.options.bsFormPropsFilter && typeof this.options.bsFormPropsFilter === "function") {
                this.options.bsFormPropsFilter(allProps, data, this)
            }

            return allProps;
        },

        /**
         * 通过数据源分组名称，获取数据源
         * @returns {string}
         * @private
         */
        _getOptionDatasources: function () {
            var datasources = this.options.optionsDatasources;
            if (typeof datasources === "function") {
                datasources = datasources(this, this.currentData);
            } else if (typeof datasources === "string" && this._isUrl(datasources)) {
                var resp = this._ajaxSyncGet(datasources);

                //后台返回的内容应该是：
                //{
                //   otherKey:....
                //   datasources:[]
                // }
                if (resp && resp["datasources"]) {
                    datasources = resp["datasources"];
                }
            }
            return datasources;
        },


        /**
         * 获取数据源里定义的 options 的内容
         * @param options
         * @returns {*}
         * @private
         */
        _parseOptions: function (options) {
            if (typeof options === "object" && Array.isArray(options)) {
                return options;
            }

            if (typeof options === "function") {
                return options(this, this.currentData);
            }

            if (typeof options === "string" && this._isUrl(options)) {
                var resp = this._ajaxSyncGet(options);

                //后台返回的内容应该是：
                //{
                //   otherKey:....
                //   options:[]
                // }

                if (resp && resp['options']) {
                    return resp['options']
                }
            }
        },

        /**
         * 合并 componentProps 和 defaultProps
         * componentProps 优先级大于 defaultProps
         * @param componentProps
         * @param defaultProps
         * @private
         */
        _mergeProps: function (componentProps, defaultProps) {
            var allProps = [].concat(defaultProps);

            for (let componentProp of componentProps) {
                var isOverride = false;
                for (let i = 0; i < defaultProps.length; i++) {
                    var defaultProp = allProps[i];
                    if (defaultProp.name === componentProp.name) {
                        allProps[i] = componentProp;
                        isOverride = true;
                    }
                }
                if (!isOverride) {
                    allProps.push(componentProp)
                }
            }

            return allProps;
        },

        /**
         * 初始化 Options 的 dragable 组件
         */
        _initOptionsSortable: function () {
            var $optionsEl = this.$propsPanel.children(".options");
            if ($optionsEl.length > 0) {
                var sortable = new Sortable($optionsEl[0], {
                    handle: '.option-handle', // handle's class
                    filter: '.filtered', // 'filtered' class is not draggable
                    animation: 150,
                    onEnd: () => this._syncCurrentDataOptionsFromPropSetting(),
                });
                $optionsEl.data("sortable", sortable);
            }
        },


        /**
         * 渲染属性模板
         * @param prop
         * @param data
         * @param template
         * @returns {*}
         */
        renderPropTemplate: function (prop, data, template) {

            //保证所有的 prop 必须包含着几个属性
            var propData = $.extend({
                disabled: false, required: false, id: "", label: "", name: "", placeholder: "", type: "", value: "",
            }, prop);

            var paras = ["$builder", "$prop", "$data"].concat(Object.keys(propData));

            var values = paras.map(k => prop[k] || "");
            values[0] = this;
            values[1] = prop;
            values[2] = data;

            return this._renderTemplate(template, paras, values);
        },


        /**
         * 导出 json
         */
        exportToJson: function () {
            var exportData = this.deepCopy(this.datas, false);
            this._arrangeExportData(exportData);
            return JSON.stringify(exportData);
        },


        /**
         * 导出 html
         */
        exportToHtml: function () {
            var exportData = this.deepCopy(this.datas, false);
            //根据 index 对 dataArray 进行升序排序，越小越靠前
            exportData.sort((a, b) => a.index - b.index);

            var html = "";
            for (let data of exportData) {
                html += this.render(data, false).outerHTML;
            }
            return html;
        },


        /**
         * 整理导出数据
         * 1、删除 component 数据
         * 2、删除 element 数据
         * 3、对 dataArray 根据 index 进行排序
         * @param array
         * @private
         */
        _arrangeExportData: function (array) {
            if (!array || array.length === 0) {
                return;
            }


            //根据 index 对 dataArray 进行升序排序
            //越小越靠前
            array.sort((a, b) => a.index - b.index);

            for (let data of array) {
                delete data.component;
                delete data.elementId;
                delete data.customOptions;

                //remove data.options elementId
                if (data.options && Array.isArray(data.options)) {
                    this._arrangeExportData(data.options);
                }

                if (data.children) {
                    for (let arr of Object.values(data.children)) {
                        this._arrangeExportData(arr);
                    }
                }
            }
        },

        /**
         * 获取 datas 数据，并可以对其进行修改
         */
        getDatas: function () {
            return this.datas;
        },

        /**
         * 添加 data 数据到 跟节点容器
         * @param data
         */
        addDataToRoot: function (data) {
            this.addDatasToRoot([data]);
        },

        /**
         * 添加一个 data 数组到跟节点容器
         * @param array
         */
        addDatasToRoot: function (array) {
            this._initData(array, true);

            if (this.isBuilderMode()) {
                this._refreshBuilderContainer();
            } else if (this.isViewMode()) {
                this._refreshViewContainer();
            }
        },
        /**
         * 添加 data 数据到子容器节点
         * @param data
         */
        addDataToContainer: function (data, containerElementId, index) {
            this.addDatasToContainer([data], containerElementId, index);
        },

        /**
         * 添加一个 data 数组到子容器节点
         * @param array
         */
        addDatasToContainer: function (array, containerElementId, index) {
            this._initData(array, false);

            var parentData = this.getDataByElementId(containerElementId);
            if (!parentData) {
                console.error("Can not find data by elementId: " + containerElementId);
                return;
            }

            if (!parentData.children) {
                parentData.children = {};
            }

            if (!parentData.children[index]) {
                parentData.children[index] = [];
            }

            parentData.children[index].push(...array);

            this.refreshDataElement(parentData);
        },

        /**
         * 刷新数据到 html 显示
         * @param data
         */
        refreshDataElement: function (data) {
            var newEl = this.render(data, true);

            var $oldEl = $("#" + data.elementId);
            $oldEl.find('.bsItemContainer').each(function () {
                let sortable = $(this).data('bsItemSortable');
                if (sortable) sortable.destroy();
            });

            this._invokeComponentOnAddBefore(data, newEl);
            $oldEl.replaceWith(newEl);
            this._invokeComponentOnAdd(data);
        },

        /**
         * 更新 data 属性，并同步到 html
         * @param data
         * @param attr
         * @param value
         */
        updateDataAttr: function (data, attr, value) {
            if (!data) {
                console.error("data must not be null.");
                return;
            }

            if (value === "true") value = true;
            else if (value === "false") value = false;


            var oldValue = data[attr]


            //更新数据源时，需要同时更新数据源自带的 options
            if (attr === "optionsDatasource") {
                var datasources = this._getOptionDatasources();
                var optionsInDatasource = null;
                if (datasources && datasources.length > 0) {
                    for (let datasource of datasources) {
                        if (datasource.value.toString() === value) {
                            optionsInDatasource = this._parseOptions(datasource.options);
                        }
                    }
                }
                this.updateDataAttr(data, "options", optionsInDatasource)
            }


            //当前组件定义了 onPropChange 监听方法，并且该方法执行成功了
            //那么，可以理解为该方法会去更新 html 内容，而不通过系统继续渲染了
            if (data.component && typeof data.component.onPropChange === "function"
                && data.component.onPropChange(this, data, attr, value)) {
                data[attr] = value;
            }

            //配置 onDataChange 方法
            else if (this.options.onDataChange && typeof this.options.onDataChange === "function"
                && this.options.onDataChange(this, data, attr, value)) {
                data[attr] = value;
            }

            //默认行为，更新数据并刷新设计面板的 element
            else {
                data[attr] = value;
                this.refreshDataElement(data);
            }

            // 更新：选项类型（数据源 或者 自定义），更新这个属性后，需要刷新属性面板
            if ("optionsType" === attr && data === this.currentData) {
                // 更新数据类型为：数据源，则需要刷新设计面板里元素（element）的 "选项" 列表
                if (value === "datasource") {
                    this._cacheCustomOptions(data);
                    var optionsDatasource = data.optionsDatasource || this._getOptionDatasources()[0].value;
                    this.updateDataAttr(data, "optionsDatasource", optionsDatasource);
                } else {
                    this._resumeCustomOptions(data);
                }

                this.refreshPropsPanel();
            }

            if (typeof this.options.onDataChanged === "function") {
                this.options.onDataChanged(data, attr, value, oldValue);
            }
        },


        /**
         * 缓存 options 数据到 customOptions，防止下次切换的时候造成数据丢失
         * @param data
         * @private
         */
        _cacheCustomOptions: function (data) {
            this.updateDataAttr(data, "customOptions", data.options)
        },


        /**
         * 恢复（copy） customOptions 数据到 options
         * @param data
         * @private
         */
        _resumeCustomOptions: function (data) {
            this.updateDataAttr(data, "options", data.customOptions)
            this.updateDataAttr(data, "customOptions", null)
        },


        /**
         * 是否是视图模式
         */
        isViewMode: function () {
            return this.options.mode === "view";
        },


        /**
         * 是否是构建工具模式
         */
        isBuilderMode: function () {
            return this.options.mode === "builder";
        },


        /**
         * 清空组件
         */
        clear: function () {
            this.datas = [];
            this._refreshBuilderContainer();

            this.currentData = null;
            this.refreshPropsPanel();
        },


        /**
         * 销毁整个组件
         */
        destroy: function () {
            this.options = null;
            this.defaultProps = null;
            this.propTemplates = null;
            this.useComponents = null;
            this.$container = null;
            this.$containerPlaceHolder = null;
            this.$propsPanel = null;
            this.components = null;
            this.datas = null;
            this.currentData = null;
            this.$rootEl.data('bsFormBuilder', null);
            this.$rootEl.html('');
            this.$rootEl = null;
        },

    }


    $.fn.bsFormBuilder = function (option) {
        var arg = arguments,
            options = typeof option == 'object' && option;

        return this.each(function () {
            var $this = $(this),
                bsFormBuilder = $this.data('bsFormBuilder');

            if (!bsFormBuilder) {
                $this.data('bsFormBuilder', (bsFormBuilder = new BsFormBuilder(this, options)));
            }
            if (typeof option === 'string') {
                var method = bsFormBuilder[option];
                if (!method) {
                    console.error("bsFormBuilder has not the method: " + option);
                    return;
                }
                if (arg.length > 1) {
                    method.apply(bsFormBuilder, Array.prototype.slice.call(arg, 1));
                } else {
                    method();
                }
            }
        });
    }
})(jQuery);
