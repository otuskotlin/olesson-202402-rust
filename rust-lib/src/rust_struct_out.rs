// STRUCT OUT ====================

use std::ffi::c_int;

#[repr(C)]
#[derive(Debug)]
pub struct RustStructResult {
    pub sum: c_int,
    pub status: RustStructStatus,
}

#[repr(C)]
#[derive(Debug)]
pub enum RustStructStatus {
    SUCCESS = 10,
    // ERROR = 20,
}

#[no_mangle]
pub unsafe extern "C" fn addRustReturn(left: c_int, right: c_int) -> *mut RustStructResult {
    Box::into_raw(Box::new(RustStructResult {
        sum: left + right,
        status: RustStructStatus::SUCCESS,
    }))
}

#[no_mangle]
pub extern "C" fn addRustReturnClean(ptr: *mut RustStructResult) {
    if ptr.is_null() {
        return;
    }
    unsafe {
        let _ = Box::from_raw(ptr);
    }
}
