var CommonUtils2 = Java.type("org.clever.nashorn.internal.CommonUtils");

var test = function (name) {
    var fuc = function () { return 1 + 6; };
    var array = [1, 2.2, true, "nashorn", null, new Date(), undefined, fuc];
    var object = {
        "string": "aaa",
        "number1": 12,
        "number2": 12.345,
        "boolean": true,
        "null": null,
        "date": new Date(),
        "undefined": undefined,
        "function": fuc
    };
    console.log("打印JS变量 | undefined ", undefined);
    console.log("打印JS变量 | null ", null);
    console.log("打印JS变量 | int ", 1);
    console.log("打印JS变量 | float ", 2.2);
    console.log("打印JS变量 | boolean ", true);
    console.log("打印JS变量 | string ", "nashorn");
    console.log("打印JS变量 | date ", new Date());
    console.log("打印JS变量 | array ", array);
    console.log("打印JS变量 | object ", object);
    console.log("打印JS变量 | function ", fuc);
    console.log("打印JS变量 | JSON.stringify ", JSON.stringify({date: new Date()}));

    // 循环引用
    var a = {"name": "AAA"};
    var b = {"name": "BBB"};
    a.child = b;
    b.parent = a;
    console.log("打印JS变量 | 循环引用 ", a);
    // 循环打印
    for (var i = 0; i < 30; i++) {
        console.log("name=", name, " | i=", i, " ", object);
        // CommonUtils.sleep(350);
        CommonUtils2.sleep(1000);
    }
};

// exports.init = init;
exports.test = test;



