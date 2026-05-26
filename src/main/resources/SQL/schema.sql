-- schema.sql
-- Creación de la estructura de la base de datos para la Gestión de Conciertos

CREATE TABLE IF NOT EXISTS Rol (
                                   idRol INT AUTO_INCREMENT PRIMARY KEY,
                                   rol VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS Usuario (
                                       idUsuario INT AUTO_INCREMENT PRIMARY KEY,
                                       nombre VARCHAR(255) NOT NULL,
    gmail VARCHAR(255),
    contrasena VARCHAR(255),
    idRol INT DEFAULT 0,
    telefono VARCHAR(10),
    direccion VARCHAR(255),
    contactoEmergenciaNombre VARCHAR(255),
    contactoEmergenciaTelefono VARCHAR(20),
    contactoEmergenciaRelacion VARCHAR(100),
    FOREIGN KEY (idRol) REFERENCES Rol(idRol)
    );

ALTER TABLE Usuario
    ADD COLUMN IF NOT EXISTS idRol INT DEFAULT 0;

CREATE TABLE IF NOT EXISTS Contrato (
                                        idContrato INT AUTO_INCREMENT PRIMARY KEY,
                                        fecha DATE NOT NULL,
                                        estadoFirma VARCHAR(255) DEFAULT 'PENDIENTE'
);

ALTER TABLE Contrato
    ADD COLUMN IF NOT EXISTS estadoFirma VARCHAR(255) DEFAULT 'PENDIENTE';

CREATE TABLE IF NOT EXISTS Clausula (
                                        idClausula INT AUTO_INCREMENT PRIMARY KEY,
                                        clausula VARCHAR(255) NOT NULL,
    idContrato INT,
    FOREIGN KEY (idContrato) REFERENCES Contrato(idContrato)
    );

CREATE TABLE IF NOT EXISTS AnalisisFinanciero (
                                                  idAnalisisF INT AUTO_INCREMENT PRIMARY KEY,
                                                  presupuesto INT NOT NULL,
                                                  aprobado BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS Gasto (
                                     idGasto INT AUTO_INCREMENT PRIMARY KEY,
                                     descripcion VARCHAR(255),
    valor INT NOT NULL,
    idAnalisisF INT,
    FOREIGN KEY (idAnalisisF) REFERENCES AnalisisFinanciero(idAnalisisF)
    );

CREATE TABLE IF NOT EXISTS Ingreso (
                                       idIngreso INT AUTO_INCREMENT PRIMARY KEY,
                                       descripcion VARCHAR(255),
    valor INT NOT NULL,
    idAnalisisF INT,
    FOREIGN KEY (idAnalisisF) REFERENCES AnalisisFinanciero(idAnalisisF)
    );

CREATE TABLE IF NOT EXISTS Boleteria (
                                         idBoleteria INT AUTO_INCREMENT PRIMARY KEY,
                                         seccion VARCHAR(255),
    cantidad INT,
    precioBoleta INT,
    ingresoTotal INT,
    idAnalisisF INT,
    FOREIGN KEY (idAnalisisF) REFERENCES AnalisisFinanciero(idAnalisisF)
    );

CREATE TABLE IF NOT EXISTS Horario (
                                       idHorario INT AUTO_INCREMENT PRIMARY KEY,
                                       fechaInc DATE NOT NULL,
                                       fechaFin DATE NOT NULL,
                                       horaInc TIME NOT NULL,
                                       horaFin TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS ReferenciaDeObjeto (
                                                  idReferenciaObjeto INT AUTO_INCREMENT PRIMARY KEY,
                                                  referencia VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS TipoObjeto (
                                          idTipoObjeto INT AUTO_INCREMENT PRIMARY KEY,
                                          tipo VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS Objeto (
                                      idObjeto INT AUTO_INCREMENT PRIMARY KEY,
                                      idTipoObjeto INT NOT NULL,
                                      idReferenciaObjeto INT NOT NULL,
                                      FOREIGN KEY (idTipoObjeto) REFERENCES TipoObjeto(idTipoObjeto),
    FOREIGN KEY (idReferenciaObjeto) REFERENCES ReferenciaDeObjeto(idReferenciaObjeto)
    );

CREATE TABLE IF NOT EXISTS ObjetoInventario (
                                                idInventario INT AUTO_INCREMENT PRIMARY KEY,
                                                idTipoObjeto INT,
                                                FOREIGN KEY (idTipoObjeto) REFERENCES TipoObjeto(idTipoObjeto)
    );

CREATE TABLE IF NOT EXISTS Concierto (
                                         idConcierto INT AUTO_INCREMENT PRIMARY KEY,
                                         nombreConcierto VARCHAR(255) NOT NULL,
    idHorario INT,
    aforo INT NOT NULL,
    idContrato INT,
    programado BOOLEAN NOT NULL,
    idAnalisisF INT,
    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario),
    FOREIGN KEY (idContrato) REFERENCES Contrato(idContrato),
    FOREIGN KEY (idAnalisisF) REFERENCES AnalisisFinanciero(idAnalisisF)
    );

CREATE TABLE IF NOT EXISTS HorarioUsuario (
                                              idUsuario INT,
                                              idHorario INT,
                                              PRIMARY KEY (idUsuario, idHorario),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario)
    );

CREATE TABLE IF NOT EXISTS ConciertoInventario (
                                                   idInventario INT,
                                                   idConcierto INT,
                                                   PRIMARY KEY (idInventario, idConcierto),
    FOREIGN KEY (idInventario) REFERENCES ObjetoInventario(idInventario),
    FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto)
    );

-- Tabla que almacena los subroles disponibles para usuarios con rol Staff
CREATE TABLE IF NOT EXISTS Subrol (
                                      idSubrol INT AUTO_INCREMENT PRIMARY KEY,
                                      nombre VARCHAR(100) NOT NULL
    );

-- Insertar subroles por defecto
MERGE INTO Subrol (idSubrol, nombre) KEY(idSubrol)
    VALUES (1, 'Sonido'), (2, 'Luces'), (3, 'Seguridad'), (4, 'Logística'), (5, 'Producción');

-- Tabla principal de asignaciones (ROL + USUARIO + CONCIERTO)
CREATE TABLE IF NOT EXISTS RolConciertoUsuario (
                                                   idRol INT,
                                                   idUsuario INT,
                                                   idConcierto INT,
                                                   idSubrol INT,                    -- ← Columna agregada
                                                   PRIMARY KEY (idRol, idUsuario, idConcierto),
    FOREIGN KEY (idRol) REFERENCES Rol(idRol),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
    FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto),
    FOREIGN KEY (idSubrol) REFERENCES Subrol(idSubrol)
    );

-- Migración para bases ya existentes que no tenían idSubrol
ALTER TABLE RolConciertoUsuario
    ADD COLUMN IF NOT EXISTS idSubrol INT;

-- Tabla principal de actividades / notificaciones del sistema
CREATE TABLE IF NOT EXISTS ActividadSistema (
                                                idActividad INT AUTO_INCREMENT PRIMARY KEY,
                                                tipo VARCHAR(50) NOT NULL,
    modulo VARCHAR(100) NOT NULL,
    origen VARCHAR(100) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    fechaHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    idUsuarioActor INT,
    rolDestino VARCHAR(255),
    FOREIGN KEY (idUsuarioActor) REFERENCES Usuario(idUsuario)
    );

-- Estado individual de cada actividad para cada usuario
CREATE TABLE IF NOT EXISTS EstadoActividadUsuario (
                                                      idActividad INT NOT NULL,
                                                      idUsuario INT NOT NULL,
                                                      revisado BOOLEAN DEFAULT FALSE NOT NULL,
                                                      fechaRevision TIMESTAMP,
                                                      PRIMARY KEY (idActividad, idUsuario),
    FOREIGN KEY (idActividad) REFERENCES ActividadSistema(idActividad) ON DELETE CASCADE,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS DocumentoInventario (
                                                   idDocumentoInventario INT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS ObjetoDocumentoInventario (
                                                         idInventario INT,
                                                         idObjeto INT,
                                                         FOREIGN KEY (idInventario) REFERENCES DocumentoInventario(idDocumentoInventario),
    FOREIGN KEY (idObjeto) REFERENCES Objeto(idObjeto)
    );

CREATE TABLE IF NOT EXISTS DocumentoInventarioHorario (
                                                          idDocumentoInventario INT,
                                                          idHorario INT,
                                                          PRIMARY KEY (idDocumentoInventario, idHorario),
    FOREIGN KEY (idDocumentoInventario) REFERENCES DocumentoInventario(idDocumentoInventario),
    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario)
    );

CREATE TABLE IF NOT EXISTS ConciertoDocumentoInventario (
                                                            idDocumentoInventario INT,
                                                            idConcierto INT,
                                                            PRIMARY KEY (idDocumentoInventario, idConcierto),
    FOREIGN KEY (idDocumentoInventario) REFERENCES DocumentoInventario(idDocumentoInventario),
    FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto)
    );

CREATE TABLE IF NOT EXISTS Nomina (
    idNomina INT AUTO_INCREMENT PRIMARY KEY,
    idConcierto INT NOT NULL,
    idUsuario INT NOT NULL,
    rol VARCHAR(100),
    horasTrabajadas DOUBLE NOT NULL,
    tarifaPorHora DOUBLE NOT NULL,
    horasExtra DOUBLE DEFAULT 0,
    total DOUBLE NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    pagado BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- Insertar roles por defecto
MERGE INTO Rol (idRol, rol) KEY(idRol)
    VALUES (0, 'Sin rol'), (1, 'Administrador'), (2, 'Tecnico'), (3, 'Manager'), (4, 'Staff');

MERGE INTO TipoObjeto (tipo) KEY(tipo)
    VALUES ('Micrófono'), ('Parlante'), ('Cable XLR'), ('Cable de poder'), ('Consola de mezcla'),
    ('Amplificador'), ('Monitor de escenario'), ('Pantalla LED'), ('Proyector'),
    ('Soporte de micrófono'), ('Rack de audio'), ('Interfaz de audio'),
    ('Sistema in-ear'), ('Luces LED'), ('Generador eléctrico');

MERGE INTO Concierto (idConcierto, nombreConcierto, aforo, programado)
    KEY(idConcierto) VALUES (0, 'mantenimiento', 0, FALSE);