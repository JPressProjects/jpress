CKEDITOR.dialog.add( 'html5audio', function( editor ) {
    return {
        title: editor.lang.html5audio.title,
        minWidth: 500,
        minHeight: 100,
        contents: [ {
            id: 'info',
            label: editor.lang.html5audio.infoLabel,
            elements: [ {
                type: 'vbox',
                padding: 0,
                children: [ {
                    type: 'hbox',
                    widths: [ '365px', '110px' ],
                    align: 'right',
                    children: [ {
                        type: 'text',
                        id: 'url',
                        label: editor.lang.common.url,
                        required: true,
                        validate: CKEDITOR.dialog.validate.notEmpty( editor.lang.html5audio.urlMissing ),
                        setup: function( widget ) {
                            this.setValue( widget.data.src );
                        },
                        commit: function( widget ) {
                            widget.setData( 'src', this.getValue() );
                        }
                    },
                    {
                        type: 'button',
                        id: 'browse',
                        // v-align with the 'txtUrl' field.
                        // TODO: We need something better than a fixed size here.
                        style: 'display:inline-block;margin-top:14px;',
                        align: 'center',
                        label: editor.lang.common.browseServer,
                        hidden: true,
                        filebrowser: 'info:url'
                    } ]
                } ]
            },
            {
                type: 'hbox',
                id: 'alignment',
                children: [ {
                    type: 'radio',
                    id: 'align',
                    label: editor.lang.common.align,
                    items: [
                        [editor.lang.common.alignCenter, 'center'],
                        [editor.lang.common.alignLeft, 'left'],
                        [editor.lang.common.alignRight, 'right'],
                        [editor.lang.common.alignNone, 'none']
                    ],
                    'default': 'center',
                    setup: function( widget ) {
                        if ( widget.data.align ) {
                            this.setValue( widget.data.align );
                        }
                    },
                    commit: function( widget ) {
                        widget.setData( 'align', this.getValue() );
                    }
                } ]
            } ]
        },
        {
            id: 'Upload',
            hidden: true,
            filebrowser: 'uploadButton',
            label: editor.lang.html5audio.upload,
            elements: [ {
                type: 'file',
                id: 'upload',
                label: editor.lang.html5audio.btnUpload,
                style: 'height:40px',
                size: 38
            },
            {
                type: 'fileButton',
                id: 'uploadButton',
                filebrowser: 'info:url',
                label: editor.lang.html5audio.btnUpload,
                'for': [ 'Upload', 'upload' ]
            } ]
        },
        {
            id: 'advanced',
            label: editor.lang.html5audio.advanced,
            elements: [ {
                type: 'vbox',
                padding: 0,
                children: [ {
                    type: 'hbox',
                    children: [ {
                        type: 'radio',
                        id: 'autoplay',
                        label: editor.lang.html5audio.autoplay,
                        items: [
                            [editor.lang.html5audio.yes, 'yes'],
                            [editor.lang.html5audio.no, 'no']
                        ],
                        'default': 'no',
                        setup: function( widget ) {
                            if ( widget.data.autoplay ) {
                                this.setValue( widget.data.autoplay );
                            }
                        },
                        commit: function( widget ) {
                            widget.setData( 'autoplay', this.getValue() );
                        }
                    },
                    {
                        type: 'radio',
                        id: 'allowdownload',
                        label: editor.lang.html5audio.allowdownload,
                        items: [
                            [editor.lang.html5audio.yes, 'yes'],
                            [editor.lang.html5audio.no, 'no']
                        ],
                        'default': 'no',
                        setup: function( widget ) {
                            if ( widget.data.allowdownload ) {
                                this.setValue(widget.data.allowdownload);
                            }
                        },
                        commit: function( widget ) {
                            widget.setData( 'allowdownload', this.getValue() );
                        }
                    } ]
                } ]
            } ]
        } ]
    };
} );
