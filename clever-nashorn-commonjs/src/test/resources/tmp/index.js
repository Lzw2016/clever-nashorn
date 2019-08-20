module.exports.test = function (name) {
    // console.log("string {}", name);
    // console.log("number {}", 12);
    // console.log("number {}", 12.345);
    // console.log("boolean {}", false);
    // console.log("null {}", null);
    // console.log("date {}", new Date());
    console.log("array {}", ["aaa", 12, 12.345, true, null, new Date()]);
    // console.log("array {}", ["aaa", "bbb", "ccc"]);
    // console.log("object {}", {"string": "aaa", "number1": 12, "number2": 12.345, "boolean": true, "null": null, "date": new Date()});

    // console.log("JSON.stringify -> {}", JSON.stringify({date: new Date()}));
};

module.exports.init = function () {
    console.log("###############################");
};
