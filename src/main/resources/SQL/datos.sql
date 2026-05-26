
-----------------------------------
-- DATOS DE PRUEBA
-----------------------------------

-- 1. USUARIOS
MERGE INTO Usuario (idUsuario, nombre, gmail, contrasena, idRol, telefono, direccion, contactoEmergenciaNombre, contactoEmergenciaTelefono, contactoEmergenciaRelacion) KEY(idUsuario)
    VALUES
    (1, 'Admin', 'admin@gestionconcierto.com', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 1, '314234123', 'Calle 100 #48-90', 'Aseguradora ALIANZA', '310233211', 'Aseguradora'),
    (2, 'Feid', 'feid@vidaloka.com', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 3, '310000221', 'Calle 44 #22-01', 'Centrals Seguros', '324231231', 'Aseguradora'),
    (3, 'Pepe', 'pepe@cloro.co', '$2a$10$ToZhw13TSN5sI4X1N9YnjuMFRk1lYBtXGtCVHzDEpWbnLHMw.9X7O', 0, '312324123', 'Calle 1 #32-09', 'Abuelita Marta', '3111211', 'Abuela');

ALTER TABLE Usuario ALTER COLUMN idUsuario RESTART WITH 4;

-- 2. HORARIOS (Deben ir antes del concierto)
MERGE INTO Horario (idHorario, fechaInc, fechaFin, horaInc, horaFin) KEY (idHorario)
    VALUES
    (1, '2026-05-01', '2026-05-01', '20:00:00', '01:00:00'),
    (2, '2026-07-05', '2026-07-07', '21:00:00', '03:00:00');

ALTER TABLE Horario ALTER COLUMN idHorario RESTART WITH 3;

-- 3. CONTRATOS (Deben ir antes del concierto y cláusulas)
MERGE INTO Contrato (idContrato, fecha) KEY (idContrato)
    VALUES
    (1, '2026-04-01'),
    (2, '2026-06-05');

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
MERGE INTO Concierto (idConcierto, nombreConcierto, idHorario, aforo, idContrato, programado) KEY(idConcierto)
VALUES
    (1, 'Fin del Mundo Loko', 1, 100001, 1, FALSE),
    (2, 'Vida loka', 2, 35000, 2, TRUE);

ALTER TABLE Concierto ALTER COLUMN idConcierto RESTART WITH 3;

-- 6. TABLAS INTERMEDIAS
-- 3 = Manager/Artista
MERGE INTO RolConciertoUsuario (idRol, idUsuario, idConcierto) KEY (idRol, idUsuario, idConcierto)
    VALUES
    (3, 2, 1), -- Asigna a Feid (Usuario 2) al Concierto 1 con Rol 3
    (3, 2, 2); -- Asigna a Feid (Usuario 2) al Concierto 2 con Rol 3