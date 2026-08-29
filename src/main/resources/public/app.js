const searchForm = document.getElementById('searchForm');
const searchInput = document.getElementById('searchInput');
const resultsBody = document.getElementById('resultsBody');
const navToggle = document.querySelector('.nav-toggle');
const navMenu = document.querySelector('.nav-menu');
const navLinks = document.querySelectorAll('.nav-link');
const views = document.querySelectorAll('.view');

async function fetchJson(url) {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error('No se pudo cargar la información');
  }
  return response.json();
}

function renderRows(libros) {
  if (!resultsBody) return;

  if (!libros || libros.length === 0) {
    resultsBody.innerHTML = '<tr><td colspan="4" class="empty-state">No se encontraron libros para esta búsqueda.</td></tr>';
    return;
  }

  resultsBody.innerHTML = libros
    .map(
      (libro) => `
        <tr>
          <td>${libro.titulo}</td>
          <td>${libro.autor}</td>
          <td>${libro.categoria}</td>
          <td>${libro.paginas}</td>
        </tr>
      `
    )
    .join('');
}

async function loadStats() {
  const stats = await fetchJson('/api/estadisticas');
  const totalLibros = document.getElementById('totalLibros');
  const promedioPaginas = document.getElementById('promedioPaginas');
  const totalCategorias = document.getElementById('totalCategorias');
  const statTotal = document.getElementById('statTotal');
  const statPromedio = document.getElementById('statPromedio');
  const statCategorias = document.getElementById('statCategorias');

  if (totalLibros) totalLibros.textContent = stats.totalLibros;
  if (promedioPaginas) promedioPaginas.textContent = `${stats.promedioPaginas.toFixed(1)} pág.`;
  if (totalCategorias) totalCategorias.textContent = stats.categorias.length;
  if (statTotal) statTotal.textContent = stats.totalLibros;
  if (statPromedio) statPromedio.textContent = `${stats.promedioPaginas.toFixed(1)} pág.`;
  if (statCategorias) statCategorias.textContent = stats.categorias.length;
}

async function loadBooks(q = '') {
  const url = q ? `/api/buscar?q=${encodeURIComponent(q)}` : '/api/libros';
  const libros = await fetchJson(url);
  renderRows(libros);
}

function showView(viewName) {
  views.forEach((view) => {
    view.classList.toggle('active', view.id === `${viewName}-view`);
  });

  navLinks.forEach((link) => {
    link.classList.toggle('active', link.dataset.view === viewName);
  });

  if (navMenu) {
    navMenu.classList.remove('open');
  }
  if (navToggle) {
    navToggle.setAttribute('aria-expanded', 'false');
  }
}

searchForm?.addEventListener('submit', async (event) => {
  event.preventDefault();
  const query = searchInput.value.trim();
  await loadBooks(query);
  showView('catalogo');
});

searchInput?.addEventListener('input', async () => {
  const query = searchInput.value.trim();
  if (query.length === 0 || query.length >= 2) {
    await loadBooks(query);
  }
});

navToggle?.addEventListener('click', () => {
  const isOpen = navMenu.classList.toggle('open');
  navToggle.setAttribute('aria-expanded', String(isOpen));
});

navLinks.forEach((link) => {
  link.addEventListener('click', () => {
    showView(link.dataset.view);
  });
});

(async function init() {
  await loadStats();
  await loadBooks();
})();
