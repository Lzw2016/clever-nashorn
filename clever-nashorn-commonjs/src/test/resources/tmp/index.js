var test = function (name) {
    console.log("string -> {}", name);
    console.log("number -> {}", 12);
    console.log("number -> {}", 12.345);
    console.log("boolean -> {}", false);
    console.log("null -> {}", null);
    console.log("date -> {}", new Date());
    console.log("array -> {}", ["aaa", 12, 12.345, true, null, new Date()]);
    console.log("array -> {}", ["aaa", "bbb", "ccc"]);
    console.log("object -> {}", {"string": "aaa", "number1": 12, "number2": 12.345, "boolean": true, "null": null, "date": new Date()});
    console.log("function -> {}", function () {
        return 1 + 6;
    });

    console.log("JSON.stringify -> {}", JSON.stringify({date: new Date()}));
};

console.log("main -> {}", this.main);
console.log("exports -> {}", this.exports);
console.log("children -> {}", this.children);
console.log("filepath -> {}", this.filepath);
console.log("filename -> {}", this.filename);
console.log("id -> {}", this.id);
console.log("loaded -> {}", this.loaded);
console.log("parent -> {}", this.parent);
console.log("console -> {}", this.console);

var init = function () {
    console.log("###############################");
    test();
    abcd.callBack();
};

exports.test = test;
exports.init = init;



