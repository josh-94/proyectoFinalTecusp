-- ============================================================
-- SEED: returns_db  (ejecutar en postgres-returns :5445)
-- ============================================================

-- Devoluciones en todos los estados posibles
INSERT INTO devoluciones (id, numero_devolucion, pedido_id, solicitado_por, estado, observaciones, motivo_rechazo, creado_en) VALUES
  -- PENDIENTE: recién registrada, esperando inspección
  ('dev-001', 'DEV-001A2B3C', 'ped-001', 'admin', 'PENDIENTE',     NULL, NULL, NOW() - INTERVAL '3 days'),
  -- INSPECCIONADA: ya revisada, esperando aprobación/rechazo
  ('dev-002', 'DEV-002D4E5F', 'ped-001', 'admin', 'INSPECCIONADA', 'Productos en buen estado, embalaje original intacto. Temperatura de cadena de frío verificada.', NULL, NOW() - INTERVAL '6 days'),
  -- APROBADA: flujo completo aprobado
  ('dev-003', 'DEV-003G6H7I', 'ped-002', 'admin', 'APROBADA',      'Inspección OK. Lotes verificados contra registro sanitario.', NULL, NOW() - INTERVAL '12 days'),
  -- RECHAZADA: no cumplió condiciones
  ('dev-004', 'DEV-004J8K9L', 'ped-002', 'admin', 'RECHAZADA',     'Embalaje deteriorado. Posible ruptura de cadena de frío.', 'Productos con signos de mal almacenamiento, no pueden reingresarse al inventario', NOW() - INTERVAL '8 days'),
  -- PENDIENTE: otra en espera
  ('dev-005', 'DEV-005M0N1O', 'ped-006', 'admin', 'PENDIENTE',     NULL, NULL, NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Líneas de devolución
INSERT INTO lineas_devolucion (devolucion_id, lote_id, cantidad_devuelta, motivo_devolucion) VALUES
  -- Dev 1 (PENDIENTE)
  ('dev-001', 'lote-001', 10, 'Exceso de stock solicitado, no se utilizó en tratamiento'),
  ('dev-001', 'lote-004', 20, 'Pacientes dados de alta antes de completar tratamiento'),
  -- Dev 2 (INSPECCIONADA)
  ('dev-002', 'lote-006', 50, 'Procedimientos cancelados por reprogramación quirúrgica'),
  -- Dev 3 (APROBADA)
  ('dev-003', 'lote-009', 15, 'Cirugías reprogramadas, guantes no utilizados'),
  -- Dev 4 (RECHAZADA)
  ('dev-004', 'lote-006', 30, 'Jeringas sobrantes de campaña de vacunación'),
  -- Dev 5 (PENDIENTE)
  ('dev-005', 'lote-004', 5,  'Medicamento no compatible con nuevo protocolo de tratamiento')
ON CONFLICT DO NOTHING;
