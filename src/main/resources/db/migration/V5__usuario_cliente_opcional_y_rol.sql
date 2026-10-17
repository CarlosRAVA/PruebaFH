-- Alinea la tabla usuarios con la entidad Usuario:
-- 1) Un usuario administrador puede existir sin cliente asociado.
-- 2) Incorpora explícitamente la columna rol controlada por el servidor.

ALTER TABLE usuarios ALTER COLUMN cliente_id DROP NOT NULL;

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS rol VARCHAR(30) NOT NULL DEFAULT 'ROLE_USER';
