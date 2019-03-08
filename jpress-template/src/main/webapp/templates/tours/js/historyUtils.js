var historyUtils = {
	add : function (url) {
        var historyArray = historyUtils.getLocal();
        if (!historyArray) {
            historyArray = [];
        }
        var currentPage = historyArray.pop();
        if (currentPage && currentPage == url) {
            //do nothing
        } else if (currentPage){
            historyArray.push(currentPage); //历史里面没有现在传入的url，在加回去
        }
        historyArray.push(url);
        historyUtils.saveLocal(historyArray);
    },
    back : function() {
        var historyArray = historyUtils.getLocal();
        var currentPage = historyArray.pop();//去掉当前页面，pop取最后，类似stack
        var history = historyArray.pop();
        if (!history) {//没有历史页面
            historyUtils.add(currentPage);//将当前页面加入回数组中
            return;
        }
        historyUtils.saveLocal(historyArray);
        window.location.href = history;
    },
    getLocal : function() {
        var result = window.sessionStorage.getItem(historyUtils.key);
        if (!result) {
            return null;
        }
        return JSON.parse(result);
    },
    saveLocal : function(data) {
        window.sessionStorage.setItem(historyUtils.key, JSON.stringify(data));
    },
    init : function() {
        historyUtils.saveLocal([]);
    },
    key : "_history_"
};

historyUtils.add(window.location.href);