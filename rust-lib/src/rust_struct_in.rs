// STRUCT IN ====================

use std::ffi::c_int;

#[repr(C)]
#[derive(Debug)]
pub struct RustStructQuery {
    pub left: c_int,
    pub right: c_int,
}

#[no_mangle]
pub unsafe extern "C" fn addRustStruct(ptr: *mut RustStructQuery) -> c_int {
    let data = &*ptr;
    return data.left + data.right;
}
