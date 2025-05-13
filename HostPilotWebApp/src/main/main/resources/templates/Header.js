(function () {
  function createHeader () {
    const cssHref = 'Header.css';
    if (!document.querySelector(`link[href="${cssHref}"]`)) {
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = cssHref;
      document.head.appendChild(link);
    }

    const biHref = 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css';
    if (!document.querySelector(`link[href^="${biHref}"]`)) {
      const bi = document.createElement('link');
      bi.rel = 'stylesheet';
      bi.href = biHref;
      document.head.appendChild(bi);
    }

    if (!document.querySelector('.hp-header')) {
      document.body.insertAdjacentHTML(
        'afterbegin',
        `<header class="hp-header text-white">
           <div class="container-fluid py-3 px-lg-4 d-flex flex-wrap align-items-center justify-content-between">
             <a href="../templates/Inicio.html" class="d-flex align-items-center text-white text-decoration-none mb-2 mb-lg-0">
               <img src="LogoHP.png" alt="Logo HostPilot" class="logo me-2">
               <span class="fs-5 fw-semibold">HostPilot</span>
             </a>
             <ul class="nav mx-lg-4 flex-wrap">
               <li class="nav-item"><a href="#" class="nav-link px-2 text-white active">Inicio</a></li>
               <li class="nav-item"><a href="#" class="nav-link px-2 text-white">Nosotros</a></li>
               <li class="nav-item"><a href="#" class="nav-link px-2 text-white">Contáctanos</a></li>
               <li class="nav-item"><a href="#" class="nav-link px-2 text-white">Redes</a></li>
               <li class="nav-item"><a href="#" class="nav-link px-2 text-white">Mis reservas</a></li>
             </ul>
             <div class="d-flex gap-2">
               <a href="#" class="btn btn-outline-light position-relative">
                 <i class="bi bi-cart-fill"></i>
               </a>
               <button class="btn btn-outline-light">Iniciar sesión</button>
               <button class="btn btn-warning">Registrarse</button>
             </div>
           </div>
         </header>`
      );
    }
  }

  window.createHeader = createHeader;

  document.addEventListener('DOMContentLoaded', createHeader);
})();
