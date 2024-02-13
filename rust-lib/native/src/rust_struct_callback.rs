// ASYNC ====================

use std::ffi::c_void;
use std::thread::sleep;
use std::time::Duration;
use crate::rust_struct_in::RustStructQuery;
use crate::rust_struct_out::RustStructResult;
use crate::rust_struct_out::RustStructStatus::SUCCESS;

pub type AddRustCallback = unsafe extern "C" fn(*const RustStructResult, data: *mut c_void);

#[no_mangle]
pub unsafe extern "C" fn addRustCallback(ptr: *mut RustStructQuery, callback: AddRustCallback, udat: *mut c_void) {
    let data = &*ptr;
    let rt  = tokio::runtime::Builder::new_current_thread().build().unwrap();

    let result = RustStructResult {
        sum: rt.block_on(add_async_ints(data.left as i32, data.right as i32)),
        status: SUCCESS,
    };
    callback(&result, udat);
}

async fn add_async_ints(left: i32, right: i32) -> i32 {
    sleep(Duration::from_millis(10000));
    left + right
}
