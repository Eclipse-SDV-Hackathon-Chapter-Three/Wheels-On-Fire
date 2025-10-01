use clap::Parser;
use std::process::Command;
use std::path::Path;
use anyhow::{Result, Context};

// Global configuration constants
const TARGET: &str = "thumbv7em-none-eabihf";
const CHIP: &str = "STM32F412RGTx";

/// Simple Rust script to flash firmware using cargo flash command
#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
    /// Path to the binary file to flash
    #[arg(short, long)]
    file: String,

    /// Optional: Chip name (overrides default)
    #[arg(short, long, default_value = CHIP)]
    chip: String,

    /// Optional: Target architecture (overrides default)
    #[arg(short, long, default_value = TARGET)]
    target: String,
}

fn main() -> Result<()> {
    let args = Args::parse();

    // Validate file exists
    let file_path = Path::new(&args.file);
    if !file_path.exists() {
        anyhow::bail!("File not found: {}", args.file);
    }

    println!("🔍 Preparing to flash firmware...");
    println!("📁 Binary file: {}", args.file);
    println!("🎯 Target: {}", args.target);
    println!("🔧 Chip: {}", args.chip);

    // Build the cargo flash command
    let mut cmd = Command::new("cargo");
    cmd.args(&[
        "flash",
        "--release",
        "--chip", &args.chip,
        "--target", &args.target,
        "--bin", &args.file,
    ]);

    println!("⚡ Executing: cargo flash --release --chip {} --target {} --bin {}", 
             args.chip, args.target, args.file);

    // Execute the command
    let output = cmd.output()
        .context("Failed to execute cargo flash command. Make sure cargo-flash is installed.")?;

    // Print the output
    if !output.stdout.is_empty() {
        println!("📤 Output:");
        println!("{}", String::from_utf8_lossy(&output.stdout));
    }

    if !output.stderr.is_empty() {
        println!("⚠️  Errors/Warnings:");
        println!("{}", String::from_utf8_lossy(&output.stderr));
    }

    if output.status.success() {
        println!("✅ Flash operation completed successfully!");
    } else {
        anyhow::bail!("Flash operation failed with exit code: {:?}", output.status.code());
    }

    Ok(())
}
