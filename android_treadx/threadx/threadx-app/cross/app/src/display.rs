use embedded_graphics::mono_font::ascii::FONT_7X14;
use embedded_graphics::{
    mono_font::MonoTextStyleBuilder,
    pixelcolor::BinaryColor,
    prelude::*,
    text::{Baseline, Text},
};
use board::{DisplayType, I2CBus};
use threadx_rs::mutex::Mutex;
use threadx_rs::WaitOption::WaitForever;

/// Display manager for handling text rendering on the OLED display.
pub struct DisplayManager<'a> {
    display: &'a Mutex<Option<DisplayType<I2CBus>>>,
}

impl<'a> DisplayManager<'a> {
    /// Creates a new DisplayManager instance.
    ///
    /// # Arguments
    /// * `display` - A reference to the display mutex containing the display instance.
    ///
    /// # Returns
    /// A new DisplayManager instance.
    pub fn new(display: &'a Mutex<Option<DisplayType<I2CBus>>>) -> Self {
        Self { display }
    }

    /// Prints text to the display.
    ///
    /// # Arguments
    /// * `text` - The text to display on the screen.
    ///
    /// # Panics
    /// Will panic if unable to obtain the display lock.
    pub fn print_text(&self, text: &str) {
        let mut display_guard = self.display.lock(WaitForever).unwrap();
        self.print_text_unlocked(text, &mut *display_guard);
    }

    /// Prints text to the display without acquiring the lock.
    /// This is useful when the lock is already held by the caller.
    ///
    /// # Arguments
    /// * `text` - The text to display on the screen.
    /// * `display` - A mutable reference to the display option.
    pub fn print_text_unlocked(&self, text: &str, display: &mut Option<DisplayType<I2CBus>>) {
        let text_style = MonoTextStyleBuilder::new()
            .font(&FONT_7X14)
            .text_color(BinaryColor::On)
            .build();
        
        if let Some(actual_display) = display {
            actual_display.clear_buffer();
            Text::with_baseline(text, Point::zero(), text_style, Baseline::Top)
                .draw(actual_display)
                .unwrap();
            actual_display.flush().unwrap();
        }
    }

    /// Clears the display buffer.
    ///
    /// # Panics
    /// Will panic if unable to obtain the display lock.
    pub fn clear(&self) {
        let mut display_guard = self.display.lock(WaitForever).unwrap();
        if let Some(actual_display) = display_guard.as_mut() {
            actual_display.clear_buffer();
            actual_display.flush().unwrap();
        }
    }

    /// Clears the display buffer without acquiring the lock.
    /// This is useful when the lock is already held by the caller.
    ///
    /// # Arguments
    /// * `display` - A mutable reference to the display option.
    pub fn clear_unlocked(&self, display: &mut Option<DisplayType<I2CBus>>) {
        if let Some(actual_display) = display {
            actual_display.clear_buffer();
            actual_display.flush().unwrap();
        }
    }

    /// Renders text at a specific position on the display.
    ///
    /// # Arguments
    /// * `text` - The text to display.
    /// * `position` - The position where to render the text.
    /// * `display` - A mutable reference to the display option.
    pub fn render_text_at_position(&self, text: &str, position: Point, display: &mut Option<DisplayType<I2CBus>>) {
        let text_style = MonoTextStyleBuilder::new()
            .font(&FONT_7X14)
            .text_color(BinaryColor::On)
            .build();
        
        if let Some(actual_display) = display {
            Text::with_baseline(text, position, text_style, Baseline::Top)
                .draw(actual_display)
                .unwrap();
        }
    }

    /// Renders multiple text elements on the display without clearing the buffer.
    /// This allows for layered text rendering.
    ///
    /// # Arguments
    /// * `text_elements` - A slice of tuples containing (text, position) pairs.
    /// * `display` - A mutable reference to the display option.
    pub fn render_multiple_text(&self, text_elements: &[(&str, Point)], display: &mut Option<DisplayType<I2CBus>>) {
        if let Some(actual_display) = display {
            actual_display.clear_buffer();
            
            let text_style = MonoTextStyleBuilder::new()
                .font(&FONT_7X14)
                .text_color(BinaryColor::On)
                .build();
            
            for (text, position) in text_elements {
                Text::with_baseline(text, *position, text_style, Baseline::Top)
                    .draw(actual_display)
                    .unwrap();
            }
            
            actual_display.flush().unwrap();
        }
    }
}

/// Convenience function for printing text to a display.
/// This function maintains backward compatibility with the original `print_text` function.
///
/// # Arguments
/// * `text` - The text to display on the screen.
/// * `display` - A mutable reference to the display option.
pub fn print_text(text: &str, display: &mut Option<DisplayType<I2CBus>>) {
    let text_style = MonoTextStyleBuilder::new()
        .font(&FONT_7X14)
        .text_color(BinaryColor::On)
        .build();
    
    if let Some(actual_display) = display {
        actual_display.clear_buffer();
        Text::with_baseline(text, Point::zero(), text_style, Baseline::Top)
            .draw(actual_display)
            .unwrap();
        actual_display.flush().unwrap();
    }
}

/// Convenience function for rendering multiple text elements on the display.
///
/// # Arguments
/// * `text_elements` - A slice of tuples containing (text, position) pairs.
/// * `display` - A mutable reference to the display option.
pub fn render_multiple_text(text_elements: &[(&str, Point)], display: &mut Option<DisplayType<I2CBus>>) {
    if let Some(actual_display) = display {
        actual_display.clear_buffer();
        
        let text_style = MonoTextStyleBuilder::new()
            .font(&FONT_7X14)
            .text_color(BinaryColor::On)
            .build();
        
        for (text, position) in text_elements {
            Text::with_baseline(text, *position, text_style, Baseline::Top)
                .draw(actual_display)
                .unwrap();
        }
        
        actual_display.flush().unwrap();
    }
}
