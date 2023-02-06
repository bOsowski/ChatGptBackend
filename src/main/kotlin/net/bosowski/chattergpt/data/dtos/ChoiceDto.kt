package net.bosowski.chattergpt.data.dtos

import lombok.Data

@Data
class ChoiceDto {
    var text: String? = null
    var index: Int? = null
    var logprobs: Any? = null
    var finish_reason: String? = null
}