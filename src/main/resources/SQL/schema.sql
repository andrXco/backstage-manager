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
    gastos INT NOT NULL
);

CREATE TABLE IF NOT EXISTS Horario (
    idHorario INT AUTO_INCREMENT PRIMARY KEY,
    fechaInc DATE NOT NULL,
    fechaFin DATE NOT NULL,
    horaInc TIME NOT NULL,
    horaFin TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS TipoObjeto (
        idTipoObjeto INT AUTO_INCREMENT PRIMARY KEY,
        nombre VARCHAR(255)
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

CREATE TABLE IF NOT EXISTS RolConciertoUsuario (
        idRol INT,
        idUsuario INT,
        idConcierto INT,
        PRIMARY KEY (idRol, idUsuario, idConcierto),
        FOREIGN KEY (idRol) REFERENCES Rol(idRol),
        FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
        FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto)
    );

-- Insertar roles por defecto
MERGE INTO Rol (idRol, rol) KEY(idRol)
    VALUES (0, 'Sin rol'),(1, 'Administrador'), (2, 'Tecnico'), (3, 'Manager'), (4, 'Staff');

-----------------------------------
--DATOS DE PRUEBA
-----------------------------------

---USUARIO---

-- Insertar los usuarios por defecto, las contraseñas son "12345678"
MERGE INTO Usuario (idUsuario, nombre, gmail, contrasena, idRol, telefono, direccion, contactoEmergenciaNombre, contactoEmergenciaTelefono, contactoEmergenciaRelacion) KEY(idUsuario)
    VALUES
    (1, 'Admin', 'admin@gestionconcierto.com', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 1, '314234123', 'Calle 100 #48-90', 'Aseguradora ALIANZA', '310233211', 'Aseguradora'),
    (2, 'Feid', 'feid@vidaloca.com', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 3, '314234123', 'Calle 44 #22-01', 'Centrals Seguros', '324231231', 'Aseguradora');

-- Resincronizar el contador autoincremental de la tabla Usuario para evitar choques de Primary Key
ALTER TABLE Usuario ALTER COLUMN idUsuario RESTART WITH 3;

--CONCIERTO--

