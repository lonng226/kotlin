// WITH_RUNTIME
// IS_APPLICABLE: false
fun foo(list: List<String>, target: MutableList<String>) {
    <caret>for (s in list) {
        val length = if (s.isNotEmpty()) s.length else continue
        target.add(length.toString())
    }
}