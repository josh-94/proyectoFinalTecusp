import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/devoluciones_service.dart';
import '../../domain/devolucion_model.dart';
import '../devoluciones_provider.dart';

class RegistrarDevolucionDialog extends ConsumerStatefulWidget {
  const RegistrarDevolucionDialog({super.key});

  @override
  ConsumerState<RegistrarDevolucionDialog> createState() =>
      _RegistrarDevolucionDialogState();
}

class _RegistrarDevolucionDialogState
    extends ConsumerState<RegistrarDevolucionDialog> {
  final _formKey = GlobalKey<FormState>();
  final _pedidoCtrl = TextEditingController();
  final List<_LineaForm> _lineas = [_LineaForm()];
  bool _loading = false;
  String? _error;

  @override
  void dispose() {
    _pedidoCtrl.dispose();
    for (final l in _lineas) { l.loteCtrl.dispose(); l.cantCtrl.dispose(); l.motivoCtrl.dispose(); }
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _loading = true; _error = null; });
    try {
      await ref.read(devolucionesServiceProvider).registrar(
            pedidoId: _pedidoCtrl.text.trim(),
            lineas: _lineas
                .map((l) => LineaDevolucionModel(
                      loteId: l.loteCtrl.text.trim(),
                      cantidadDevuelta: int.parse(l.cantCtrl.text.trim()),
                      motivoDevolucion: l.motivoCtrl.text.trim(),
                    ))
                .toList(),
          );
      ref.read(devolucionesRefreshProvider.notifier).update((s) => s + 1);
      if (mounted) Navigator.of(context).pop();
    } catch (e) {
      setState(() {
        _error = 'Error al registrar la devolución.';
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return AlertDialog(
      title: const Text('Registrar Devolución'),
      content: SizedBox(
        width: 520,
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextFormField(
                  controller: _pedidoCtrl,
                  decoration: const InputDecoration(
                      labelText: 'ID del Pedido original',
                      prefixIcon: Icon(Icons.receipt_long_outlined)),
                  validator: (v) =>
                      (v == null || v.isEmpty) ? 'Requerido' : null,
                ),
                const SizedBox(height: 20),
                Row(children: [
                  Text('Ítems a devolver',
                      style: TextStyle(
                          fontWeight: FontWeight.w600, color: cs.primary)),
                  const Spacer(),
                  TextButton.icon(
                    icon: const Icon(Icons.add, size: 16),
                    label: const Text('Agregar ítem'),
                    onPressed: () => setState(() => _lineas.add(_LineaForm())),
                  ),
                ]),
                const SizedBox(height: 8),
                ..._lineas.asMap().entries.map((entry) {
                  final i = entry.key;
                  final linea = entry.value;
                  return Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: Padding(
                      padding: const EdgeInsets.all(12),
                      child: Column(children: [
                        Row(children: [
                          Text('Ítem ${i + 1}',
                              style:
                                  const TextStyle(fontWeight: FontWeight.w500)),
                          const Spacer(),
                          if (_lineas.length > 1)
                            IconButton(
                              icon: const Icon(Icons.remove_circle_outline,
                                  size: 18),
                              onPressed: () =>
                                  setState(() => _lineas.removeAt(i)),
                            ),
                        ]),
                        const SizedBox(height: 8),
                        TextFormField(
                          controller: linea.loteCtrl,
                          decoration: const InputDecoration(
                              labelText: 'ID de Lote', isDense: true),
                          validator: (v) =>
                              (v == null || v.isEmpty) ? 'Requerido' : null,
                        ),
                        const SizedBox(height: 8),
                        Row(children: [
                          Expanded(
                            child: TextFormField(
                              controller: linea.cantCtrl,
                              decoration: const InputDecoration(
                                  labelText: 'Cantidad', isDense: true),
                              keyboardType: TextInputType.number,
                              validator: (v) {
                                if (v == null || v.isEmpty) return 'Req.';
                                final n = int.tryParse(v);
                                if (n == null || n <= 0) return '> 0';
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(width: 8),
                          Expanded(
                            flex: 2,
                            child: TextFormField(
                              controller: linea.motivoCtrl,
                              decoration: const InputDecoration(
                                  labelText: 'Motivo devolución',
                                  isDense: true),
                              validator: (v) =>
                                  (v == null || v.isEmpty) ? 'Req.' : null,
                            ),
                          ),
                        ]),
                      ]),
                    ),
                  );
                }),
                if (_error != null) ...[
                  const SizedBox(height: 8),
                  Text(_error!, style: TextStyle(color: cs.error)),
                ],
              ],
            ),
          ),
        ),
      ),
      actions: [
        TextButton(
            onPressed: _loading ? null : () => Navigator.of(context).pop(),
            child: const Text('Cancelar')),
        FilledButton(
          onPressed: _loading ? null : _submit,
          child: _loading
              ? const SizedBox(
                  width: 18, height: 18,
                  child: CircularProgressIndicator(
                      strokeWidth: 2, color: Colors.white))
              : const Text('Registrar'),
        ),
      ],
    );
  }
}

class _LineaForm {
  final loteCtrl = TextEditingController();
  final cantCtrl = TextEditingController();
  final motivoCtrl = TextEditingController();
}
