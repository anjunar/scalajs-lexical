import './index.css'
import '@anjunar/scalajs-lexical/index.css'
import 'scalajs:main.js'

// Update timestamp when state changes (hooked into window for simplicity)
window.updateStateTimestamp = () => {
  const lastUpdate = document.getElementById('last-update');
  if (lastUpdate) {
    const now = new Date();
    lastUpdate.textContent = `Last updated: ${now.toLocaleTimeString()}`;
  }
};

// Showcase Interaction Logic
document.addEventListener('DOMContentLoaded', () => {
  const sidebarItems = document.querySelectorAll('.sidebar-nav li[data-mode]');
  const resonanceMsg = document.getElementById('resonance-msg');
  
  const resonanceQuotes = [
    "Confusion is raw material, not an accident.",
    "Good form protects the living.",
    "Precise and revisable. Strict at the thresholds.",
    "Truth emerges through simplification.",
    "Logic ends at closed systems. Seek the third space.",
    "The ego is necessary, but not sovereign."
  ];

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
