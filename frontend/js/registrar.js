// Endpoint de la historia de usuario 1 (API_BASE viene de comun.js)
const API_URL = `${API_BASE}/api/v1/usuarios`;

const formulario = document.getElementById("formulario");
const boton = document.getElementById("botonRegistrar");
const mensaje = document.getElementById("mensaje");
const resultado = document.getElementById("resultado");
const resumen = document.getElementById("resumen");
const salario = document.getElementById("salario_base");
const salarioAyuda = document.getElementById("salarioAyuda");

const ICONO_EXITO = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m8 12 3 3 5-6"/></svg>';
const ICONO_ERROR = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 8v4M12 16h.01"/></svg>';

const pesos = (valor) => `$ ${Number(valor).toLocaleString("es-CO")}`;

// Mientras se escribe el salario, se muestra con puntos de miles para leerlo fácil
salario.addEventListener("input", () => {
  salarioAyuda.textContent = salario.value === ""
    ? "Entre $0 y $15.000.000"
    : `${pesos(salario.value)} · Entre $0 y $15.000.000`;
});

// Convierte un texto vacío en null, para que la API reciba "sin dato" y no un texto vacío
function valorONull(id) {
  const valor = document.getElementById(id).value.trim();
  return valor === "" ? null : valor;
}

// Arma el JSON con los mismos nombres que espera la API (snake_case)
function leerFormulario() {
  return {
    nombres: document.getElementById("nombres").value,
    apellidos: document.getElementById("apellidos").value,
    fecha_nacimiento: valorONull("fecha_nacimiento"),
    direccion: valorONull("direccion"),
    telefono: valorONull("telefono"),
    correo_electronico: document.getElementById("correo_electronico").value.trim(),
    salario_base: salario.value === "" ? null : Number(salario.value),
  };
}

function mostrarMensaje(tipo, titulo, texto) {
  mensaje.innerHTML = "";
  const caja = document.createElement("div");
  caja.className = `mensaje mensaje--${tipo}`;
  caja.innerHTML = tipo === "exito" ? ICONO_EXITO : ICONO_ERROR;
  const contenido = document.createElement("div");
  const fuerte = document.createElement("strong");
  fuerte.textContent = titulo;
  contenido.append(fuerte, document.createTextNode(texto));
  caja.appendChild(contenido);
  mensaje.appendChild(caja);
}

function limpiarErrores() {
  document.querySelectorAll(".con-error").forEach((input) => input.classList.remove("con-error"));
}

// Marca en rojo el campo que menciona el mensaje de error de la API
function resaltarCampo(textoError) {
  limpiarErrores();
  const texto = textoError.toLowerCase();
  const reglas = [
    ["nombres", "nombres"],
    ["apellidos", "apellidos"],
    ["correo", "correo_electronico"],
    ["salario", "salario_base"],
  ];
  for (const [palabra, id] of reglas) {
    if (texto.includes(palabra)) {
      const input = document.getElementById(id);
      input.classList.add("con-error");
      input.focus();
      return;
    }
  }
}

function formatearFecha(fechaIso) {
  if (!fechaIso) return null;
  const [anio, mes, dia] = fechaIso.split("-");
  return `${dia}/${mes}/${anio}`;
}

function mostrarUsuarioCreado(usuario) {
  const iniciales = `${usuario.nombres?.trim()[0] ?? ""}${usuario.apellidos?.trim()[0] ?? ""}`;
  document.getElementById("iniciales").textContent = iniciales.toUpperCase();
  document.getElementById("nombreCompleto").textContent = `${usuario.nombres} ${usuario.apellidos}`;
  document.getElementById("correoResultado").textContent = usuario.correo_electronico;

  const filas = [
    ["Id", usuario.id],
    ["Salario base", pesos(usuario.salario_base)],
    ["Fecha de nacimiento", formatearFecha(usuario.fecha_nacimiento)],
    ["Teléfono", usuario.telefono],
    ["Dirección", usuario.direccion],
  ];
  resumen.innerHTML = "";
  for (const [etiqueta, valor] of filas) {
    const bloque = document.createElement("div");
    const dt = document.createElement("dt");
    dt.textContent = etiqueta;
    const dd = document.createElement("dd");
    dd.textContent = valor ?? "—";
    bloque.append(dt, dd);
    resumen.appendChild(bloque);
  }
  formulario.hidden = true;
  resultado.hidden = false;
}

formulario.addEventListener("submit", async (evento) => {
  evento.preventDefault();
  limpiarErrores();
  boton.disabled = true;
  boton.textContent = "Registrando…";

  try {
    const respuesta = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(leerFormulario()),
    });
    const datos = await respuesta.json().catch(() => ({}));

    if (respuesta.status === 201) {
      mostrarMensaje("exito", "¡Solicitante registrado!", ` Quedó guardado con el id ${datos.id}.`);
      mostrarUsuarioCreado(datos);
    } else {
      const textoError = datos.error || "No se pudo registrar el solicitante.";
      mostrarMensaje("error", `Error ${respuesta.status}`, ` ${textoError}`);
      resaltarCampo(textoError);
    }
  } catch (error) {
    // fetch falla así cuando la API está apagada o el navegador bloquea la petición (CORS)
    mostrarMensaje(
      "error",
      "No se pudo conectar con la API",
      " Verifica que la aplicación esté corriendo en http://localhost:8080 (./gradlew bootRun)."
    );
  } finally {
    boton.disabled = false;
    boton.textContent = "Registrar solicitante";
  }
});

document.getElementById("registrarOtro").addEventListener("click", () => {
  formulario.reset();
  salarioAyuda.textContent = "Entre $0 y $15.000.000";
  mensaje.innerHTML = "";
  resultado.hidden = true;
  formulario.hidden = false;
  document.getElementById("nombres").focus();
});
