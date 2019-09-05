//禁止鼠标右键菜单
document.oncontextmenu = function () {
    return false;
};

// 代码树
var codeTree = $("#codeTree");
var codeTreeObj = null;
var codeTreeUrl = "/api/js_code_file/tree";
var codeTreeUrlParams = {bizType: "default", groupName: "default"};

// 初始化入口
$(document).ready(function () {
    // 初始化编辑器
    initEditor({
        width: "100%",
        height: "100%",
        "last": "last"
    });

    // 初始化代码树
    $.ajax({
        url: codeTreeUrl + "?" + Qs.stringify(codeTreeUrlParams),
        type: "GET",
        async: true,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            console.log(result);
            codeTreeObj = $.fn.zTree.init(
                codeTree,
                {
                    view: {
                        selectedMulti: false,
                        fontCss: {"font-size": "16px !important"}
                    }
                },
                result
            );
        }
    });


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

/*
// 清除日志
window.console.clear();

*/
