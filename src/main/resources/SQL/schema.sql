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
    fecha DATE NOT NULL
);

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
    FOREIGN KEY (idAnalisisF)
        REFERENCES AnalisisFinanciero(idAnalisisF)
);

CREATE TABLE IF NOT EXISTS Ingreso (
    idIngreso INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255),
    valor INT NOT NULL,
    idAnalisisF INT,
    FOREIGN KEY (idAnalisisF)
        REFERENCES AnalisisFinanciero(idAnalisisF)
);

CREATE TABLE IF NOT EXISTS Boleteria (
    idBoleteria INT AUTO_INCREMENT PRIMARY KEY,
    seccion VARCHAR(255),
    cantidad INT,
    precioBoleta INT,
    ingresoTotal INT,
    idAnalisisF INT,
    FOREIGN KEY (idAnalisisF)
        REFERENCES AnalisisFinanciero(idAnalisisF)
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

CREATE TABLE IF NOT EXISTS RolConciertoUsuario (
        idRol INT,
        idUsuario INT,
        idConcierto INT,
        PRIMARY KEY (idRol, idUsuario, idConcierto),
        FOREIGN KEY (idRol) REFERENCES Rol(idRol),
        FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
        FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto)
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

-- Insertar roles por defecto
MERGE INTO Rol (idRol, rol) KEY(idRol)
    VALUES (0, 'Sin rol'),(1, 'Administrador'), (2, 'Tecnico'), (3, 'Manager'), (4, 'Staff');

-----------------------------------
--DATOS DE PRUEBA
-----------------------------------

-----------------------------------
-- DATOS DE PRUEBA
-----------------------------------

-- 1. USUARIOS
MERGE INTO Usuario (idUsuario, nombre, gmail, contrasena, idRol, telefono, direccion, contactoEmergenciaNombre, contactoEmergenciaTelefono, contactoEmergenciaRelacion) KEY(idUsuario)
    VALUES
    (1, 'Admin', 'admin@gestionconcierto.com', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 1, '314234123', 'Calle 100 #48-90', 'Aseguradora ALIANZA', '310233211', 'Aseguradora'),
    (2, 'Feid', 'feid@vidaloka.com', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 3, '310000221', 'Calle 44 #22-01', 'Centrals Seguros', '324231231', 'Aseguradora'),
    (3, 'Pepe', 'pepe@cloro.co', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 2, '312324123', 'Calle 1 #32-09', 'Abuelita Marta', '3111211', 'Abuela');


ALTER TABLE Usuario ALTER COLUMN idUsuario RESTART WITH 4;

-- 2. HORARIOS (Deben ir antes del concierto)
MERGE INTO Horario (idHorario, fechaInc, fechaFin, horaInc, horaFin) KEY (idHorario)
    VALUES
    (1, '2026-05-01', '2026-05-01', '20:00:00', '01:00:00'),
    (2, '2026-05-04', '2026-05-05', '20:00:00', '01:00:00'),
    (3, '2026-07-05', '2026-07-07', '21:00:00', '03:00:00');

ALTER TABLE Horario ALTER COLUMN idHorario RESTART WITH 3;

-- 3. CONTRATOS (Deben ir antes del concierto y cláusulas)
MERGE INTO Contrato (idContrato, fecha) KEY (idContrato)
    VALUES
    (1, '2026-04-01'),
    (2, '2026-04-01'),
    (3, '2026-06-05');

ALTER TABLE Contrato ALTER COLUMN idContrato RESTART WITH 3;

-- 4. CLAUSULAS
MERGE INTO Clausula (idClausula, clausula, idContrato) KEY (idClausula)
    VALUES
    (1, 'Quiero que me paguen los primeros $100.000USD por adelantado', 1),
    (2, 'Quiero un latte despues del concierto', 1),
    (3, 'Quiero un chocolate blanco antes de cantar', 1),
    (4, 'Quiero ir al zoo despues del concierto', 2),
    (5, 'Quiero $2.000USD en caramelos', 2);

ALTER TABLE Clausula ALTER COLUMN idClausula RESTART WITH 6;

-- 5. CONCIERTOS (referenciando Horario y Contrato existentes)
MERGE INTO Concierto (idConcierto, nombreConcierto, idHorario, aforo, idContrato, programado, idAnalisisF) KEY(idConcierto)
    VALUES
    (1, 'Fin del Mundo Loko', 1, 100001, 1, FALSE, null),
    (2, 'Hola Mundo', 1, 100001, 1, FALSE, null),
    (3, 'Vida loka', 2, 35000, 2, TRUE, null);

ALTER TABLE Concierto ALTER COLUMN idConcierto RESTART WITH 3;

-- 6. TABLAS INTERMEDIAS
-- 3 = Manager/Artista
MERGE INTO RolConciertoUsuario (idRol, idUsuario, idConcierto) KEY (idRol, idUsuario, idConcierto)
    VALUES
    (3, 2, 1), -- Asigna a Feid (Usuario 2) al Concierto 1 con Rol 3
    (3, 2, 2); -- Asigna a Feid (Usuario 2) al Concierto 2 con Rol 3

MERGE INTO TipoObjeto (tipo) KEY(tipo) VALUES
    ('Micrófono'),
    ('Parlante'),
    ('Cable XLR'),
    ('Cable de poder'),
    ('Consola de mezcla'),
    ('Amplificador'),
    ('Monitor de escenario'),
    ('Pantalla LED'),
    ('Proyector'),
    ('Soporte de micrófono'),
    ('Rack de audio'),
    ('Interfaz de audio'),
    ('Sistema in-ear'),
    ('Luces LED'),
    ('Generador eléctrico');

MERGE INTO Concierto (idConcierto, nombreConcierto, aforo, programado)
    KEY(idConcierto)
    VALUES (0, 'mantenimiento', 0, FALSE);