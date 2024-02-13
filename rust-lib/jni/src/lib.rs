// This is the interface to the JVM that we'll call the majority of our
// methods on.
use jni::JNIEnv;

// These objects are what you should use as arguments to your native
// function. They carry extra lifetime information to prevent them escaping
// this context and getting used after being GC'd.
use jni::objects::JObject;

// This is just a pointer. We'll be returning it from our function. We
// can't return one of the objects with lifetime information because the
// lifetime checker won't let us.
use jni::sys::jint;

#[no_mangle]
pub extern "system" fn Java_ru_otus_otuskotlin_NativeHost_callInt(
    mut env: JNIEnv,
    obj: JObject,
    x: jint
) -> jint {
    let in_val = env.get_field(obj, "coeff", "I");
    if in_val.is_ok() {
        return x * in_val.unwrap().i().unwrap()
    }
    0
}
