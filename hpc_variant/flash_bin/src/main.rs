use clap::Parser;
use std::process::Command;
use std::path::Path;
use anyhow::{Result, Context};

// Global configuration constants
const CHIP: &str = "STM32F412RGTx";

/// Simple Rust script to flash ELF binaries using probe-rs download command
#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
    /// Path to the ELF binary file to flash
    #[arg(short, long)]
    file: String,

    /// Optional: Chip name (overrides default)
    #[arg(short, long, default_value = CHIP)]
    chip: String,
}

fn main() -> Result<()> {
    let args = Args::parse();

    // Validate file exists
    let file_path = Path::new(&args.file);
    if !file_path.exists() {
        anyhow::bail!("File not found: {}", args.file);
    }

    println!("🔍 Preparing to flash ELF binary...");
    println!("📁 ELF file: {}", args.file);
    println!("🔧 Chip: {}", args.chip);

    // Build the probe-rs download command
    let mut cmd = Command::new("probe-rs");
    cmd.args(&[
        "download",
        "--chip", &args.chip
        &args.file,
    ]);
   
    println!("⚡ Executing: probe-rs download --chip {} {}", args.chip, args.file);

    // Execute the download command
    let output = cmd.output()
        .context("Failed to execute probe-rs download command. Make sure probe-rs-cli is installed.")?;

    // Print the output
    if !output.stdout.is_empty() {
        println!("📤 Output:");
        println!("{}", String::from_utf8_lossy(&output.stdout));
    }

    if !output.stderr.is_empty() {
        println!("⚠️  Errors/Warnings:");
        println!("{}", String::from_utf8_lossy(&output.stderr));
    }

    if !output.status.success() {
        anyhow::bail!("Download operation failed with exit code: {:?}", output.status.code());
    }

    println!("✅ Download completed successfully!");

    // Now reset the board to start execution
    println!("🔄 Resetting board to start execution...");
    
    let mut reset_cmd = Command::new("probe-rs");
    reset_cmd.args(&[
        "reset",
        "--chip", &args.chip,
    ]);

    println!("⚡ Executing: probe-rs reset --chip {}", args.chip);

    let reset_output = reset_cmd.output()
        .context("Failed to execute probe-rs reset command.")?;

    // Print reset output
    if !reset_output.stdout.is_empty() {
        println!("📤 Reset Output:");
        println!("{}", String::from_utf8_lossy(&reset_output.stdout));
    }

    if !reset_output.stderr.is_empty() {
        println!("⚠️  Reset Errors/Warnings:");
        println!("{}", String::from_utf8_lossy(&reset_output.stderr));
    }

    if reset_output.status.success() {
        println!("✅ Board reset completed successfully!");
        println!("🚀 Your binary is now running on the target!");
    } else {
        anyhow::bail!("Reset operation failed with exit code: {:?}", reset_output.status.code());
    }

    Ok(())
}
