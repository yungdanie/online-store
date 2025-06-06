package ru.practicum.account.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentDTO(@NotNull BigDecimal amount) {}
