import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/inventario_service.dart';
import '../../domain/lote_model.dart';
import '../inventario_provider.dart';

class MovimientoDialog extends ConsumerStatefulWidget {
  final LoteModel lote;
  const MovimientoDialog({super.key, required this.lote});

  @override
  ConsumerState<MovimientoDialog> createState() => _MovimientoDialogState();
}

class _MovimientoDialogState extends ConsumerState<MovimientoDialog> {
  final _formKey = GlobalKey<FormState>();
  final _cantidadCtrl = TextEditingController();
  final _refCtrl = TextEditingController();
  String _tipo = 'ENTRADA';
  bool _loading = false;
  String? _error;

  static const _tipos = ['ENTRADA', 'SALIDA', 'RESERVA', 'LIBERACION'];

  @override
  void dispose() {
    _cantidadCtrl.dispose();
    _refCtrl.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _loading = true; _error = null; });
    try {
      await ref.read(inventarioServiceProvider).registrarMovimiento(
            loteId: widget.lote.id,
            tipo: _tipo,
            cantidad: int.parse(_cantidadCtrl.text.trim()),
            referenciaExterna: _refCtrl.text.trim().isEmpty
                ? null
                : _refCtrl.text.trim(),
          );
      ref.read(lotesRefreshProvider.notifier).update((s) => s + 1);
      if (mounted) Navigator.of(context).pop();
    } catch (e) {
      setState(() {
        _error = e.toString().contains('409')
            ? 'Stock insuficiente para este movimiento'
            : 'Error al registrar el movimiento';
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return AlertDialog(
      title: const Text('Registrar Movimiento'),
      content: SizedBox(
        width: 380,
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _InfoRow('Lote', widget.lote.numeroLote),
              _InfoRow('Producto', widget.lote.nombreProducto),
              _InfoRow('Stock actual', '${widget.lote.cantidadDisponible}'),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _tipo,
                decoration: const InputDecoration(labelText: 'Tipo de movimiento'),
                items: _tipos
                    .map((t) => DropdownMenuItem(value: t, child: Text(t)))
                    .toList(),
                onChanged: (v) => setState(() => _tipo = v!),
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _cantidadCtrl,
                decoration: const InputDecoration(labelText: 'Cantidad'),
                keyboardType: TextInputType.number,
                validator: (v) {
                  if (v == null || v.isEmpty) return 'Requerido';
                  final n = int.tryParse(v);
                  if (n == null || n <= 0) return 'Debe ser mayor a 0';
                  return null;
                },
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _refCtrl,
                decoration: const InputDecoration(
                    labelText: 'Referencia externa (opcional)'),
              ),
              if (_error != null) ...[
                const SizedBox(height: 12),
                Text(_error!, style: TextStyle(color: cs.error)),
              ],
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _loading ? null : () => Navigator.of(context).pop(),
          child: const Text('Cancelar'),
        ),
        FilledButton(
          onPressed: _loading ? null : _submit,
          child: _loading
              ? const SizedBox(
                  width: 18, height: 18,
                  child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
              : const Text('Registrar'),
        ),
      ],
    );
  }
}

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;
  const _InfoRow(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: Row(
        children: [
          Text('$label: ',
              style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
          Text(value, style: const TextStyle(fontSize: 13)),
        ],
      ),
    );
  }
}
