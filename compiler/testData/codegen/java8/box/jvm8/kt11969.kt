// JVM_TARGET: 1.8
// WITH_RUNTIME
// IGNORE_BACKEND: JS

interface Z {
    private fun privateFun() = { "OK" }

    fun callPrivateFun() = privateFun()

    fun publicFun() = { "OK" }

    class Nested
}

class Test : Z

fun box(): String {

    val privateFun = Test().callPrivateFun()
    var enclosing = privateFun.javaClass.enclosingMethod!!
    if (enclosing.name != "privateFun") return "fail 1: ${enclosing.name}"
    if (enclosing.getDeclaringClass().simpleName != "Z") return "fail 2: ${enclosing.getDeclaringClass().simpleName}"

    val publicFun = Test().publicFun()
    enclosing = publicFun.javaClass.enclosingMethod!!
    if (enclosing.name != "publicFun") return "fail 3: ${enclosing.name}"
    if (enclosing.getDeclaringClass().simpleName != "Z") return "fail 4: ${enclosing.getDeclaringClass().simpleName}"

    val nested = Z.Nested::class.java
    val enclosingClass = nested.enclosingClass!!
    if (enclosingClass.name != "Z") return "fail 5: ${enclosingClass.name}"

    return "OK"
}
