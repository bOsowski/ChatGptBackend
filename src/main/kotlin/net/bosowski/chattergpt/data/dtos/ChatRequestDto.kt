package net.bosowski.chattergpt.data.dtos

import lombok.Data

@Data
class ChatRequestDto {
    var messages: MutableList<MessageDto> = ArrayList();

    var model: String = "text-davinci-003";

    var maxTokens: Int = 150;

    var temperature = 0.9f
}