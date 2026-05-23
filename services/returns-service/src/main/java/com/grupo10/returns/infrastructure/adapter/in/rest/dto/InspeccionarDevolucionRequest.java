package com.grupo10.returns.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record InspeccionarDevolucionRequest(@NotBlank String observaciones) {}
