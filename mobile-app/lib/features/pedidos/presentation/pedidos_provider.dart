import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../data/pedidos_service.dart';
import '../domain/pedido_model.dart';

final pedidosRefreshProvider = StateProvider<int>((ref) => 0);

final pedidosProvider = FutureProvider.autoDispose<List<PedidoModel>>((ref) {
  ref.watch(pedidosRefreshProvider);
  return ref.read(pedidosServiceProvider).getPedidos();
});
