import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/network/dio_client.dart';
import '../domain/pedido_model.dart';

final pedidosServiceProvider =
    Provider((ref) => PedidosService(ref.read(dioClientProvider)));

class PedidosService {
  final Dio _dio;
  PedidosService(this._dio);

  Future<List<PedidoModel>> getPedidos() async {
    final resp = await _dio.get(ApiConstants.pedidos);
    return (resp.data as List<dynamic>)
        .map((e) => PedidoModel.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<PedidoModel> crearPedido({
    required String hospitalDestino,
    required List<LineaPedidoModel> lineas,
  }) async {
    final resp = await _dio.post(ApiConstants.pedidos, data: {
      'hospitalDestino': hospitalDestino,
      'lineas': lineas.map((l) => l.toJson()).toList(),
    });
    return PedidoModel.fromJson(resp.data as Map<String, dynamic>);
  }

  Future<void> despachar(String pedidoId) async {
    await _dio.patch('${ApiConstants.pedidos}/$pedidoId/despachar');
  }

  Future<void> cancelar(String pedidoId) async {
    await _dio.patch('${ApiConstants.pedidos}/$pedidoId/cancelar');
  }
}
