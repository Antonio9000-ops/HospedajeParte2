package com.hostpilot.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * /reservas  – muestra el grid de apartamentos y el mapa interactivo.
 */
@WebServlet("/reservas")
public class ReservasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("""
        <!DOCTYPE html>
        <html lang="es">
        <head>
          <meta charset="UTF-8"/>
          <meta name="viewport" content="width=device-width, initial-scale=1"/>
          <title>Reservas</title>

          <!-- Bootstrap -->
          <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
          <!-- Leaflet -->
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
          <!-- Header -->
          <link rel="stylesheet" href="Header.css"/>
          <script src="Header.js" defer></script>
          <!-- Hoja principal -->
          <link rel="stylesheet" href="estilo4.css"/>

          <style>
            .page-layout{display:flex;width:100%;height:calc(100vh - 72px);}
            .left-col{flex:0 0 60%;height:100%;overflow-y:auto;}
            .right-col{flex:0 0 40%;height:100%;}
            .airbnb-wrapper{background:#fff;margin:2rem;padding:2rem;border-radius:12px;box-shadow:0 6px 16px rgba(0,0,0,.08);}
            .ap-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:1.5rem;}
            @media(max-width:767.98px){.ap-grid{grid-template-columns:repeat(2,1fr);}}
            .ap-link{display:block;text-decoration:none;color:inherit;}
            .ap-card{display:flex;flex-direction:column;height:100%;transition:transform .15s,box-shadow .15s;}
            .ap-link:hover .ap-card{transform:translateY(-3px);box-shadow:0 6px 16px rgba(0,0,0,.12);}
            .ap-card img{width:100%;height:100px;object-fit:cover;border-radius:10px;}
            .rating{font-weight:600;color:#000;}
            .price{font-weight:700;color:#5A31F4;}
            #map{height:100%;width:100%;}
          </style>
        </head>

        <body>
          <div class="page-layout">
            <!-- IZQUIERDA -->
            <div class="left-col">
              <div class="airbnb-wrapper">
                <h2 class="text-center mb-4">Alojamientos recomendados</h2>
                <div class="ap-grid" id="apartmentGrid">
        """);

        /* ---------- 16 TARJETAS ---------- */
        String[][] cards = {
            {"apt1","https://hips.hearstapps.com/hmg-prod/images/6-antiguo-almacen-convertido-loft-muebles-diseno-vintage-salon-vigas-madera-1660741511.jpg?resize=980:*","4.9","Loft moderno","Lima, Perú","240"},
            {"apt2","https://nexoinmobiliario.pe/blog/wp-content/uploads/2023/01/ventana-vista-frente-mar-nexo-inmobiliario.jpg","4.7","Departamento frente al mar","Máncora, Perú","320"},
            {"apt3","https://imagenes.20minutos.es/files/image_640_480/files/fp/uploads/imagenes/2023/01/27/vistas-panoramicas-desde-el-salon-del-atico-del-mandarin-oriental-residences-en-barcelona.r_d.804-378-6201.jpeg","4.8","Ático con vista panorámica","Cusco, Perú","450"},
            {"apt4","https://content.elmueble.com/medio/2021/10/14/dsc2037-8d39c55e-1200x801_d4f42dae_1200x801.jpg","5.0","Casa rústica acogedora","Arequipa, Perú","280"},
            {"apt5","https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=720&q=80","4.6","Bungalow ecológico","Tarapoto, Perú","310"},
            {"apt6","https://dbdzm869oupei.cloudfront.net/img/photomural/preview/56054.png","4.7","Cabaña junto al lago","Puno, Perú","350"},
            {"apt7","https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=720&q=80","4.5","Estudio céntrico","Trujillo, Perú","190"},
            {"apt8","https://cuscoperu.b-cdn.net/wp-content/uploads/2023/04/Mesa-de-trabajo-1casa-inca-gartcilaso.jpg","4.8","Casa colonial restaurada","Cajamarca, Perú","300"},
            {"apt9","https://www.hola.com/horizon/original_aspect_ratio/f0dc8c69c16f-loft-antrax-1a-a.jpg","4.6","Loft industrial","Arequipa, Perú","260"},
            {"apt10","https://nexoinmobiliario.pe/blog/wp-content/uploads/2022/05/7-ventajas-comprar-departamento-terraza.jpg","4.7","Departamento con terraza","Lima (Barranco), Perú","380"},
            {"apt11","https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&w=720&q=80","4.9","Glamping de lujo","Paracas, Perú","420"},
            {"apt12","https://cf.bstatic.com/xdata/images/hotel/max1024x768/462138725.jpg?k=abfa758a5dbf5a0bb18c72762de7b474029d63676f91e90482cbdb8ef249133f&o=&hp=1","4.8","Domo andino","Huaraz, Perú","330"},
            {"apt13","https://images.unsplash.com/photo-1507089947368-19c1da9775ae?auto=format&fit=crop&w=720&q=80","4.5","Villa tropical","Zorritos, Perú","390"},
            {"apt14","https://i.pinimg.com/564x/b5/04/e6/b504e6cf976c938fbcaab44b397cfa6b.jpg","4.4","Mini suite moderna","Chiclayo, Perú","200"},
            {"apt15","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaHeH2ExyAypFlX7Zgh1Is3xeUPYDoMk4N6g&s","4.8","Chalet alpino","Oxapampa, Perú","310"},
            {"apt16","https://images.unsplash.com/photo-1587502536263-7e75ee7c2c7b?auto=format&fit=crop&w=720&q=80","4.6","Casa de playa moderna","Punta Hermosa, Perú","370"}
        };

        for (String[] c : cards) {
            out.printf("""
              <a class="ap-link" href="" data-id="%s">
                <div class="ap-card">
                  <img src="%s" alt="%s">
                  <div class="card-body">
                    <div class="rating">&#9733;&nbsp;%s</div>
                    <strong>%s</strong><br>
                    <span>%s</span><br>
                    <span class="price">S/&nbsp;%s&nbsp;/noche</span>
                  </div>
                </div>
              </a>
            """, c[0], c[1], c[4], c[2], c[3], c[4], c[5]);
        }

        /* Cierre de columnas y contenedores */
        out.println("""
                </div><!-- ap-grid -->
              </div><!-- airbnb-wrapper -->
            </div><!-- left-col -->

            <!-- DERECHA -->
            <div class="right-col">
              <div id="map"></div>
            </div>
          </div><!-- page-layout -->

          <!-- SCRIPTS -->
          <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

          <script>
            const apartments = [
              { id:'apt1', name:'Loft moderno',           lat:-12.0464, lng:-77.0428 },
              { id:'apt2', name:'Depto frente al mar',    lat:-4.1089, lng:-81.1528 },
              { id:'apt3', name:'Ático panorámico',       lat:-13.5320, lng:-71.9675 },
              { id:'apt4', name:'Casa rústica acogedora', lat:-16.4090, lng:-71.5375 },
              { id:'apt5', name:'Bungalow ecológico',     lat:-6.4776, lng:-76.3730 },
              { id:'apt6', name:'Cabaña junto al lago',   lat:-15.8402, lng:-70.0219 },
              { id:'apt7', name:'Estudio céntrico',       lat:-8.1120, lng:-79.0280 },
              { id:'apt8', name:'Casa colonial',          lat:-7.1619, lng:-78.5100 },
              { id:'apt9', name:'Loft industrial',        lat:-16.3989, lng:-71.5350 },
              { id:'apt10',name:'Depto con terraza',      lat:-12.1446, lng:-77.0212 },
              { id:'apt11',name:'Glamping de lujo',       lat:-13.8331, lng:-76.2707 },
              { id:'apt12',name:'Domo andino',            lat:-9.5278, lng:-77.5278 },
              { id:'apt13',name:'Villa tropical',         lat:-3.6818, lng:-80.8631 },
              { id:'apt14',name:'Mini suite moderna',     lat:-6.7735, lng:-79.8440 },
              { id:'apt15',name:'Chalet alpino',          lat:-10.5761, lng:-75.4011 },
              { id:'apt16',name:'Casa de playa moderna',  lat:-12.3227, lng:-76.7864 }
            ];

            const map = L.map('map').setView([-9.19, -75.0152], 5);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              attribution: '© OpenStreetMap'
            }).addTo(map);

            apartments.forEach(ap => {
              const marker = L.marker([ap.lat, ap.lng]).addTo(map)
                             .bindPopup(`<strong>${ap.name}</strong>`);
              marker.on('click', () => highlightCard(ap.id));
            });

            function highlightCard(id) {
              document.querySelectorAll('.ap-card').forEach(c => c.classList.remove('border', 'border-primary'));
              const card = document.querySelector(`[data-id="${id}"] .ap-card`);
              if (card) {
                card.classList.add('border', 'border-primary');
                card.scrollIntoView({ behavior: 'smooth', block: 'center' });
              }
            }

            // Atenúa tarjetas fuera de un radio de 3 km al hacer clic en el mapa
            map.on('click', e => {
              const radius = 3;
              apartments.forEach(ap => {
                const dist = map.distance(e.latlng, L.latLng(ap.lat, ap.lng)) / 1000;
                toggleCard(ap.id, dist <= radius);
              });
            });

            function toggleCard(id, show) {
              const link = document.querySelector(`[data-id="${id}"]`);
              if (link) link.style.opacity = show ? '1' : '0.25';
            }
          </script>
        </body>
        </html>
        """);
    }
}
