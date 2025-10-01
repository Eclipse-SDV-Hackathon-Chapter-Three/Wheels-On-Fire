# Flash Rust - Simple probe-rs download and reset wrapper

A simple Rust script that wraps the `probe-rs download` and `probe-rs reset` commands to flash ELF binaries to target boards and automatically start execution.

## Configuration

The script is pre-configured for:
- **Chip**: `STM32F412RGTx`

This value is defined as a global constant in the source code and can be easily modified if needed.

## Prerequisites

1. **Install Rust**: Download and install Rust from [rustup.rs](https://rustup.rs/)
2. **Install probe-rs-cli**: 
   ```bash
   cargo install probe-rs-cli
   ```

## Usage

```bash
cargo run -- --file <ELF_FILE_PATH>
```

### Arguments

- `--file` or `-f`: Path to the ELF binary file to flash (required)
- `--chip` or `-c`: Chip name (optional, defaults to STM32F412RGTx)

### Examples

```bash
# Flash ELF binary using defaults
cargo run -- --file target/debug/app.elf

# Override chip
cargo run -- --file firmware.elf --chip STM32F411RE

# Using short flags
cargo run -- -f target/release/app.elf -c STM32F103C8
```

## How it works

The script executes the following commands:
```bash
probe-rs download --chip <CHIP> <ELF_FILE>
probe-rs reset --chip <CHIP>
```

This approach leverages the existing `probe-rs-cli` tool, which provides:
- Built-in probe detection
- Support for multiple debug probes
- Comprehensive chip support
- Direct ELF file flashing
- Automatic board reset to start execution

## Features

- ✅ Simple command-line interface
- ✅ Pre-configured defaults for STM32F412RGTx
- ✅ Override chip if needed
- ✅ Comprehensive error handling
- ✅ Progress indicators with emojis
- ✅ File validation
- ✅ Automatic board reset after flashing

## Customization

To use different defaults, modify the global constant in `src/main.rs`:

```rust
// Global configuration constant
const CHIP: &str = "STM32F412RGTx";  // Change this
```

## Error Handling

The script includes comprehensive error handling for:
- Missing probe-rs-cli installation
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