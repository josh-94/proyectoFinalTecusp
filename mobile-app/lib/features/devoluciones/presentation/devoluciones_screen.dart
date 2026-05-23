import 'package:data_table_2/data_table_2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../auth/presentation/auth_provider.dart';
import '../data/devoluciones_service.dart';
import '../domain/devolucion_model.dart';
import 'devoluciones_provider.dart';
import 'widgets/registrar_devolucion_dialog.dart';

class DevolucionesScreen extends ConsumerWidget {
  const DevolucionesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final devAsync = ref.watch(devolucionesProvider);
    final user = ref.watch(authProvider).valueOrNull;
    final cs = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: cs.surfaceContainerLowest,
      appBar: AppBar(
        title: const Text('Devoluciones'),
        actions: [
          if (user != null && user.canCreateReturns())
            FilledButton.icon(
              icon: const Icon(Icons.add, size: 18),
              label: const Text('Registrar devolución'),
              style: FilledButton.styleFrom(
                  backgroundColor: cs.onPrimary, foregroundColor: cs.primary),
              onPressed: () => showDialog(
                context: context,
                builder: (_) => const RegistrarDevolucionDialog(),
              ),
            ),
          const SizedBox(width: 8),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref
                .read(devolucionesRefreshProvider.notifier)
                .update((s) => s + 1),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: devAsync.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, _) => Center(
            child: Column(mainAxisSize: MainAxisSize.min, children: [
              Icon(Icons.error_outline, size: 48, color: cs.error),
              const SizedBox(height: 12),
              Text('Error al cargar devoluciones',
                  style: TextStyle(color: cs.error)),
              const SizedBox(height: 8),
              FilledButton(
                onPressed: () => ref
                    .read(devolucionesRefreshProvider.notifier)
                    .update((s) => s + 1),
                child: const Text('Reintentar'),
              ),
            ]),
          ),
          data: (devoluciones) => Card(
            child: DataTable2(
              columnSpacing: 8,
              horizontalMargin: 12,
              minWidth: 700,
              headingRowColor: WidgetStateProperty.all(
                  cs.primaryContainer.withValues(alpha: 0.4)),
              empty: const Center(
                child: Padding(
                  padding: EdgeInsets.all(32),
                  child: Text('No hay devoluciones registradas'),
                ),
              ),
              columns: const [
                DataColumn2(label: Text('Número'),     size: ColumnSize.S),
                DataColumn2(label: Text('Pedido ref.'), size: ColumnSize.M),
                DataColumn2(label: Text('Estado'),     size: ColumnSize.M),
                DataColumn2(label: Text('Ítems'),      size: ColumnSize.S),
                DataColumn2(label: Text('Fecha'),      size: ColumnSize.S),
                DataColumn2(label: Text('Acciones'),   size: ColumnSize.L, fixedWidth: 130),
              ],
              rows: devoluciones
                  .map((d) => _buildRow(context, ref, d, user, cs))
                  .toList(),
            ),
          ),
        ),
      ),
    );
  }

  DataRow2 _buildRow(BuildContext context, WidgetRef ref, DevolucionModel d,
      dynamic user, ColorScheme cs) {
    return DataRow2(cells: [
      DataCell(Text(d.numeroDevolucion,
          style: const TextStyle(fontFamily: 'monospace', fontSize: 13))),
      DataCell(Text(d.pedidoId,
          style: const TextStyle(fontSize: 12),
          overflow: TextOverflow.ellipsis)),
      DataCell(_EstadoDevChip(estado: d.estado)),
      DataCell(Text('${d.lineas.length} ítem(s)')),
      DataCell(Text(d.creadoEn.length >= 10
          ? d.creadoEn.substring(0, 10)
          : d.creadoEn)),
      DataCell(Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            padding: EdgeInsets.zero,
            constraints: const BoxConstraints(),
            icon: const Icon(Icons.info_outline, size: 16),
            tooltip: 'Ver detalle',
            onPressed: () => _showDetalle(context, d),
          ),
          if (user != null &&
              user.canInspectReturns() &&
              d.estado == 'PENDIENTE') ...[
            const SizedBox(width: 4),
            IconButton(
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(),
              icon: Icon(Icons.search, size: 16, color: cs.primary),
              tooltip: 'Inspeccionar',
              onPressed: () => _showInspeccionar(context, ref, d.id),
            ),
          ],
          if (user != null &&
              user.canInspectReturns() &&
              d.estado == 'INSPECCIONADA') ...[
            const SizedBox(width: 4),
            IconButton(
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(),
              icon: const Icon(Icons.check_circle_outline,
                  size: 16, color: Colors.green),
              tooltip: 'Aprobar',
              onPressed: () => _aprobar(context, ref, d.id),
            ),
            const SizedBox(width: 4),
            IconButton(
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(),
              icon: Icon(Icons.cancel_outlined, size: 16, color: cs.error),
              tooltip: 'Rechazar',
              onPressed: () => _showRechazar(context, ref, d.id),
            ),
          ],
        ],
      )),
    ]);
  }

  void _showDetalle(BuildContext context, DevolucionModel d) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text('Devolución ${d.numeroDevolucion}'),
        content: SizedBox(
          width: 400,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _InfoRow('Pedido ref.', d.pedidoId),
              _InfoRow('Estado', d.estado),
              _InfoRow('Solicitado por', d.solicitadoPor),
              if (d.observaciones != null)
                _InfoRow('Observaciones', d.observaciones!),
              if (d.motivoRechazo != null)
                _InfoRow('Motivo rechazo', d.motivoRechazo!),
              const Divider(height: 24),
              const Text('Ítems:',
                  style: TextStyle(fontWeight: FontWeight.w600)),
              const SizedBox(height: 8),
              ...d.lineas.map((l) => Padding(
                    padding: const EdgeInsets.only(bottom: 4),
                    child: Text(
                        '• Lote ${l.loteId} — ${l.cantidadDevuelta} unid. — ${l.motivoDevolucion}',
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

  Future<void> _showInspeccionar(
      BuildContext context, WidgetRef ref, String id) async {
    final ctrl = TextEditingController();
    final result = await showDialog<String>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Registrar inspección'),
        content: SizedBox(
          width: 360,
          child: TextField(
            controller: ctrl,
            maxLines: 3,
            decoration: const InputDecoration(
                labelText: 'Observaciones de inspección'),
          ),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancelar')),
          FilledButton(
              onPressed: () => Navigator.pop(context, ctrl.text.trim()),
              child: const Text('Guardar')),
        ],
      ),
    );
    if (result != null && result.isNotEmpty) {
      try {
        await ref.read(devolucionesServiceProvider).inspeccionar(id, result);
        ref.read(devolucionesRefreshProvider.notifier).update((s) => s + 1);
      } catch (_) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Error al registrar inspección')));
        }
      }
    }
  }

  Future<void> _aprobar(
      BuildContext context, WidgetRef ref, String id) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Aprobar devolución'),
        content: const Text(
            '¿Aprobar la devolución? El stock será liberado en inventario.'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('Cancelar')),
          FilledButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('Aprobar')),
        ],
      ),
    );
    if (ok == true) {
      try {
        await ref.read(devolucionesServiceProvider).aprobar(id);
        ref.read(devolucionesRefreshProvider.notifier).update((s) => s + 1);
      } catch (_) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Error al aprobar la devolución')));
        }
      }
    }
  }

  Future<void> _showRechazar(
      BuildContext context, WidgetRef ref, String id) async {
    final ctrl = TextEditingController();
    final result = await showDialog<String>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Rechazar devolución'),
        content: SizedBox(
          width: 360,
          child: TextField(
            controller: ctrl,
            maxLines: 2,
            decoration:
                const InputDecoration(labelText: 'Motivo del rechazo'),
          ),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancelar')),
          FilledButton(
              onPressed: () => Navigator.pop(context, ctrl.text.trim()),
              child: const Text('Rechazar')),
        ],
      ),
    );
    if (result != null && result.isNotEmpty) {
      try {
        await ref.read(devolucionesServiceProvider).rechazar(id, result);
        ref.read(devolucionesRefreshProvider.notifier).update((s) => s + 1);
      } catch (_) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Error al rechazar la devolución')));
        }
      }
    }
  }
}

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;
  const _InfoRow(this.label, this.value);

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.only(bottom: 6),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          SizedBox(
              width: 130,
              child: Text('$label:',
                  style: const TextStyle(
                      fontWeight: FontWeight.w600, fontSize: 13))),
          Expanded(child: Text(value, style: const TextStyle(fontSize: 13))),
        ]),
      );
}

class _EstadoDevChip extends StatelessWidget {
  final String estado;
  const _EstadoDevChip({required this.estado});

  static const _colors = {
    'PENDIENTE':     (Color(0xFFF3F3F3), Color(0xFF757575)),
    'INSPECCIONADA': (Color(0xFFFFFDE7), Color(0xFFF57F17)),
    'APROBADA':      (Color(0xFFE8F5E9), Color(0xFF2E7D32)),
    'RECHAZADA':     (Color(0xFFFFEBEE), Color(0xFFC62828)),
  };

  @override
  Widget build(BuildContext context) {
    final (bg, fg) =
        _colors[estado] ?? (const Color(0xFFF3F3F3), const Color(0xFF757575));
    return Chip(
      label: Text(estado,
          style: TextStyle(
              fontSize: 11, color: fg, fontWeight: FontWeight.w500)),
      backgroundColor: bg,
      side: BorderSide(color: fg.withValues(alpha: 0.4)),
      padding: EdgeInsets.zero,
      visualDensity: VisualDensity.compact,
    );
  }
}
