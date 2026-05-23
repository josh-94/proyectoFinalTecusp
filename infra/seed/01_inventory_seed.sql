-- ============================================================
-- SEED: inventory_db  (ejecutar en postgres-inventory :5443)
-- ============================================================

-- Productos médicos
INSERT INTO productos (id, sku, nombre, descripcion, unidad_medida, stock_minimo) VALUES
  ('prod-001', 'MED-AMOX-500', 'Amoxicilina 500mg', 'Antibiótico de amplio espectro, cápsulas', 'Caja', 50),
  ('prod-002', 'MED-IBUP-400', 'Ibuprofeno 400mg', 'Antiinflamatorio no esteroideo, tabletas', 'Caja', 30),
  ('prod-003', 'MED-PARA-1G',  'Paracetamol 1g',   'Analgésico y antipirético, tabletas', 'Caja', 40),
  ('prod-004', 'INS-JERGA-5',  'Jeringas 5ml',     'Jeringa descartable con aguja 21G', 'Unidad', 200),
  ('prod-005', 'INS-GASA-10',  'Gasa estéril 10x10','Gasa no tejida estéril para curaciones', 'Paquete', 100),
  ('prod-006', 'MED-OMEP-20',  'Omeprazol 20mg',   'Inhibidor de la bomba de protones', 'Caja', 25),
  ('prod-007', 'INS-GUANT-M',  'Guantes quirúrgicos M', 'Guantes de látex talla M, par', 'Par', 150),
  ('prod-008', 'MED-CLOR-1G',  'Cloranfenicol 1g', 'Antibiótico de amplio espectro inyectable', 'Frasco', 20)
ON CONFLICT (id) DO NOTHING;

-- Lotes con distintos estados (ok, stock bajo, vencido)
INSERT INTO lotes (id, producto_id, numero_lote, fecha_vencimiento, cantidad_disponible) VALUES
  -- Amoxicilina — stock normal
  ('lote-001', 'prod-001', 'LOT-AMX-2024-01', '2026-08-15', 250),
  ('lote-002', 'prod-001', 'LOT-AMX-2024-02', '2025-12-31', 180),
  -- Ibuprofeno — stock bajo (<=30)
  ('lote-003', 'prod-002', 'LOT-IBU-2024-01', '2026-03-20', 12),
  -- Paracetamol — stock normal
  ('lote-004', 'prod-003', 'LOT-PAR-2024-01', '2026-11-10', 320),
  ('lote-005', 'prod-003', 'LOT-PAR-2024-02', '2025-09-01', 95),
  -- Jeringas — stock normal
  ('lote-006', 'prod-004', 'LOT-JER-2024-01', '2027-01-01', 850),
  -- Gasa — stock bajo (<=100)
  ('lote-007', 'prod-005', 'LOT-GAS-2024-01', '2026-06-30', 45),
  -- Omeprazol — VENCIDO (fecha en el pasado)
  ('lote-008', 'prod-006', 'LOT-OME-2022-01', '2023-04-15', 60),
  -- Guantes — stock normal
  ('lote-009', 'prod-007', 'LOT-GUA-2024-01', '2026-12-31', 400),
  -- Cloranfenicol — stock bajo + cerca de vencer
  ('lote-010', 'prod-008', 'LOT-CLO-2024-01', '2025-07-01', 8)
ON CONFLICT (id) DO NOTHING;

-- Movimientos históricos de entrada (para que exista historial)
INSERT INTO movimientos_stock (id, lote_id, tipo, cantidad, referencia_externa, creado_por) VALUES
  ('mov-001', 'lote-001', 'ENTRADA', 300, 'OC-2024-001', 'bodega1'),
  ('mov-002', 'lote-001', 'SALIDA',   50, 'PED-20240110', 'bodega1'),
  ('mov-003', 'lote-002', 'ENTRADA', 200, 'OC-2024-002', 'bodega1'),
  ('mov-004', 'lote-002', 'SALIDA',   20, 'PED-20240115', 'bodega1'),
  ('mov-005', 'lote-003', 'ENTRADA',  50, 'OC-2024-003', 'bodega1'),
  ('mov-006', 'lote-003', 'SALIDA',   38, 'PED-20240120', 'bodega1'),
  ('mov-007', 'lote-004', 'ENTRADA', 400, 'OC-2024-004', 'bodega1'),
  ('mov-008', 'lote-004', 'SALIDA',   80, 'PED-20240201', 'bodega1'),
  ('mov-009', 'lote-006', 'ENTRADA',1000, 'OC-2024-005', 'bodega1'),
  ('mov-010', 'lote-006', 'SALIDA',  150, 'PED-20240210', 'bodega1'),
  ('mov-011', 'lote-009', 'ENTRADA', 500, 'OC-2024-006', 'bodega1'),
  ('mov-012', 'lote-009', 'SALIDA',  100, 'PED-20240220', 'bodega1')
ON CONFLICT (id) DO NOTHING;
