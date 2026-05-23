-- ============================================================
-- SEED: orders_db  (ejecutar en postgres-orders :5444)
-- ============================================================

-- Pedidos en distintos estados para mostrar el flujo completo
INSERT INTO pedidos (id, numero_pedido, solicitado_por, hospital_destino, estado, motivo_rechazo, creado_en) VALUES
  -- DESPACHADO: flujo completo terminado
  ('ped-001', 'PED-001A2B3C', 'admin', 'Hospital Nacional Dos de Mayo',     'DESPACHADO',      NULL, NOW() - INTERVAL '10 days'),
  -- CONFIRMADO: listo para despachar (acción disponible para bodega)
  ('ped-002', 'PED-002D4E5F', 'admin', 'Hospital Rebagliati EsSalud',        'CONFIRMADO',      NULL, NOW() - INTERVAL '5 days'),
  -- PENDIENTE_STOCK: esperando reserva de inventario
  ('ped-003', 'PED-003G6H7I', 'admin', 'Clínica San Pablo',                  'PENDIENTE_STOCK', NULL, NOW() - INTERVAL '2 days'),
  -- RECHAZADO: stock insuficiente
  ('ped-004', 'PED-004J8K9L', 'admin', 'Hospital María Auxiliadora',         'RECHAZADO',       'Stock insuficiente para Cloranfenicol 1g en el lote solicitado', NOW() - INTERVAL '7 days'),
  -- CANCELADO: cancelado por el solicitante
  ('ped-005', 'PED-005M0N1O', 'admin', 'Hospital Arzobispo Loayza',          'CANCELADO',       NULL, NOW() - INTERVAL '15 days'),
  -- CONFIRMADO: otro pedido listo (para mostrar múltiples)
  ('ped-006', 'PED-006P2Q3R', 'admin', 'Hospital de Emergencias Grau',       'CONFIRMADO',      NULL, NOW() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- Líneas de pedido
INSERT INTO lineas_pedido (pedido_id, lote_id, cantidad, descripcion) VALUES
  -- Pedido 1 (DESPACHADO)
  ('ped-001', 'lote-001', 50,  'Amoxicilina 500mg — tratamiento antibiótico'),
  ('ped-001', 'lote-004', 80,  'Paracetamol 1g — analgesia postoperatoria'),
  -- Pedido 2 (CONFIRMADO)
  ('ped-002', 'lote-006', 200, 'Jeringas 5ml — procedimientos de enfermería'),
  ('ped-002', 'lote-009', 50,  'Guantes quirúrgicos M — cirugía programada'),
  -- Pedido 3 (PENDIENTE_STOCK)
  ('ped-003', 'lote-002', 30,  'Amoxicilina 500mg — urgencias'),
  ('ped-003', 'lote-007', 20,  'Gasa estéril — curaciones'),
  -- Pedido 4 (RECHAZADO)
  ('ped-004', 'lote-010', 15,  'Cloranfenicol 1g — infecciones graves'),
  -- Pedido 5 (CANCELADO)
  ('ped-005', 'lote-003', 10,  'Ibuprofeno 400mg — dolor postoperatorio'),
  -- Pedido 6 (CONFIRMADO)
  ('ped-006', 'lote-004', 40,  'Paracetamol 1g — manejo del dolor'),
  ('ped-006', 'lote-006', 100, 'Jeringas 5ml — vacunación')
ON CONFLICT DO NOTHING;
