class Foo {
  class MyClass() {
  }
}

class Test(s: Foo.MyClass)

class Test2() {
  fun test(s: Foo.MyClass) {}
}

class Test3() {
  val s: Foo.MyClass? = null
}

// TESTED_OBJECT_KIND: innerClass
// TESTED_OBJECTS: Test, MyClass
// FLAGS: ACC_FINAL, ACC_PUBLIC, ACC_STATIC


// TESTED_OBJECT_KIND: innerClass
// TESTED_OBJECTS: Test2, MyClass
// FLAGS: ACC_FINAL, ACC_PUBLIC, ACC_STATIC

// TESTED_OBJECT_KIND: innerClass
// TESTED_OBJECTS: Test3, MyClass
// FLAGS: ACC_FINAL, ACC_PUBLIC, ACC_STATIC
