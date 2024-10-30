package com.example.nnprorocnikovyprojekt.dtos.general;

public class GeneralResponseDto {
    private String content;

    protected GeneralResponseDto() {

    }

    public GeneralResponseDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
