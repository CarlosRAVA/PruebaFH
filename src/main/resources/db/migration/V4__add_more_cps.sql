-- Insertar una muestra más amplia de Códigos Postales
INSERT INTO cat_codigos_postales (codigo) VALUES 
('01000'), ('11000'), ('14000'), ('03100'), ('04000'), -- CDMX
('20000'), ('20100'), ('20200'), -- Aguascalientes
('64000'), ('64800'), ('66200'), -- Nuevo León
('44100'), ('44200'), ('45000'), -- Jalisco
('97000'), ('97200'), ('97300'), -- Yucatán
('37000'), ('37100'), ('37200'), -- Guanajuato
('50000'), ('50100'), ('53000'), -- Edo. Mex
('72000'), ('72500'), ('72800'), -- Puebla
('76000'), ('76100'), ('76200')  -- Querétaro
ON CONFLICT (codigo) DO NOTHING;
