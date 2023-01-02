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
        "props": [
            {
                name: "placeholder",
                type: "input",
                label: "占位符",
                placeholder: "",
                disabled: false,
                required: false,
            },
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="text" class="form-control" name="{{name}}" id="{{id}}"' +
            '        placeholder="{{placeholder}}" value="{{value}}"  {{required ? "required":""}}/>' +
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
            },
            {
                name: "placeholder",
                type: "input",
                label: "占位符",
                placeholder: "",
                disabled: false,
                required: false,
            },
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="{{id}}">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <textarea name="{{name}}" class="form-control" id="{{id}}" rows="{{rows}}"' +
            '        placeholder="{{placeholder}}" {{required ? "required":""}}>{{value}}</textarea>' +
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
        "useProps": ["tag", "id"],
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
        "useProps": ["tag", "id"],
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
            '  <blockquote id="{{id}}">{{content}}</blockquote>' +
            '  </div>' +
            '</div>',
    },



    //分割线
    {
        "name": "分割线",
        "tag": "subtraction",
        "drag": {
            "title": "分割线",
            "type": "assist",
            "index": 100,
            "icon": "bi bi-arrows-expand"
        },
        "useProps": ["tag", "id"],
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
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <hr class="{{className}}" style="{{style}}" />' +
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
            '      <select class="custom-select" name="{{name}}" id="{{id}}" {{required ? "required":""}}>' +
            '        {{~for(let option of options)}}' +
            '        <option value="{{option.value}}">{{option.text}}</option>' +
            '        {{~end}}' +
            '      </select>' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },



    //滑块
    {
        "name": "滑块",
        "tag": "range",
        "drag": {
            "title": "滑块",
            "type": "base",
            "index": 100,
            "icon": "bi bi-sliders"
        },
        "props": [
            {
                name: "max",
                type: "number",
                label: "最大值",
                defaultValue: 10,
            },
            {
                name: "min",
                type: "number",
                label: "最小值",
                defaultValue: 1,
            },
            {
                name: "step",
                type: "number",
                label: "步长",
                defaultValue: 1,
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="range" class="form-control" min="{{min}}" max="{{max}}" step="{{step}}" name="{{name}}" id="{{id}}"' +
            '        placeholder="{{placeholder}}" value="{{value}}" {{required ? "required":""}}/>' +
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
            '      <input type="time" class="form-control" name="{{name}}" id="{{id}}"' +
            '        placeholder="{{placeholder}}" value="{{value}}" {{required ? "required":""}}/>' +
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
            "icon": "bi bi-calendar3"
        },
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="date" class="form-control" name="{{name}}" id="{{id}}" value="{{value}}" {{required ? "required":""}}/>' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },


    //日期和时间
    {
        "name": "日期和时间",
        "tag": "datetime",
        "drag": {
            "title": "日期和时间",
            "type": "base",
            "index": 100,
            "icon": "bi bi-calendar2-day"
        },
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '      <input type="datetime-local" class="form-control" name="{{name}}" id="{{id}}" value="{{value}}" {{required ? "required":""}}/>' +
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
            '        <input type="checkbox" class="custom-control-input" name="{{name}}" id="{{id}}" />' +
            '        <label class="custom-control-label" for="{{id}}"></label>' +
            '      </div>' +
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
            '      <input type="file" class="custom-file-input" name="{{name}}" id="{{id}}" {{required ? "required":""}}/>' +
            '      <label class="custom-file-label" for="{{id}}">{{placeholder}}</label>' +
            '    </div>' +
            '  </div>' +
            '</div>',
    },
    // 图片上传
    {
        "name": "图片上传",
        "tag": "image-upload",
        "drag": {
            "title": "图片上传",
            "type": "base",
            "index": 100,
            "icon": "bi bi-images"
        },
        props: [
            {
                name: "count",
                type: "number",
                label: "数量限制",
                placeholder: "最多允许上传的图片数量",
                defaultValue: 3,
                disabled: false,
                required: false,
            }
        ],
        "template": '<div class="bsFormItem">' +
            '  <div class="form-group clearfix">' +
            '    <div class="form-label-left">' +
            '      <label for="label">{{label}}</label>' +
            '    </div>' +
            '    <div class="flex-auto">' +
            '       <div class="uploadList" data-url="{{url}}">' +
            '           <div class="bsForm-upload-list">' +
            // '               <div class="bsForm-upload-item">' +
            // '                   <img src="static/images/" alt="">' +
            // '                   <span class="bsFormDelete"><i class="bi bi-trash"></i></span>' +
            // '               </div>' +
            '               <button type="button" class="bsForm-upload-btn">' +
            '                   <i class="bi bi-plus-lg bsForm-icon"></i>' +
            '                   <p>点击上传图片</p> '+
            '                       <input class="bsForm-upload-file" type="file" accept="image/gif,image/jpeg,image/jpg,image/png,image/svg" name="{{name}}" multiple="true" id="{{id}}">' +
            '               </button>' +
            '           </div>' +
            '      </div>' +
            '    </div>' +
            '</div>',
    },
]);

if (window.bsComponentsDef) {
    window.bsComponentsDef.push(...componentsDef);
} else {
    window.bsComponentsDef = componentsDef;
}

