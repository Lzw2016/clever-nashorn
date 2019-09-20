package org.clever.nashorn.tuples;


/**
 * 简单元组
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 22:37 <br/>
 */
public final class Tuple3<A, B, C> {
    private A value1;
    private B value2;
    private C value3;

    public static <A, B, C> Tuple3<A, B, C> creat(final A value1, final B value2, final C value3) {
        return new Tuple3<>(value1, value2, value3);
    }

    private Tuple3(final A value1, final B value2, final C value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public B getValue2() {
        return value2;
    }

    public void setValue2(B val) {
        value2 = val;
    }

    public A getValue1() {
        return value1;
    }

    public void setValue1(A val) {
        value1 = val;
    }

    public C getValue3() {
        return value3;
    }

    public void setValue3(C value3) {
        this.value3 = value3;
    }
}
