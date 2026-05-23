import 'package:data_table_2/data_table_2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../auth/presentation/auth_provider.dart';
import 'inventario_provider.dart';
import 'widgets/movimiento_dialog.dart';

class InventarioScreen extends ConsumerWidget {
  const InventarioScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final lotesAsync = ref.watch(lotesWithRefreshProvider);
    final user = ref.watch(authProvider).valueOrNull;
    final cs = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: cs.surfaceContainerLowest,
      appBar: AppBar(
        title: const Text('Inventario — Lotes'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Actualizar',
            onPressed: () =>
                ref.read(lotesRefreshProvider.notifier).update((s) => s + 1),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: lotesAsync.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, _) => Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(Icons.error_outline, size: 48, color: cs.error),
                const SizedBox(height: 12),
                Text('Error al cargar inventario',
                    style: TextStyle(color: cs.error)),
                const SizedBox(height: 8),
                FilledButton(
                  onPressed: () => ref
                      .read(lotesRefreshProvider.notifier)
                      .update((s) => s + 1),
                  child: const Text('Reintentar'),
                ),
              ],
            ),
          ),
          data: (lotes) {
            final bajos = lotes.where((l) => l.stockBajo).length;
            final vencidos = lotes.where((l) => l.vencido).length;

            return Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                if (bajos > 0 || vencidos > 0)
                  Wrap(spacing: 12, runSpacing: 8, children: [
                    if (bajos > 0)
                      _AlertChip(
                          icon: Icons.warning_amber_rounded,
                          label: '$bajos lote(s) con stock bajo',
                          color: Colors.orange),
                    if (vencidos > 0)
                      _AlertChip(
                          icon: Icons.event_busy,
                          label: '$vencidos lote(s) vencido(s)',
                          color: cs.error),
                  ]),
                if (bajos > 0 || vencidos > 0) const SizedBox(height: 16),
                Expanded(
                  child: Card(
                    child: DataTable2(
                      columnSpacing: 12,
                      horizontalMargin: 16,
                      headingRowColor: WidgetStateProperty.all(
                          cs.primaryContainer.withValues(alpha: 0.4)),
                      columns: [
                        const DataColumn2(label: Text('Lote'), size: ColumnSize.S),
                        const DataColumn2(label: Text('Producto'), size: ColumnSize.L),
                        const DataColumn2(
                            label: Text('Stock'),
                            size: ColumnSize.S,
                            numeric: true),
                        const DataColumn2(label: Text('Mín.'), size: ColumnSize.S),
                        const DataColumn2(label: Text('Vencimiento'), size: ColumnSize.M),
                        const DataColumn2(label: Text('Estado'), size: ColumnSize.S),
                        if (user != null && user.canManageStock())
                          const DataColumn2(label: Text('Acción'), size: ColumnSize.S),
                      ],
                      rows: lotes.map((lote) {
                        final alerta = lote.stockBajo || lote.vencido;
                        return DataRow2(
                          color: alerta
                              ? WidgetStateProperty.all(
                                  cs.errorContainer.withValues(alpha: 0.15))
                              : null,
                          cells: [
                            DataCell(Text(lote.numeroLote,
                                style: const TextStyle(
                                    fontFamily: 'monospace', fontSize: 13))),
                            DataCell(Text(lote.nombreProducto)),
                            DataCell(Text(
                              '${lote.cantidadDisponible}',
                              style: TextStyle(
                                fontWeight: FontWeight.w600,
                                color: lote.stockBajo ? cs.error : null,
                              ),
                            )),
                            DataCell(Text('${lote.stockMinimo}')),
                            DataCell(Text(lote.fechaVencimiento.length >= 10
                                ? lote.fechaVencimiento.substring(0, 10)
                                : lote.fechaVencimiento)),
                            DataCell(_EstadoChip(lote: lote)),
                            if (user != null && user.canManageStock())
                              DataCell(IconButton(
                                icon: const Icon(Icons.add_circle_outline,
                                    size: 20),
                                tooltip: 'Registrar movimiento',
                                onPressed: () => showDialog(
                                  context: context,
                                  builder: (_) =>
                                      MovimientoDialog(lote: lote),
                                ),
                              )),
                          ],
                        );
                      }).toList(),
                    ),
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}

class _AlertChip extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color color;
  const _AlertChip(
      {required this.icon, required this.label, required this.color});

  @override
  Widget build(BuildContext context) => Chip(
        avatar: Icon(icon, color: color, size: 16),
        label: Text(label, style: TextStyle(color: color, fontSize: 12)),
        backgroundColor: color.withValues(alpha: 0.1),
        side: BorderSide(color: color.withValues(alpha: 0.3)),
        padding: const EdgeInsets.symmetric(horizontal: 4),
      );
}

class _EstadoChip extends StatelessWidget {
  final dynamic lote;
  const _EstadoChip({required this.lote});

  @override
  Widget build(BuildContext context) {
    if (lote.vencido) {
      return const Chip(
          label: Text('Vencido', style: TextStyle(fontSize: 11)),
          backgroundColor: Color(0xFFFFEBEE),
          side: BorderSide(color: Colors.red),
          padding: EdgeInsets.zero);
    }
    if (lote.stockBajo) {
      return const Chip(
          label: Text('Stock bajo', style: TextStyle(fontSize: 11)),
          backgroundColor: Color(0xFFFFF3E0),
          side: BorderSide(color: Colors.orange),
          padding: EdgeInsets.zero);
    }
    return const Chip(
        label: Text('OK', style: TextStyle(fontSize: 11)),
        backgroundColor: Color(0xFFE8F5E9),
        side: BorderSide(color: Colors.green),
        padding: EdgeInsets.zero);
  }
}
