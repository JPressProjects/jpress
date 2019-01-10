CKEDITOR.plugins.add( 'html5audio', {
    requires: 'widget',
    lang: 'bg,ca,de,el,en,eu,es,fr,ru,uk,uz,zh-cn,fa',
    icons: 'html5audio',
    init: function( editor ) {
        editor.widgets.add( 'html5audio', {
            button: editor.lang.html5audio.button,
            template: '<div class="ckeditor-html5-audio"></div>',   // We add the audio element when needed in the data function, to avoid having an undefined src attribute.
                                                                    // See issue #9 on github: https://github.com/iametza/ckeditor-html5-audio/issues/9
            editables: {},
            /*
             * Allowed content rules (http://docs.ckeditor.com/#!/guide/dev_allowed_content_rules):
             *  - div-s with text-align,float,margin-left,margin-right inline style rules and required ckeditor-html5-audio class.
             *  - audio tags with src and controls attributes.
             */
            allowedContent: 'div(!ckeditor-html5-audio){text-align,float,margin-left,margin-right}; audio[src,controls,controlslist,autoplay];',
            requiredContent: 'div(ckeditor-html5-audio); audio[src,controls];',
            upcast: function( element ) {
                return element.name === 'div' && element.hasClass( 'ckeditor-html5-audio' );
            },
            dialog: 'html5audio',
            init: function() {
                var src = '';
                var autoplay = '';
                var align = this.element.getStyle( 'text-align' );

                // If there's a child (the audio element)
                if ( this.element.getChild( 0 ) ) {
                    // get it's attributes.
                    src = this.element.getChild( 0 ).getAttribute( 'src' );
                    autoplay = this.element.getChild( 0 ).getAttribute( 'autoplay' );
                    allowdownload = !this.element.getChild( 0 ).getAttribute( 'controlslist' );
                }

                if ( src ) {
                    this.setData( 'src', src );

                    if ( align ) {
                        this.setData( 'align', align );
                    } else {
                        this.setData( 'align', 'none' );
                    }

                    if ( autoplay ) {
                        this.setData( 'autoplay', 'yes' );
                    }

                    if ( allowdownload ) {
                        this.setData( 'allowdownload', 'yes' );
                    }
                }
            },
            data: function() {
                // If there is an audio source
                if ( this.data.src ) {
                    // and there isn't a child (the audio element)
                    if ( !this.element.getChild( 0 ) ) {
                        // Create a new <audio> element.
                        var audioElement = new CKEDITOR.dom.element( 'audio' );
                        // Set the controls attribute.
                        audioElement.setAttribute( 'controls', 'controls' );
                        // Append it to the container of the plugin.
                        this.element.append( audioElement );
                    }
                    this.element.getChild( 0 ).setAttribute( 'src', this.data.src );
                }

                this.element.removeStyle( 'float' );
                this.element.removeStyle( 'margin-left' );
                this.element.removeStyle( 'margin-right' );

                if ( this.data.align === 'none' ) {
                    this.element.removeStyle( 'text-align' );
                } else {
                    this.element.setStyle( 'text-align', this.data.align );
                }

                if ( this.data.align === 'left' ) {
                    this.element.setStyle( 'float', this.data.align );
                    this.element.setStyle( 'margin-right', '10px' );
                } else if ( this.data.align === 'right' ) {
                    this.element.setStyle( 'float', this.data.align );
                    this.element.setStyle( 'margin-left', '10px' );
                }

                if ( this.element.getChild( 0 ) ) {
                    if ( this.data.autoplay === 'yes' ) {
                        this.element.getChild( 0 ).setAttribute( 'autoplay', 'autoplay' );
                    } else {
                        this.element.getChild( 0 ).removeAttribute( 'autoplay' );
                    }
                    if ( this.data.allowdownload === 'yes' ) {
                        this.element.getChild( 0 ).removeAttribute( 'controlslist' );
                    } else {
                        this.element.getChild( 0 ).setAttribute( 'controlslist', 'nodownload' );
                    }
                }
            }
        } );

        if ( editor.contextMenu ) {
            editor.addMenuGroup( 'html5audioGroup' );
            editor.addMenuItem( 'html5audioPropertiesItem', {
                label: editor.lang.html5audio.audioProperties,
                icon: 'html5audio',
                command: 'html5audio',
                group: 'html5audioGroup'
            });

            editor.contextMenu.addListener( function( element ) {
                if ( element &&
                     element.getChild( 0 ) &&
                     element.getChild( 0 ).hasClass &&
                     element.getChild( 0 ).hasClass( 'ckeditor-html5-audio' ) ) {
                    return { html5audioPropertiesItem: CKEDITOR.TRISTATE_OFF };
                }
            });
        }

        CKEDITOR.dialog.add( 'html5audio', this.path + 'dialogs/html5audio.js' );
    }
} );
