import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../inventario/presentation/inventario_provider.dart';
import '../../pedidos/presentation/pedidos_provider.dart';
import '../../devoluciones/presentation/devoluciones_provider.dart';

class DashboardScreen extends ConsumerWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final lotesAsync = ref.watch(lotesWithRefreshProvider);
    final pedidosAsync = ref.watch(pedidosProvider);
    final devAsync = ref.watch(devolucionesProvider);
    final cs = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: cs.surfaceContainerLowest,
      appBar: AppBar(
        title: const Text('Dashboard'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Actualizar',
            onPressed: () {
              ref.read(lotesRefreshProvider.notifier).update((s) => s + 1);
              ref.read(pedidosRefreshProvider.notifier).update((s) => s + 1);
              ref.read(devolucionesRefreshProvider.notifier).update((s) => s + 1);
            },
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Resumen del sistema',
                style: Theme.of(context)
                    .textTheme
                    .titleLarge
                    ?.copyWith(fontWeight: FontWeight.w600)),
            const SizedBox(height: 20),
            Wrap(
              spacing: 16,
              runSpacing: 16,
              children: [
                _SummaryCard(
                  title: 'Lotes con stock bajo',
                  icon: Icons.warning_amber_rounded,
                  color: Colors.orange,
                  asyncValue: lotesAsync.whenData(
                      (lotes) => lotes.where((l) => l.stockBajo && !l.vencido).length),
                ),
                _SummaryCard(
                  title: 'Lotes vencidos',
                  icon: Icons.event_busy,
                  color: cs.error,
                  asyncValue: lotesAsync
                      .whenData((lotes) => lotes.where((l) => l.vencido).length),
                ),
                _SummaryCard(
                  title: 'Pedidos pendientes de stock',
                  icon: Icons.hourglass_empty,
                  color: Colors.grey.shade600,
                  asyncValue: pedidosAsync.whenData(
                      (p) => p.where((x) => x.estado == 'PENDIENTE_STOCK').length),
                ),
                _SummaryCard(
                  title: 'Pedidos confirmados',
                  icon: Icons.check_circle_outline,
                  color: Colors.green.shade700,
                  asyncValue: pedidosAsync.whenData(
                      (p) => p.where((x) => x.estado == 'CONFIRMADO').length),
                ),
                _SummaryCard(
                  title: 'Devoluciones pendientes',
                  icon: Icons.assignment_return_outlined,
                  color: Colors.grey.shade600,
                  asyncValue: devAsync.whenData(
                      (d) => d.where((x) => x.estado == 'PENDIENTE').length),
                ),
                _SummaryCard(
                  title: 'Devoluciones por aprobar',
                  icon: Icons.rate_review_outlined,
                  color: Colors.amber.shade800,
                  asyncValue: devAsync.whenData(
                      (d) => d.where((x) => x.estado == 'INSPECCIONADA').length),
                ),
              ],
            ),
            const SizedBox(height: 32),
            Text('Inventario — alertas',
                style: Theme.of(context)
                    .textTheme
                    .titleMedium
                    ?.copyWith(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            lotesAsync.when(
              loading: () => const LinearProgressIndicator(),
              error: (e, _) => Text('Error: $e',
                  style: TextStyle(color: cs.error)),
              data: (lotes) {
                final alertas =
                    lotes.where((l) => l.stockBajo || l.vencido).toList();
                if (alertas.isEmpty) {
                  return Card(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Row(children: [
                        Icon(Icons.check_circle,
                            color: Colors.green.shade700, size: 20),
                        const SizedBox(width: 8),
                        const Text('Sin alertas de inventario'),
                      ]),
                    ),
                  );
                }
                return Card(
                  child: ListView.separated(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: alertas.length,
                    separatorBuilder: (_, __) =>
                        const Divider(height: 1, indent: 16, endIndent: 16),
                    itemBuilder: (context, i) {
                      final l = alertas[i];
                      return ListTile(
                        dense: true,
                        leading: Icon(
                          l.vencido
                              ? Icons.event_busy
                              : Icons.warning_amber_rounded,
                          color: l.vencido ? cs.error : Colors.orange,
                          size: 20,
                        ),
                        title: Text(l.nombreProducto,
                            style: const TextStyle(fontSize: 13)),
                        subtitle: Text(
                            'Lote ${l.numeroLote} · Stock: ${l.cantidadDisponible}',
                            style: const TextStyle(fontSize: 11)),
                        trailing: Text(
                          l.vencido ? 'VENCIDO' : 'STOCK BAJO',
                          style: TextStyle(
                              fontSize: 11,
                              color: l.vencido ? cs.error : Colors.orange,
                              fontWeight: FontWeight.w600),
                        ),
                      );
                    },
                  ),
                );
              },
            ),
            const SizedBox(height: 32),
            Text('Últimos pedidos',
                style: Theme.of(context)
                    .textTheme
                    .titleMedium
                    ?.copyWith(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            pedidosAsync.when(
              loading: () => const LinearProgressIndicator(),
              error: (e, _) =>
                  Text('Error: $e', style: TextStyle(color: cs.error)),
              data: (pedidos) {
                final recientes = pedidos.take(5).toList();
                if (recientes.isEmpty) {
                  return const Card(
                    child: Padding(
                      padding: EdgeInsets.all(20),
                      child: Text('No hay pedidos registrados'),
                    ),
                  );
                }
                return Card(
                  child: ListView.separated(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: recientes.length,
                    separatorBuilder: (_, __) =>
                        const Divider(height: 1, indent: 16, endIndent: 16),
                    itemBuilder: (context, i) {
                      final p = recientes[i];
                      return ListTile(
                        dense: true,
                        leading: const Icon(Icons.list_alt_outlined, size: 20),
                        title: Text(
                            '${p.numeroPedido} · ${p.hospitalDestino}',
                            style: const TextStyle(fontSize: 13)),
                        subtitle: Text(
                            p.creadoEn.length >= 10
                                ? p.creadoEn.substring(0, 10)
                                : p.creadoEn,
                            style: const TextStyle(fontSize: 11)),
                        trailing: _estadoChip(p.estado, cs),
                      );
                    },
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _estadoChip(String estado, ColorScheme cs) {
    final colors = <String, (Color, Color)>{
      'PENDIENTE_STOCK': (const Color(0xFFF3F3F3), const Color(0xFF757575)),
      'CONFIRMADO': (const Color(0xFFE8F5E9), const Color(0xFF2E7D32)),
      'RECHAZADO': (const Color(0xFFFFEBEE), const Color(0xFFC62828)),
      'DESPACHADO': (const Color(0xFFE3F2FD), const Color(0xFF1565C0)),
      'CANCELADO': (const Color(0xFFFFF3E0), const Color(0xFFE65100)),
    };
    final (bg, fg) =
        colors[estado] ?? (const Color(0xFFF3F3F3), const Color(0xFF757575));
    return Chip(
      label: Text(estado.replaceAll('_', ' '),
          style: TextStyle(fontSize: 10, color: fg, fontWeight: FontWeight.w500)),
      backgroundColor: bg,
      side: BorderSide(color: fg.withValues(alpha: 0.4)),
      padding: EdgeInsets.zero,
      visualDensity: VisualDensity.compact,
    );
  }
}

class _SummaryCard extends StatelessWidget {
  final String title;
  final IconData icon;
  final Color color;
  final AsyncValue<int> asyncValue;

  const _SummaryCard({
    required this.title,
    required this.icon,
    required this.color,
    required this.asyncValue,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 200,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: [
                Icon(icon, color: color, size: 24),
                const Spacer(),
                asyncValue.when(
                  loading: () => const SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2)),
                  error: (_, __) =>
                      Icon(Icons.error_outline, color: color, size: 16),
                  data: (n) => Text(
                    '$n',
                    style: TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.bold,
                        color: color),
                  ),
                ),
              ]),
              const SizedBox(height: 12),
              Text(title,
                  style: const TextStyle(fontSize: 13, color: Colors.black54)),
            ],
          ),
        ),
      ),
    );
  }
}
