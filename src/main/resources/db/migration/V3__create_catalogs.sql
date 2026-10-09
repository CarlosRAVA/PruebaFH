CREATE TABLE cat_paises (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE cat_estados (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    pais_id INTEGER NOT NULL REFERENCES cat_paises(id),
    UNIQUE(nombre, pais_id)
);

CREATE TABLE cat_codigos_postales (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE
);

-- Insertar País Base
INSERT INTO cat_paises (nombre) VALUES ('México');

-- Insertar Estados de México
INSERT INTO cat_estados (nombre, pais_id) VALUES 
('Aguascalientes', 1), ('Baja California', 1), ('Baja California Sur', 1),
('Campeche', 1), ('Chiapas', 1), ('Chihuahua', 1), ('Ciudad de México', 1),
('Coahuila', 1), ('Colima', 1), ('Durango', 1), ('Guanajuato', 1),
('Guerrero', 1), ('Hidalgo', 1), ('Jalisco', 1), ('México', 1),
('Michoacán', 1), ('Morelos', 1), ('Nayarit', 1), ('Nuevo León', 1),
('Oaxaca', 1), ('Puebla', 1), ('Querétaro', 1), ('Quintana Roo', 1),
('San Luis Potosí', 1), ('Sinaloa', 1), ('Sonora', 1), ('Tabasco', 1),
('Tamaulipas', 1), ('Tlaxcala', 1), ('Veracruz', 1), ('Yucatán', 1),
('Zacatecas', 1);


INSERT INTO cat_codigos_postales (codigo) VALUES 
('01000'), ('11000'), ('20000'), ('64000'), ('44100'), ('97000');

-- Modificar tabla clientes
ALTER TABLE clientes DROP COLUMN nacionalidad;
ALTER TABLE clientes ADD COLUMN pais_id INTEGER NOT NULL DEFAULT 1 REFERENCES cat_paises(id);

-- Modificar tabla domicilios
ALTER TABLE domicilios DROP COLUMN pais;
ALTER TABLE domicilios DROP COLUMN estado;
ALTER TABLE domicilios ADD COLUMN estado_id INTEGER NOT NULL DEFAULT 7 REFERENCES cat_estados(id);

ALTER TABLE domicilios DROP COLUMN codigo_postal;
ALTER TABLE domicilios ADD COLUMN codigo_postal_id INTEGER NOT NULL DEFAULT 1 REFERENCES cat_codigos_postales(id);
