function test7() {
    var i;
    var fib = []; //Initialize array!
    fib[0] = 1;
    fib[1] = 1;
    for (i = 2; i <= 100; i++) {
        fib[i] = fib[i - 2] + fib[i - 1];
    }
    return fib;
}

exports.test7 = test7;