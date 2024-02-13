extern crate cbindgen;

use std::env;
use cbindgen::{Language, Style};

fn main() {
    let crate_dir = env::var("CARGO_MANIFEST_DIR").unwrap();
    let target_dir = format!("{crate_dir}/../target");
    println!("cargo:warning=target dir: {}", target_dir);

    let x = cbindgen::Builder::new()
        .with_crate(crate_dir)
        .with_language(Language::C)
        .with_style(Style::Tag)
        .with_include_guard("RS_LIB_H")
        .generate()
        .expect("Unable to generate bindings")
        .write_to_file(format!("{target_dir}/includes/rust-lib.h"));
    println!("cargo:warning=DONE {}", x);
}
