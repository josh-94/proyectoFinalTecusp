import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../data/devoluciones_service.dart';
import '../domain/devolucion_model.dart';

final devolucionesRefreshProvider = StateProvider<int>((ref) => 0);

final devolucionesProvider =
    FutureProvider.autoDispose<List<DevolucionModel>>((ref) {
  ref.watch(devolucionesRefreshProvider);
  return ref.read(devolucionesServiceProvider).getDevoluciones();
});
