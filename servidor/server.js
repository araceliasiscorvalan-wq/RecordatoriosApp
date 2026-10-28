
const express = require("express");
const fs = require("fs");
const path = require("path");

const app = express();
const PORT = 3000;

app.use(express.json());

// Archivo donde guardaremos los recordatorios
const ARCHIVO_DATOS = path.join(__dirname, "recordatorios.json");

// Cargar registros guardados
function cargarDatos() {
  if (!fs.existsSync(ARCHIVO_DATOS)) {
    return {};
  }

  return JSON.parse(
    fs.readFileSync(ARCHIVO_DATOS, "utf8")
  );
}

// Guardar registros de forma persistente
function guardarDatos(datos) {
  const temporal = ARCHIVO_DATOS + ".tmp";

  fs.writeFileSync(
    temporal,
    JSON.stringify(datos, null, 2),
    "utf8"
  );

  fs.renameSync(temporal, ARCHIVO_DATOS);
}

let recordatorios = cargarDatos();

// Verificar que el servidor funciona
app.get("/", (req, res) => {
  res.json({
    mensaje: "API de RecordatoriosApp funcionando",
    estado: "OK"
  });
});

// Consultar todos los recordatorios activos
app.get("/api/recordatorios", (req, res) => {
  res.json(
    Object.values(recordatorios).filter(
      (recordatorio) => !recordatorio.eliminado
    )
  );
});

// Consultar un recordatorio
app.get("/api/recordatorios/:clienteId", (req, res) => {
  const recordatorio =
    recordatorios[req.params.clienteId];

  if (!recordatorio || recordatorio.eliminado) {
    return res.status(404).json({
      mensaje: "Recordatorio no encontrado"
    });
  }

  res.json(recordatorio);
});

// Crear o actualizar un recordatorio sin duplicarlo
app.put("/api/recordatorios/:clienteId", (req, res) => {
  const clienteId = req.params.clienteId;
  const datos = req.body;

  if (
    !datos ||
    datos.clienteId !== clienteId ||
    typeof datos.titulo !== "string" ||
    !datos.titulo.trim() ||
    typeof datos.categoria !== "string" ||
    typeof datos.prioridad !== "string" ||
    typeof datos.estado !== "string"
  ) {
    return res.status(400).json({
      mensaje: "Datos del recordatorio inválidos"
    });
  }

  const anterior = recordatorios[clienteId];

  const recordatorio = {
    id: anterior?.id || clienteId,
    clienteId,
    titulo: datos.titulo,
    descripcion: datos.descripcion ?? null,
    categoria: datos.categoria,
    prioridad: datos.prioridad,
    estado: datos.estado,
    fecha: datos.fecha ?? anterior?.fecha ?? Date.now(),
    eliminado: false
  };

  recordatorios[clienteId] = recordatorio;
  guardarDatos(recordatorios);

  res.status(anterior ? 200 : 201).json(recordatorio);
});

// Eliminar usando el mismo identificador
app.delete("/api/recordatorios/:clienteId", (req, res) => {
  const clienteId = req.params.clienteId;

  // Conservamos una marca de eliminación
  // para que repetir DELETE sea seguro.
  const anterior = recordatorios[clienteId];

  recordatorios[clienteId] = {
    ...(anterior || {}),
    id: anterior?.id || clienteId,
    clienteId,
    eliminado: true
  };

  guardarDatos(recordatorios);

  res.status(204).send();
});

// Iniciar servidor local de pruebas
app.listen(PORT, "0.0.0.0", () => {
  console.log(
    `API de RecordatoriosApp funcionando en puerto ${PORT}`
  );
});

