# Flash Rust - Simple cargo flash wrapper

A simple Rust script that wraps the `cargo flash` command to flash firmware to target boards.

## Configuration

The script is pre-configured for:
- **Target**: `thumbv7em-none-eabihf`
- **Chip**: `STM32F412RGTx`

These values are defined as global constants in the source code and can be easily modified if needed.

## Prerequisites

1. **Install Rust**: Download and install Rust from [rustup.rs](https://rustup.rs/)
2. **Install cargo-flash**: 
   ```bash
   cargo install cargo-flash
   ```

## Usage

```bash
cargo run -- --file <BINARY_FILE_PATH>
```

### Arguments

- `--file` or `-f`: Path to the binary file to flash (required)
- `--chip` or `-c`: Chip name (optional, defaults to STM32F412RGTx)
- `--target` or `-t`: Target architecture (optional, defaults to thumbv7em-none-eabihf)

### Examples

```bash
# Flash firmware using defaults
cargo run -- --file firmware.bin

# Override chip and target
cargo run -- --file app.bin --chip STM32F411RE --target thumbv7em-none-eabihf

# Using short flags
cargo run -- -f target/debug/app.bin -c STM32F103C8
```

## How it works

The script executes the following command:
```bash
cargo flash --release --chip <CHIP> --target <TARGET> --bin <FILE>
```

This approach leverages the existing `cargo-flash` tool, which provides:
- Built-in probe detection
- Support for multiple debug probes
- Comprehensive chip support
- Optimized flashing algorithms

## Features

- ✅ Simple command-line interface
- ✅ Pre-configured defaults for STM32F412RGTx
- ✅ Override chip and target if needed
- ✅ Comprehensive error handling
- ✅ Progress indicators with emojis
- ✅ File validation
- ✅ Leverages proven cargo-flash tool

## Customization

To use different defaults, modify the global constants in `src/main.rs`:

```rust
// Global configuration constants
const TARGET: &str = "thumbv7em-none-eabihf";  // Change this
const CHIP: &str = "STM32F412RGTx";            // Change this
```

## Error Handling

The script includes comprehensive error handling for:
- Missing cargo-flash installation
- File not found
- Command execution failures
- Invalid arguments

## Building

```bash
# Build the project
cargo build

# Build in release mode
cargo build --release

# Run directly
cargo run -- --help
```