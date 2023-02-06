package net.bosowski.chattergpt.data.dtos

import lombok.Data

@Data
class UsageDto {
    var prompt_tokens: Int? = null
    var completion_tokens: Int? = null
    var total_tokens: Int? = null
}