// HU3: manejo de la sesión en el navegador. El token (JWT) se guarda en localStorage
// y se envía en la cabecera Authorization de cada petición protegida.
const CLAVE_SESION = "crediya.sesion";

function guardarSesion(datos) {
  localStorage.setItem(CLAVE_SESION, JSON.stringify({
    token: datos.token,
    correo: datos.correo_electronico,
    rol: datos.rol,
    expira: Date.now() + datos.expira_en * 1000,
  }));
}

function obtenerSesion() {
  try {
    const sesion = JSON.parse(localStorage.getItem(CLAVE_SESION));
    if (!sesion || Date.now() > sesion.expira) return null;
    return sesion;
  } catch (e) {
    return null;
  }
}

function cerrarSesion() {
  localStorage.removeItem(CLAVE_SESION);
  window.location.href = "login.html";
}

// Cabeceras para llamar a la API con el token
function cabecerasConToken() {
  const sesion = obtenerSesion();
  return {
    "Content-Type": "application/json",
    ...(sesion ? { Authorization: `Bearer ${sesion.token}` } : {}),
  };
}

// Si no hay sesión (o el rol no sirve para esta página) se envía al login
function requerirSesion(rolesPermitidos) {
  const sesion = obtenerSesion();
  if (!sesion) {
    window.location.href = `login.html?volver=${encodeURIComponent(location.pathname.split("/").pop())}`;
    return null;
  }
  if (rolesPermitidos && !rolesPermitidos.includes(sesion.rol)) {
    bloquearPagina(sesion.rol, rolesPermitidos);
    return null;
  }
  return sesion;
}

// A dónde se sugiere ir según el rol cuando la página no le corresponde
const DESTINO_POR_ROL = {
  CLIENTE: { texto: "Ir a solicitar un préstamo", url: "http://localhost:8001/login.html" },
};

// Oculta el contenido de la página y muestra un aviso de permisos.
// Es solo para la experiencia de usuario: la seguridad real la aplica la API (responde 403).
function bloquearPagina(rol, rolesPermitidos) {
  const main = document.querySelector("main");
  if (!main) return;
  for (const hijo of main.children) hijo.hidden = true;

  const panel = document.createElement("section");
  panel.className = "panel acceso-denegado";
  const titulo = document.createElement("h1");
  titulo.textContent = "No tienes permiso para esta página";
  const texto = document.createElement("p");
  texto.textContent = `Iniciaste sesión con rol ${rol}. Esta página es solo para: ${rolesPermitidos.join(" o ")}.`;
  const acciones = document.createElement("div");
  acciones.className = "acceso-denegado__acciones";
  const destino = DESTINO_POR_ROL[rol];
  if (destino) {
    const enlace = document.createElement("a");
    enlace.className = "boton";
    enlace.href = destino.url;
    enlace.textContent = destino.texto;
    acciones.appendChild(enlace);
  }
  const salir = document.createElement("button");
  salir.type = "button";
  salir.className = "boton boton--fantasma";
  salir.textContent = "Cerrar sesión y entrar con otro usuario";
  salir.addEventListener("click", cerrarSesion);
  acciones.appendChild(salir);
  panel.append(titulo, texto, acciones);
  main.prepend(panel);
}

// Muestra quién está conectado y el botón para salir (si la página tiene #usuarioSesion)
function pintarSesion() {
  const caja = document.getElementById("usuarioSesion");
  if (!caja) return;
  const sesion = obtenerSesion();
  caja.innerHTML = "";
  if (!sesion) {
    const enlace = document.createElement("a");
    enlace.href = "login.html";
    enlace.textContent = "Iniciar sesión";
    caja.appendChild(enlace);
    return;
  }
  const texto = document.createElement("span");
  texto.textContent = `${sesion.correo} · ${sesion.rol}`;
  const salir = document.createElement("button");
  salir.type = "button";
  salir.className = "boton boton--fantasma boton--mini";
  salir.textContent = "Salir";
  salir.addEventListener("click", cerrarSesion);
  caja.append(texto, salir);
}

pintarSesion();
