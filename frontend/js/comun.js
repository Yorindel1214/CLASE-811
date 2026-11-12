// Dirección base de la API (el backend Spring Boot que corre con ./gradlew bootRun)
const API_BASE = "http://localhost:8080";

// Revisa si la API responde y actualiza el indicador de la cabecera
async function verificarApi() {
  const indicador = document.getElementById("estadoApi");
  if (!indicador) return;
  const texto = indicador.querySelector(".estado-api__texto");

  try {
    const respuesta = await fetch(`${API_BASE}/actuator/health`);
    if (respuesta.ok) {
      indicador.className = "estado-api estado-api--ok";
      texto.textContent = "API en línea";
    } else {
      // La API responde, pero algo falla (por ejemplo, la base de datos apagada)
      indicador.className = "estado-api estado-api--error";
      texto.textContent = "API con problemas";
    }
  } catch (error) {
    indicador.className = "estado-api estado-api--error";
    texto.textContent = "API sin conexión";
  }
}

verificarApi();
setInterval(verificarApi, 15000);
