import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../data/inventario_service.dart';
import '../domain/lote_model.dart';

final lotesProvider = FutureProvider.autoDispose<List<LoteModel>>((ref) {
  return ref.read(inventarioServiceProvider).getLotes();
});

final lotesRefreshProvider = StateProvider<int>((ref) => 0);

final lotesWithRefreshProvider = FutureProvider.autoDispose<List<LoteModel>>((ref) {
  ref.watch(lotesRefreshProvider);
  return ref.read(inventarioServiceProvider).getLotes();
});
