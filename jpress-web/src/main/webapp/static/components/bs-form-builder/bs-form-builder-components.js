/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

let componentsDef = [];
componentsDef.push(...[

    //单行输入框定义
    {
        "name": "输入框",
        "tag": "input",
        "drag": {
            "title": "输入框",
            "type": "base",
            "index": 100,
            "icon": "bi bi-terminal"
        },
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="text" class="form-control" id="{{id}}"' +
            '        placeholder="{{placeholder}}" value="{{value}}" />' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },

    //多行输入框
    {
        "name": "多行输入框",
        "tag": "textarea",
        "drag": {
            "title": "多行输入框",
            "type": "base",
            "index": 100,
            "icon": "bi bi-textarea-resize"
        },
        "props": [
            {
                name: "rows",
                type: "number",
                label: "行数",
                placeholder: "请输入行数...",
                defaultValue: 3,
                disabled: false,
                required: true,
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="{{id}}">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <textarea name="{{name}}" class="form-control" id="{{id}}" rows="{{rows}}"' +
            '        placeholder="{{placeholder}}" >{{value}}</textarea>' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },

    //提示
    {
        "name": "提示",
        "tag": "tips",
        "drag": {
            "title": "提示",
            "type": "assist",
            "index": 100,
            "icon": "bi bi-info-circle"
        },
        "propsfilter": ["tag", "id"],
        props: [
            {
                name: "title",
                type: "input",
                label: "提示内容",
                placeholder: "提示内容...",
                disabled: false,
                required: true,
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <i class="d-inline-block bi bi-exclamation-circle"' +
            '      data-toggle="tooltip"' +
            '      data-placement="right"' +
            '      title="{{title}}"' +
            '    ></i>' +
            '  </div>' +
            '</div>',
    },

    //空DIV
    {
        "name": "空DIV",
        "tag": "empty-div",
        "drag": {
            "title": "空DIV",
            "type": "assist",
            "index": 100,
            "icon": "bi bi-fullscreen"
        },
        "propsfilter": ["tag", "id"],
        props: [
            {
                name: "height",
                type: "number",
                label: "高度",
                disabled: false,
                required: true,
                defaultValue: 20
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix" style="height: {{height}}px">' +
            '  </div>' +
            '</div>',
    },

    //引用
    {
        "name": "引用",
        "tag": "blockquote",
        "drag": {
            "title": "引用",
            "type": "assist",
            "index": 100,
            "icon": "bi bi-quote"
        },
        "propsfilter": ["tag", "id"],
        props: [
            {
                name: "content",
                type: "textarea",
                label: "内容",
                placeholder: "请输入引用内容...",
                disabled: false,
                required: true,
                defaultValue: "这里是引用的内容..."
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '  <blockquote>{{content}}</blockquote>' +
            '  </div>' +
            '</div>',
    },


    //下拉菜单
    {
        "name": "下拉菜单",
        "tag": "select",
        "drag": {
            "title": "下拉菜单",
            "type": "base",
            "index": 100,
            "icon": "bi bi-card-checklist"
        },
        withOptions: true,
        defaultOptions: [
            {
                text: "选项1",
                value: "value1"
            },
            {
                text: "选项2",
                value: "value2"
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <legend class="col-form-label pt-0">{{label}}</legend>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <select class="custom-select">' +
            '        {{~for(let option of options)}}' +
            '        <option value="{{option.value}}">{{option.text}}</option>' +
            '        {{~end}}' +
            '      </select>' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },

    //单选框
    {
        "name": "单选框",
        "tag": "radio",
        "drag": {
            "title": "单选框",
            "type": "base",
            "index": 100,
            "icon": "bi bi-record2"
        },
        withOptions: true,
        defaultOptions: [
            {
                text: "选项1",
                value: "value1"
            },
            {
                text: "选项2",
                value: "value2"
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '        {{~for(let option of options)}}' +
            '      <div class="form-check form-check-inline">' +
            '        <input class="form-check-input" type="radio" name="{{name}}" ' +
            '               id="{{option.value}}-{{id}}" value="{{option.value}}" />' +
            '        <label class="form-check-label" for="{{option.value}}-{{id}}">{{option.text}}</label>' +
            '      </div>' +
            '        {{~end}}' +
            '    </div>' +
            '  </div>' +
            '</div>',

    },


    //复选框
    {
        "name": "复选框",
        "tag": "checkbox",
        "drag": {
            "title": "复选框",
            "type": "base",
            "index": 100,
            "icon": "bi bi-check-square"
        },
        withOptions: true,
        defaultOptions: [
            {
                text: "选项1",
                value: "value1"
            },
            {
                text: "选项2",
                value: "value2"
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '        {{~for(let option of options)}}' +
            '      <div class="form-check form-check-inline">' +
            '        <input class="form-check-input" type="checkbox" name="{{name}}" ' +
            '               id="{{option.value}}-{{id}}" value="{{option.value}}" />' +
            '        <label class="form-check-label" for="{{option.value}}-{{id}}">{{option.text}}</label>' +
            '      </div>' +
            '        {{~end}}' +
            '    </div>' +
            '  </div>' +
            '</div>',

    },

    //开关
    {
        "name": "开关",
        "tag": "switch",
        "drag": {
            "title": "开关",
            "type": "base",
            "index": 100,
            "icon": "bi bi-toggle-on"
        },
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <legend class="col-form-label pt-0">{{label}}</legend>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <div class="custom-control custom-switch">' +
            '        <input type="checkbox" class="custom-control-input" id="{{id}}" />' +
            '        <label class="custom-control-label" for="{{id}}"></label>' +
            '      </div>' +
            '    </div>' +
            '  </div>' +
            '</div>',

    },


    //时间
    {
        "name": "时间",
        "tag": "time",
        "drag": {
            "title": "时间",
            "type": "base",
            "index": 100,
            "icon": "bi bi-clock"
        },
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="time" class="form-control" id="{{id}}"' +
            '        placeholder="{{placeholder}}" value="{{value}}" />' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },


    //时间
    {
        "name": "日期",
        "tag": "date",
        "drag": {
            "title": "日期",
            "type": "base",
            "index": 100,
            "icon": "bi bi-calendar2-date"
        },
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="date" class="form-control" id="{{id}}" value="{{value}}" />' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },

    //文件上传
    {
        "name": "文件上传",
        "tag": "file-upload",
        "drag": {
            "title": "文件上传",
            "type": "base",
            "index": 100,
            "icon": "bi bi-cloud-upload"
        },
        props: [
            {
                name: "placeholder",
                type: "input",
                label: "占位内容",
                defaultValue: "请选择文件",
                disabled: false,
                required: true,
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="file" class="custom-file-input" id="{{id}}" />' +
            '      <label class="custom-file-label" for="{{id}}">{{placeholder}}</label>' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },

]);

if (window.bsComponentsDef) {
    window.bsComponentsDef.push(...componentsDef);
} else {
    window.bsComponentsDef = componentsDef;
}

