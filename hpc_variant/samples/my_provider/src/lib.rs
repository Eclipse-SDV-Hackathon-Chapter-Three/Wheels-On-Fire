extern crate symphony;

use symphony::models::{
    ProviderConfig, ValidationRule, DeploymentSpec, ComponentStep, ComponentSpec,
    DeploymentStep, ComponentResultSpec, State, ComponentAction,
    ComponentValidationRule
};
use symphony::ITargetProvider;
use symphony::ProviderWrapper;
use std::collections::HashMap;
use std::path::Path;
use std::process::Command;
use serde_json::Value;

pub struct MyProvider;

#[unsafe(no_mangle)]
pub extern "C" fn create_provider() -> *mut ProviderWrapper  {
    let provider: Box<dyn ITargetProvider> = Box::new(MyProvider {});
    let wrapper = Box::new(ProviderWrapper { inner: provider });
    Box::into_raw(wrapper)
}

impl ITargetProvider for MyProvider {

    fn get_validation_rule(&self) -> Result<ValidationRule, String> {
        println!("MY RUST PROVIDER: ------ get_validation_rule()");
        Ok(ValidationRule::default())
    }

    fn get(
    &self,
    _deployment: DeploymentSpec,
    _references: Vec<ComponentStep>,
) -> Result<Vec<ComponentSpec>, String> {
    println!("MY RUST PROVIDER: ------ get()");
          // Build properties map
        // Build properties map with serde_json::Value
        let mut properties_ecu: HashMap<String, Value> = HashMap::new();
        properties_ecu.insert("package".to_string(), Value::String("my_ecu_package".to_string()));
        properties_ecu.insert("break".to_string(), Value::String("0".to_string()));
        properties_ecu.insert("url".to_string(), Value::String("localhost".to_string()));
        // Construct ComponentSpec
        
    let component_spec = ComponentSpec {
        name: "ecu".to_string(),
        component_type: Some("my_ecu_type".to_string()),
        properties: Some(properties_ecu),

        // Everything else should be None to match JSON
        dependencies: None,
        metadata: None,
        parameters: None,
        constraints: None,
        routes: None,
        sidecars: None,
        skills: None,
    };

    Ok(vec![component_spec])
}

    fn apply(
    &self,
    _deployment: DeploymentSpec,
    step: DeploymentStep,
    _is_dry_run: bool,
) -> Result<HashMap<String, ComponentResultSpec>, String> {
    println!("JC - Apply method");

    let mut result: HashMap<String, ComponentResultSpec> = HashMap::new();

    for component in step.components.iter() {
        if component.action == ComponentAction::Update {
            println!("Applying component: {:?}", component.component);

            // 🔹 Check the component type (so we can decide what to do)
            match component.component.component_type.as_deref() {
                Some("my_ecu_type") => {
                    // 1️⃣ Flash ELF binary
                    let elf_path = Path::new("/extensions/network_raw_mqtt");
                    if !elf_path.exists() {
                        println!("❌ ELF file not found: {:?}", elf_path);
                    } else {
                        println!("⚡ Flashing {:?}", elf_path);
                        let status = Command::new("probe-rs")
                            .args(&[
                                "download",
                                "--chip", "STM32F412RGTx",
                                elf_path.to_str().unwrap(),
                            ])
                            .status()
                            .map_err(|e| format!("Failed to run probe-rs: {:?}", e))?;

                        if status.success() {
                            println!("✅ Flash success!");
                        } else {
                            eprintln!("❌ Flash failed with code {:?}", status.code());
                        }
                    }

                    // 2️⃣ Install APK
                    let apk_path = Path::new("/extensions/app-withoutfeature.apk");
                    if apk_path.exists() {
                        println!("🔍 Installing APK...");
                        let status = Command::new("adb")
                            .args(&["install", "-r", apk_path.to_str().unwrap()])
                            .status()
                            .map_err(|e| format!("Failed to run adb install: {:?}", e))?;

                        if status.success() {
                            println!("✅ APK installed!");
                        } else {
                            println!("❌ Failed to install APK");
                        }

                        // 3️⃣ Launch app
                        let package_name = "com.example.digitalclusterapp";
                        let main_activity = ".app.MainActivity";
                        let status = Command::new("adb")
                            .args(&[
                                "shell", "am", "start",
                                "-n", &format!("{}/{}", package_name, main_activity),
                            ])
                            .status()
                            .map_err(|e| format!("Failed to run adb start: {:?}", e))?;

                        if status.success() {
                            println!("✅ App launched!");
                        } else {
                            println!("❌ Failed to launch app");
                        }
                    } else {
                        println!("❌ APK file not found: {:?}", apk_path);
                    }
                }

                _ => {
                    println!("⚠️ Unknown component type, skipping");
                }
            }

            // Report success (or you could report failure if flash/adb failed)
            let component_result = ComponentResultSpec {
                status: State::OK,
                message: "Component applied successfully".to_string(),
            };
            result.insert(component.component.name.clone(), component_result);
        } 
        else if component.action == ComponentAction::Delete {
            println!("Deleting component: {:?}", component.component.name);
            // Here you could uninstall the APK, reset device, etc.
        }
    }

    Ok(result)
}
    
  

}

#[cfg(test)]
mod tests {
    use super::*;
    use symphony::models::{DeploymentSpec};

    #[test]
    fn test_get() {
        let provider = MyProvider {          
        };

        let deployment = DeploymentSpec::empty();
        let references = vec![];

        let result = provider.get(deployment, references);
        assert!(result.is_ok(), "Expected Ok result, but got {:?}", result);
    }
}