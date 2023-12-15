use std::ffi::c_int;

mod rust_struct_in;
mod rust_struct_out;
mod rust_struct_async;

// SIMPLE ====================

#[no_mangle]
pub unsafe extern "C" fn addRust(left: c_int, right: c_int) -> c_int {
    left + right
}


#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = unsafe { addRust(2, 2) };
        assert_eq!(result, 4);
    }
}
