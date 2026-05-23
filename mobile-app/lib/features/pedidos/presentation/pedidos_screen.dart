import 'package:data_table_2/data_table_2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../auth/presentation/auth_provider.dart';
import '../data/pedidos_service.dart';
import '../domain/pedido_model.dart';
import 'pedidos_provider.dart';
import 'widgets/nuevo_pedido_dialog.dart';

class PedidosScreen extends ConsumerWidget {
  const PedidosScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final pedidosAsync = ref.watch(pedidosProvider);
    final user = ref.watch(authProvider).valueOrNull;
    final cs = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: cs.surfaceContainerLowest,
      appBar: AppBar(
        title: const Text('Pedidos'),
        actions: [
          if (user != null && user.canCreateOrders())
            FilledButton.icon(
              icon: const Icon(Icons.add, size: 18),
              label: const Text('Nuevo pedido'),
              style: FilledButton.styleFrom(
                  backgroundColor: cs.onPrimary, foregroundColor: cs.primary),
              onPressed: () => showDialog(
                context: context,
                builder: (_) => const NuevoPedidoDialog(),
              ),
            ),
          const SizedBox(width: 8),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () =>
                ref.read(pedidosRefreshProvider.notifier).update((s) => s + 1),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: pedidosAsync.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, _) => Center(
            child: Column(mainAxisSize: MainAxisSize.min, children: [
              Icon(Icons.error_outline, size: 48, color: cs.error),
              const SizedBox(height: 12),
              Text('Error al cargar pedidos', style: TextStyle(color: cs.error)),
              const SizedBox(height: 8),
              FilledButton(
                onPressed: () => ref
                    .read(pedidosRefreshProvider.notifier)
                    .update((s) => s + 1),
                child: const Text('Reintentar'),
              ),
            ]),
          ),
          data: (pedidos) => Card(
            child: DataTable2(
              columnSpacing: 8,
              horizontalMargin: 12,
              minWidth: 680,
              headingRowColor: WidgetStateProperty.all(
                  cs.primaryContainer.withValues(alpha: 0.4)),
              empty: const Center(
                child: Padding(
                  padding: EdgeInsets.all(32),
                  child: Text('No hay pedidos registrados'),
                ),
              ),
              columns: [
                const DataColumn2(label: Text('Número'), size: ColumnSize.S),
                const DataColumn2(label: Text('Hospital'), size: ColumnSize.L),
                const DataColumn2(label: Text('Estado'), size: ColumnSize.M),
                const DataColumn2(label: Text('Líneas'), size: ColumnSize.S),
                const DataColumn2(label: Text('Fecha'), size: ColumnSize.M),
                const DataColumn2(label: Text('Acciones'), size: ColumnSize.M, fixedWidth: 90),
              ],
              rows: pedidos.map((p) => _buildRow(context, ref, p, user, cs)).toList(),
            ),
          ),
        ),
      ),
    );
  }

  DataRow2 _buildRow(BuildContext context, WidgetRef ref, PedidoModel p,
      dynamic user, ColorScheme cs) {
    return DataRow2(cells: [
      DataCell(Text(p.numeroPedido,
          style: const TextStyle(fontFamily: 'monospace', fontSize: 13))),
      DataCell(Text(p.hospitalDestino)),
      DataCell(_EstadoChip(estado: p.estado)),
      DataCell(Text('${p.lineas.length} ítem(s)')),
      DataCell(Text(p.creadoEn.length >= 10 ? p.creadoEn.substring(0, 10) : p.creadoEn)),
      DataCell(Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            padding: EdgeInsets.zero,
            constraints: const BoxConstraints(),
            icon: const Icon(Icons.info_outline, size: 16),
            tooltip: 'Ver detalle',
            onPressed: () => _showDetalle(context, p),
          ),
          if (user != null &&
              user.canDispatchOrders() &&
              p.estado == 'CONFIRMADO') ...[
            const SizedBox(width: 4),
            IconButton(
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(),
              icon: Icon(Icons.local_shipping_outlined,
                  size: 16, color: cs.primary),
              tooltip: 'Despachar',
              onPressed: () => _despachar(context, ref, p.id),
            ),
          ],
          if (user != null &&
              user.canCreateOrders() &&
              (p.estado == 'PENDIENTE_STOCK' || p.estado == 'CONFIRMADO')) ...[
            const SizedBox(width: 4),
            IconButton(
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(),
              icon: Icon(Icons.cancel_outlined, size: 16, color: cs.error),
              tooltip: 'Cancelar',
              onPressed: () => _cancelar(context, ref, p.id),
            ),
          ],
        ],
      )),
    ]);
  }

  void _showDetalle(BuildContext context, PedidoModel p) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text('Pedido ${p.numeroPedido}'),
        content: SizedBox(
          width: 400,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _Row('Hospital', p.hospitalDestino),
              _Row('Estado', p.estado),
              _Row('Solicitado por', p.solicitadoPor),
              if (p.motivoRechazo != null)
                _Row('Motivo rechazo', p.motivoRechazo!),
              const Divider(height: 24),
              const Text('Líneas:',
                  style: TextStyle(fontWeight: FontWeight.w600)),
              const SizedBox(height: 8),
              ...p.lineas.map((l) => Padding(
                    padding: const EdgeInsets.only(bottom: 4),
                    child: Text(
                        '• ${l.descripcion} — Lote: ${l.loteId} — Cant: ${l.cantidad}',
                        style: const TextStyle(fontSize: 13)),
                  )),
            ],
          ),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cerrar'))
        ],
      ),
    );
  }

  Future<void> _despachar(
      BuildContext context, WidgetRef ref, String pedidoId) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Confirmar despacho'),
        content: const Text('¿Marcar este pedido como despachado?'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('Cancelar')),
          FilledButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('Despachar')),
        ],
      ),
    );
    if (confirm == true) {
      try {
        await ref.read(pedidosServiceProvider).despachar(pedidoId);
        ref.read(pedidosRefreshProvider.notifier).update((s) => s + 1);
      } catch (_) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Error al despachar el pedido')));
        }
      }
    }
  }

  Future<void> _cancelar(
      BuildContext context, WidgetRef ref, String pedidoId) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Cancelar pedido'),
        content: const Text('¿Desea cancelar este pedido?'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('No')),
          FilledButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('Sí, cancelar')),
        ],
      ),
    );
    if (confirm == true) {
      try {
        await ref.read(pedidosServiceProvider).cancelar(pedidoId);
        ref.read(pedidosRefreshProvider.notifier).update((s) => s + 1);
      } catch (_) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Error al cancelar el pedido')));
        }
      }
    }
  }
}

class _Row extends StatelessWidget {
  final String label;
  final String value;
  const _Row(this.label, this.value);

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.only(bottom: 6),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          SizedBox(
              width: 120,
              child: Text('$label:',
                  style: const TextStyle(fontWeight: FontWeight.w600,
                      fontSize: 13))),
          Expanded(child: Text(value, style: const TextStyle(fontSize: 13))),
        ]),
      );
}

class _EstadoChip extends StatelessWidget {
  final String estado;
  const _EstadoChip({required this.estado});

  static const _colors = {
    'PENDIENTE_STOCK': (Color(0xFFF3F3F3), Color(0xFF757575)),
    'CONFIRMADO':      (Color(0xFFE8F5E9), Color(0xFF2E7D32)),
    'RECHAZADO':       (Color(0xFFFFEBEE), Color(0xFFC62828)),
    'DESPACHADO':      (Color(0xFFE3F2FD), Color(0xFF1565C0)),
    'CANCELADO':       (Color(0xFFFFF3E0), Color(0xFFE65100)),
  };

  @override
  Widget build(BuildContext context) {
    final (bg, fg) = _colors[estado] ?? (const Color(0xFFF3F3F3), const Color(0xFF757575));
    return Chip(
      label: Text(estado.replaceAll('_', ' '),
          style: TextStyle(fontSize: 11, color: fg, fontWeight: FontWeight.w500)),
      backgroundColor: bg,
      side: BorderSide(color: fg.withValues(alpha: 0.4)),
      padding: EdgeInsets.zero,
      visualDensity: VisualDensity.compact,
    );
  }
}
