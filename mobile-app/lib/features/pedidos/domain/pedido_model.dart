class LineaPedidoModel {
  final String loteId;
  final int cantidad;
  final String descripcion;

  const LineaPedidoModel({
    required this.loteId,
    required this.cantidad,
    required this.descripcion,
  });

  factory LineaPedidoModel.fromJson(Map<String, dynamic> j) => LineaPedidoModel(
        loteId: j['loteId'] as String,
        cantidad: j['cantidad'] as int,
        descripcion: j['descripcion'] as String? ?? '',
      );

  Map<String, dynamic> toJson() => {
        'loteId': loteId,
        'cantidad': cantidad,
        'descripcion': descripcion,
      };
}

class PedidoModel {
  final String id;
  final String numeroPedido;
  final String solicitadoPor;
  final String hospitalDestino;
  final String estado;
  final String? motivoRechazo;
  final List<LineaPedidoModel> lineas;
  final String creadoEn;
  final String actualizadoEn;

  const PedidoModel({
    required this.id,
    required this.numeroPedido,
    required this.solicitadoPor,
    required this.hospitalDestino,
    required this.estado,
    this.motivoRechazo,
    required this.lineas,
    required this.creadoEn,
    required this.actualizadoEn,
  });

  factory PedidoModel.fromJson(Map<String, dynamic> j) => PedidoModel(
        id: j['id'] as String,
        numeroPedido: j['numeroPedido'] as String,
        solicitadoPor: j['solicitadoPor'] as String,
        hospitalDestino: j['hospitalDestino'] as String,
        estado: j['estado'] as String,
        motivoRechazo: j['motivoRechazo'] as String?,
        lineas: (j['lineas'] as List<dynamic>? ?? [])
            .map((e) => LineaPedidoModel.fromJson(e as Map<String, dynamic>))
            .toList(),
        creadoEn: j['creadoEn'] as String? ?? '',
        actualizadoEn: j['actualizadoEn'] as String? ?? '',
      );
}
