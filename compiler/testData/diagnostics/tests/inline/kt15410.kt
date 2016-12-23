// !DIAGNOSTICS: -UNUSED_PARAMETER

abstract class Foo protected constructor()

inline fun foo(f: () -> Unit) = object: Foo() {}