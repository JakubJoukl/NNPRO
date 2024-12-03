package com.example.nnprorocnikovyprojekt.dtos.conversation;

import jakarta.validation.constraints.NotNull;

public class ConversationNameDto {
    @NotNull
    private Integer id;

    @NotNull
    private String name;

    public ConversationNameDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ConversationNameDto(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
