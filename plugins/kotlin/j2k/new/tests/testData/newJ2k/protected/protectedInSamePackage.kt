package test

class BaseSamePackage {
    fun foo() {}
    var i: Int = 1
}

internal class DerivedSamePackage {
    fun usage1() {
        val base = BaseSamePackage()
        base.foo()
        val i = base.i
    }
}
