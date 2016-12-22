// WITH_RUNTIME
// IGNORE_BACKEND: JS

interface Z {
    private fun privateFun() = { "OK" }

    fun callPrivateFun() = privateFun()

    fun publicFun() = { "OK" }

    fun funWithDefaultArfs(s: () -> Unit = {}): () -> Unit

    class Nested
}

class Test : Z {
    override fun funWithDefaultArfs(s: () -> Unit): () -> Unit {
        return s
    }
}

fun box(): String {

    val privateFun = Test().callPrivateFun()
    var enclosing = privateFun.javaClass.enclosingMethod!!
    if (enclosing.name != "privateFun") return "fail 1: ${enclosing.name}"
    if (enclosing.getDeclaringClass().simpleName != "DefaultImpls") return "fail 2: ${enclosing.getDeclaringClass().simpleName}"

    val publicFun = Test().publicFun()
    enclosing = publicFun.javaClass.enclosingMethod!!
    if (enclosing.name != "publicFun") return "fail 3: ${enclosing.name}"
    if (enclosing.getDeclaringClass().simpleName != "DefaultImpls") return "fail 4: ${enclosing.getDeclaringClass().simpleName}"

    val defaultArgs = Test().funWithDefaultArfs()
    enclosing = defaultArgs.javaClass.enclosingMethod!!
    if (enclosing.name != "funWithDefaultArfs\$default") return "fail 5: ${enclosing.name}"
    if (enclosing.parameterTypes.size != 4) return "fail 6: not default method ${enclosing.name}"
    if (enclosing.getDeclaringClass().simpleName != "DefaultImpls") return "fail 7: ${enclosing.getDeclaringClass().simpleName}"

    val nested = Z.Nested::class.java
    val enclosingClass = nested.enclosingClass!!
    if (enclosingClass.name != "Z") return "fail 8: ${enclosingClass.name}"

    return "OK"
}
