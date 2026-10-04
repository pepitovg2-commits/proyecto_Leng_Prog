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
    const errorBody = await response.json().catch(() => ({}));
    throw new Error(errorBody.error || 'No se pudo cargar la información');
  }

  return response.json();
}

function renderRows(libros) {
  if (!resultsBody) return;

  if (!libros || libros.length === 0) {
    resultsBody.innerHTML =
        '<tr><td colspan="4" class="empty-state">No se encontraron libros para esta búsqueda.</td></tr>';
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

// ==========================================
// DISTRIBUCIÓN DE LIBROS POR CATEGORÍA
// ==========================================
function renderCategoryDistribution(distribucion, totalLibros) {
  const container = document.getElementById('categoryDistribution');

  if (!container) return;

  if (!distribucion || distribucion.length === 0) {
    container.innerHTML =
        '<p class="empty-state">No hay datos de categorías disponibles.</p>';
    return;
  }

  container.innerHTML = distribucion
      .map((item) => {
        const porcentaje =
            totalLibros > 0
                ? (item.cantidad / totalLibros) * 100
                : 0;

        return `
        <div class="category-item">

          <div class="category-info">
            <span class="category-name">
              ${item.categoria}
            </span>

            <span class="category-count">
              ${item.cantidad} ${item.cantidad === 1 ? 'libro' : 'libros'}
            </span>
          </div>

          <div class="category-bar">
            <div
              class="category-bar-fill"
              style="width: ${porcentaje}%"
            ></div>
          </div>

          <div class="category-percentage">
            ${porcentaje.toFixed(0)}%
          </div>

        </div>
      `;
      })
      .join('');
}

// ==========================================
// CARGAR ESTADÍSTICAS
// ==========================================
async function loadStats() {
  const stats = await fetchJson('/api/estadisticas');

  // Estadísticas de la página de inicio
  const totalLibros = document.getElementById('totalLibros');
  const promedioPaginas = document.getElementById('promedioPaginas');
  const totalCategorias = document.getElementById('totalCategorias');

  // Estadísticas principales
  const statTotal = document.getElementById('statTotal');
  const statPromedio = document.getElementById('statPromedio');
  const statCategorias = document.getElementById('statCategorias');

  // Nuevas estadísticas
  const statLibroLargo = document.getElementById('statLibroLargo');
  const statPaginasLargo = document.getElementById('statPaginasLargo');

  const statLibroCorto = document.getElementById('statLibroCorto');
  const statPaginasCorto = document.getElementById('statPaginasCorto');

  const statLibrosCortos = document.getElementById('statLibrosCortos');

  // ==========================================
  // ESTADÍSTICAS DEL INICIO
  // ==========================================
  if (totalLibros) {
    totalLibros.textContent = stats.totalLibros;
  }

  if (promedioPaginas) {
    promedioPaginas.textContent =
        `${stats.promedioPaginas.toFixed(1)} pág.`;
  }

  if (totalCategorias) {
    totalCategorias.textContent = stats.categorias.length;
  }

  // ==========================================
  // ESTADÍSTICAS GENERALES
  // ==========================================
  if (statTotal) {
    statTotal.textContent = stats.totalLibros;
  }

  if (statPromedio) {
    statPromedio.textContent =
        `${stats.promedioPaginas.toFixed(1)} pág.`;
  }

  if (statCategorias) {
    statCategorias.textContent = stats.categorias.length;
  }

  // ==========================================
  // LIBRO CON MÁS PÁGINAS
  // ==========================================
  if (statLibroLargo) {
    statLibroLargo.textContent =
        stats.libroMasLargo.titulo;
  }

  if (statPaginasLargo) {
    statPaginasLargo.textContent =
        `${stats.libroMasLargo.paginas} páginas`;
  }

  // ==========================================
  // LIBRO CON MENOS PÁGINAS
  // ==========================================
  if (statLibroCorto) {
    statLibroCorto.textContent =
        stats.libroMasCorto.titulo;
  }

  if (statPaginasCorto) {
    statPaginasCorto.textContent =
        `${stats.libroMasCorto.paginas} páginas`;
  }

  // ==========================================
  // CANTIDAD DE LIBROS CORTOS
  // ==========================================
  if (statLibrosCortos) {
    statLibrosCortos.textContent =
        stats.cantidadLibrosCortos;
  }

  // ==========================================
  // DISTRIBUCIÓN POR CATEGORÍA
  // ==========================================
  renderCategoryDistribution(
      stats.distribucionCategorias,
      stats.totalLibros
  );
}

// ==========================================
// CARGAR LIBROS
// ==========================================
async function loadBooks(q = '') {
  const url = q
      ? `/api/buscar?q=${encodeURIComponent(q)}`
      : '/api/libros';

  const libros = await fetchJson(url);

  renderRows(libros);
}

// ==========================================
// FILTRAR LIBROS POR CATEGORÍA
// ==========================================
async function loadBooksByCategory(categoria) {
  if (categoria === 'Todos') {
    await loadBooks();
    return;
  }

  const url =
      `/api/categoria?cat=${encodeURIComponent(categoria)}`;

  const libros = await fetchJson(url);

  renderRows(libros);
}

// ==========================================
// CAMBIAR DE VISTA
// ==========================================
function showView(viewName) {
  views.forEach((view) => {
    view.classList.toggle(
        'active',
        view.id === `${viewName}-view`
    );
  });

  navLinks.forEach((link) => {
    link.classList.toggle(
        'active',
        link.dataset.view === viewName
    );
  });

  if (navMenu) {
    navMenu.classList.remove('open');
  }

  if (navToggle) {
    navToggle.setAttribute(
        'aria-expanded',
        'false'
    );
  }
}

// ==========================================
// BUSCADOR
// ==========================================
searchForm?.addEventListener(
    'submit',
    async (event) => {
      event.preventDefault();

      const query =
          searchInput.value.trim();

      await loadBooks(query);

      showView('catalogo');
    }
);

searchInput?.addEventListener(
    'input',
    async () => {
      const query =
          searchInput.value.trim();

      if (
          query.length === 0 ||
          query.length >= 2
      ) {

        // Limpiar selección de botones
        document
            .querySelectorAll('.btn-filter')
            .forEach((b) =>
                b.classList.remove('active')
            );

        document
            .querySelector(
                '.btn-filter[data-category="Todos"]'
            )
            ?.classList.add('active');

        await loadBooks(query);
      }
    }
);

// ==========================================
// MENÚ RESPONSIVE
// ==========================================
navToggle?.addEventListener(
    'click',
    () => {
      const isOpen =
          navMenu.classList.toggle('open');

      navToggle.setAttribute(
          'aria-expanded',
          String(isOpen)
      );
    }
);

// ==========================================
// NAVEGACIÓN
// ==========================================
navLinks.forEach((link) => {
  link.addEventListener('click', () => {
    showView(link.dataset.view);
  });
});

// ==========================================
// BOTONES DE FILTRO POR CATEGORÍA
// ==========================================
document
    .querySelectorAll('.btn-filter')
    .forEach((button) => {

      button.addEventListener(
          'click',
          async () => {

            document
                .querySelectorAll('.btn-filter')
                .forEach((b) =>
                    b.classList.remove('active')
                );

            button.classList.add('active');

            // Limpiar buscador
            if (searchInput) {
              searchInput.value = '';
            }

            const categoria =
                button.dataset.category;

            await loadBooksByCategory(
                categoria
            );
          }
      );
    });

// ==========================================
// INICIALIZAR APLICACIÓN
// ==========================================
(async function init() {
  await loadStats();
  await loadBooks();
})();

// ==========================================
// NUEVO: ORDENAR CATÁLOGO POR PÁGINAS
// ==========================================
document.querySelectorAll('.btn-sort').forEach((button) => {
  button.addEventListener('click', async () => {
    document
        .querySelectorAll('.btn-sort')
        .forEach((b) => b.classList.remove('active'));
    button.classList.add('active');

    const dir = button.dataset.dir;
    const libros = await fetchJson(`/api/ordenar?dir=${dir}`);
    renderRows(libros);
  });
});

// Consulta las soluciones extraídas por Scala desde las reglas de SWI-Prolog.
const prologButton = document.getElementById('prologRecommendButton');
const prologResults = document.getElementById('prologResults');

prologButton?.addEventListener('click', async () => {
  prologButton.disabled = true;
  prologResults.replaceChildren();
  prologResults.textContent = 'Consultando reglas Prolog...';

  try {
    const recomendaciones = await fetchJson('/api/recomendaciones');
    prologResults.replaceChildren();

    if (recomendaciones.length === 0) {
      prologResults.textContent = 'No hay recomendaciones para el catálogo actual.';
      return;
    }

    recomendaciones.forEach((recomendacion) => {
      const item = document.createElement('article');
      item.className = 'prolog-result';

      const title = document.createElement('strong');
      title.textContent = recomendacion.titulo;
      const details = document.createElement('span');
      details.textContent = `${recomendacion.autor} · ${recomendacion.categoria} · ${recomendacion.paginas} págs.`;
      const reason = document.createElement('p');
      reason.textContent = recomendacion.motivo;

      item.append(title, details, reason);
      prologResults.append(item);
    });
  } catch (error) {
    prologResults.textContent = error.message;
  } finally {
    prologButton.disabled = false;
  }
});