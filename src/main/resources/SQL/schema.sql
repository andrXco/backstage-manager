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
    telefono VARCHAR(10),
    direccion VARCHAR(255),
    contactoEmergenciaNombre VARCHAR(255),
    contactoEmergenciaTelefono VARCHAR(20),
    contactoEmergenciaRelacion VARCHAR(100)
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
    VALUES (1, 'Administrador'), (2, 'Tecnico'), (3, 'Artista'), (4, 'Staff');