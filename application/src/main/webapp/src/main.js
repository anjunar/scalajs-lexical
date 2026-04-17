import '@anjunar/scalajs-lexical/index.css'
import './index.css'
import 'scalajs:main.js'

const THEME_STORAGE_KEY = 'scalajs-lexical-theme'

const applyTheme = (theme) => {
  document.documentElement.setAttribute('data-theme', theme)
  try {
    window.localStorage.setItem(THEME_STORAGE_KEY, theme)
  } catch (_error) {
    // Ignore storage failures and keep the live theme change.
  }
}

const preferredTheme = () => {
  try {
    const savedTheme = window.localStorage.getItem(THEME_STORAGE_KEY)
    if (savedTheme === 'light' || savedTheme === 'dark') {
      return savedTheme
    }
  } catch (_error) {
    // Fall back to the browser preference below.
  }

  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

window.setDemoTheme = applyTheme
window.toggleDemoTheme = () => {
  const currentTheme = document.documentElement.getAttribute('data-theme') || 'light'
  applyTheme(currentTheme === 'dark' ? 'light' : 'dark')
}

applyTheme(preferredTheme())

// Update timestamp when state changes (hooked into window for simplicity)
window.updateStateTimestamp = () => {
  const lastUpdate = document.getElementById('last-update');
  if (lastUpdate) {
    const now = new Date();
    lastUpdate.textContent = `Last updated: ${now.toLocaleTimeString()}`;
  }
};

document.addEventListener('DOMContentLoaded', () => {
  const sidebarItems = document.querySelectorAll('.sidebar-nav li[data-mode]');
  const resonanceMsg = document.getElementById('resonance-msg');
  const themeToggle = document.getElementById('theme-toggle');
  const themeToggleIcon = document.querySelector('#theme-toggle .theme-toggle-icon');

  const resonanceQuotes = [
    "Confusion is raw material, not an accident.",
    "Good form protects the living.",
    "Precise and revisable. Strict at the thresholds.",
    "Truth emerges through simplification.",
    "Logic ends at closed systems. Seek the third space.",
    "The ego is necessary, but not sovereign."
  ];

  const syncThemeToggle = () => {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    if (themeToggleIcon) {
      themeToggleIcon.textContent = currentTheme === 'dark' ? 'dark_mode' : 'light_mode';
    }
    if (themeToggle) {
      themeToggle.title = currentTheme === 'dark' ? 'Switch to light theme' : 'Switch to dark theme';
      themeToggle.setAttribute('aria-label', themeToggle.title);
    }
  };

  // Sidebar Mode Switching
  sidebarItems.forEach(item => {
    item.addEventListener('click', () => {
      sidebarItems.forEach(i => i.classList.remove('active'));
      item.classList.add('active');
      
      // Visual feedback for mode switch
      const mode = item.getAttribute('data-mode');
      console.log(`Switched to mode: ${mode}`);
    });
  });

  if (themeToggle) {
    themeToggle.addEventListener('click', () => {
      window.toggleDemoTheme();
      syncThemeToggle();
    });
    syncThemeToggle();
  }

  // Periodically update resonance message to keep the manifesto alive
  let quoteIndex = 0;
  setInterval(() => {
    resonanceMsg.style.opacity = 0;
    setTimeout(() => {
      quoteIndex = (quoteIndex + 1) % resonanceQuotes.length;
      resonanceMsg.textContent = `"${resonanceQuotes[quoteIndex]}"`;
      resonanceMsg.style.opacity = 1;
    }, 500);
  }, 8000);
});
