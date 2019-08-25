exports.printJava = function (object) {
    // 打印Java变量
    console.log("打印Java变量 {}", object);
};

exports.printJS = function () {
    console.log("打印JS变量 | null {}", null);
    console.log("打印JS变量 | int {}", 1);
    console.log("打印JS变量 | float {}", 2.2);
    console.log("打印JS变量 | boolean {}", true);
    console.log("打印JS变量 | string {}", "nashorn");
    console.log("打印JS变量 | date {}", new Date());
    console.log("打印JS变量 | array {}", [1, 2.2, true, "nashorn", null, new Date()]);
    console.log("打印JS变量 | object {}", {"string": "aaa", "number1": 12, "number2": 12.345, "boolean": true, "null": null, "date": new Date()});
    console.log("打印JS变量 | function {}", function () { return 1 + 6; });
    console.log("打印JS变量 | JSON.stringify {}", JSON.stringify({date: new Date()}));
};

exports.tmp = function (object) {
    console.log("打印Java变量 {}", object);
    console.log("打印JS变量|date {}", new Date());
};
