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
    document.body.dataset.sinPermiso = "true";
  }
  return sesion;
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
