//禁止鼠标右键菜单
document.oncontextmenu = function () {
    return false;
};
// 初始化编辑器
initEditor({
    width: "100%",
    height: "100%",
    "last": "last"
});

var isDebug = false;

function debug() {
    isDebug = true;
    var ws = new WebSocket("ws://127.0.0.1:18081/ws/debug");
    ws.onopen = function () {
        console.log("已连接服务器...");
        ws.send(JSON.stringify({
            type: 'normal',
            filePath: '/',
            fileName: './public/tmp.js',
            fucName: 'test'
        }));
    };

    ws.onmessage = function (evt) {
        var data = JSON.parse(evt.data);
        // console.log(data.log);
        // console.log(data.logs);
        if (data.type === "log") {
            var _console;
            // 日志前缀 [2019-08-28  11:52:17.208] [INFO] index.js -
            var logs = [
                "[".concat(data.logTime, "] "),
                "[".concat(data.level.toUpperCase(), "] "),
                data.fileName ? (data.filePath + data.fileName) : "",
                " - "
            ].concat(data.logs);
            switch (data.level) {
                case "trace":
                    (_console = console).trace.apply(_console, logs);
                    break;
                case "debug":
                    (_console = console).debug.apply(_console, logs);
                    break;
                case "info":
                    (_console = console).info.apply(_console, logs);
                    break;
                case "warn":
                    (_console = console).warn.apply(_console, logs);
                    break;
                case "error":
                    (_console = console).error.apply(_console, logs);
                    break;
                default:
                    (_console = console).log.apply(_console, logs);
            }
        }
    };

    ws.onclose = function (evt) {
        isDebug = false;
        console.warn("关闭与服务器的连接！");
    };

    ws.onerror = function (evt) {
        console.error("连接服务器错误", evt);
    };
}

var id = 12;
$.ajax({
    url: "/api/js_code_file/" + id,
    type: "GET",
    async: true,
    contentType: "application/json; charset=utf-8",
    // data: JSON.stringify({}),
    dataType: "json",
    success: function (result) {
        // console.log(result);
        setValue(result.jsCode);
    }
});

$("#debugBtn").on("click", function () {
    if (isDebug) return;
    vConsole.show();
    // 保存数据
    $.ajax({
        url: "/api/js_code_file/" + id,
        type: "PUT",
        async: true,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({jsCode: getValue()}),
        dataType: "json",
        success: function (result) {
            debug();
        }
    });
});