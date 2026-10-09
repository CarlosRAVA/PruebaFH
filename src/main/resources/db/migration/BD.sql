CREATE TABLE IF NOT EXISTS gestopago_tokens (
    id SERIAL PRIMARY KEY,
    id_distribuidor INTEGER NOT NULL,
    codigo_dispositivo VARCHAR(100) NOT NULL,
    token TEXT NOT NULL,
    token_type VARCHAR(50),
    expires_in BIGINT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW(),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_gestopago_tokens UNIQUE (id_distribuidor, codigo_dispositivo)
);

-- 2. CATÁLOGOS BASE
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

-- 3. TABLAS TRANSACCIONALES
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    segundo_nombre VARCHAR(50),
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    curp VARCHAR(18) NOT NULL UNIQUE,
    rfc VARCHAR(13) NOT NULL UNIQUE,
    sexo VARCHAR(20) NOT NULL,
    pais_id INTEGER NOT NULL REFERENCES cat_paises(id),
    estado_civil VARCHAR(30) NOT NULL,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    telefono_movil BIGINT NOT NULL,
    telefono_alternativo BIGINT,
    ocupacion VARCHAR(100) NOT NULL,
    empresa VARCHAR(100) NOT NULL,
    ingreso_mensual DECIMAL(15,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE domicilios (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL UNIQUE,
    calle VARCHAR(100) NOT NULL,
    numero_exterior VARCHAR(20) NOT NULL,
    numero_interior VARCHAR(20),
    colonia VARCHAR(100) NOT NULL,
    municipio VARCHAR(100) NOT NULL,
    estado_id INTEGER NOT NULL REFERENCES cat_estados(id),
    codigo_postal_id INTEGER NOT NULL REFERENCES cat_codigos_postales(id),
    CONSTRAINT fk_domicilio_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    estatus VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

-- 4. ÍNDICES PARA OPTIMIZACIÓN DE BÚSQUEDAS
CREATE INDEX idx_clientes_curp ON clientes(curp);
CREATE INDEX idx_clientes_rfc ON clientes(rfc);
CREATE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
CREATE INDEX idx_usuarios_correo ON usuarios(correo);

-- 5. INSERCIÓN DE DATOS DE CATÁLOGOS
INSERT INTO cat_paises (nombre) VALUES ('México');

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
('01000'), ('11000'), ('14000'), ('03100'), ('04000'), 
('20000'), ('20100'), ('20200'), 
('64000'), ('64800'), ('66200'), 
('44100'), ('44200'), ('45000'), 
('97000'), ('97200'), ('97300'), 
('37000'), ('37100'), ('37200'), 
('50000'), ('50100'), ('53000'), 
('72000'), ('72500'), ('72800'), 
('76000'), ('76100'), ('76200')
ON CONFLICT (codigo) DO NOTHING;
