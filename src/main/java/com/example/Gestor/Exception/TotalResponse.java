package com.example.Gestor.Exception;

import java.math.BigDecimal;

public class TotalResponse {
    private BigDecimal total;

    public TotalResponse(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}