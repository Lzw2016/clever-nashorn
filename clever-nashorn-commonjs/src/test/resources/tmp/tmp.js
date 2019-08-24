var CommonUtils2 = Java.type("org.clever.nashorn.internal.CommonUtils");

var test = function (name) {
    for (var i = 0; i < 1000; i++) {
        console.log("name={} | i={}", name, i);
        // CommonUtils.sleep(350);
        CommonUtils2.sleep(350);
    }
};

// exports.init = init;
exports.test = test;



