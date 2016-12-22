// See KT-14705

enum class En { A, B, С }

fun foo() {
    // nullable variable
    val en2: Any? = En.A
    if (en2 is En) {
        when (<!DEBUG_INFO_SMARTCAST!>en2<!>) {
            En.A -> {}
            En.B -> {}
            En.С -> {}
        }
    }

    // not nullable variable
    val en1: Any = En.A
    if (en1 is En) {
        when (<!DEBUG_INFO_SMARTCAST!>en1<!>) {
            En.A -> {}
            En.B -> {}
            En.С -> {}
        }
    }
}