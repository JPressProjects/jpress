(function() {
    "use strict";
    var match = /(\{.*\})/.exec(document.body.innerHTML);
    if (match) {
        parent.postMessage(match[1], "*");
    }
}());
