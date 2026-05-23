import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/network/dio_client.dart';
import '../domain/devolucion_model.dart';

final devolucionesServiceProvider =
    Provider((ref) => DevolucionesService(ref.read(dioClientProvider)));

class DevolucionesService {
  final Dio _dio;
  DevolucionesService(this._dio);

  Future<List<DevolucionModel>> getDevoluciones() async {
    final resp = await _dio.get(ApiConstants.devoluciones);
    return (resp.data as List<dynamic>)
        .map((e) => DevolucionModel.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<DevolucionModel> registrar({
    required String pedidoId,
    required List<LineaDevolucionModel> lineas,
  }) async {
    final resp = await _dio.post(ApiConstants.devoluciones, data: {
      'pedidoId': pedidoId,
      'lineas': lineas.map((l) => l.toJson()).toList(),
    });
    return DevolucionModel.fromJson(resp.data as Map<String, dynamic>);
  }

  Future<void> inspeccionar(String id, String observaciones) async {
    await _dio.patch('${ApiConstants.devoluciones}/$id/inspeccionar',
        data: {'observaciones': observaciones});
  }

  Future<void> aprobar(String id) async {
    await _dio.patch('${ApiConstants.devoluciones}/$id/aprobar');
  }

  Future<void> rechazar(String id, String motivo) async {
    await _dio.patch('${ApiConstants.devoluciones}/$id/rechazar',
        data: {'motivo': motivo});
  }
}
