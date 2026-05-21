const app = document.getElementById('app');

let currentPage = 'welcome';

function navigate(page) {
  currentPage = page;
  renderPage();
}

function renderPage() {
  switch (currentPage) {
    case 'welcome':
      renderWelcome();
      break;

    case 'auth':
      renderAuth();
      break;

    case 'dashboard':
      renderDashboard();
      break;

    case 'room':
      renderRoom();
      break;

    case 'profile':
      renderProfile();
      break;
  }
}

renderPage();

function renderWelcome() {
  app.innerHTML = `
    <section class="page welcome-page">
      <div class="container welcome-container">
        <h1>SyncWave</h1>
        <p>Listen to music together in real-time.</p>

        <button class="primary-btn" id="startBtn">
          Get Started
        </button>
      </div>
    </section>
  `;

  document
    .getElementById('startBtn')
    .addEventListener('click', () => {
      navigate('auth');
    });
}