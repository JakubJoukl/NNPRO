package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;

public class DeleteMessageDto {
    @NotNull
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
