package net.bosowski.chattergpt.data.dtos

import lombok.Data

/*
 {
    "id": "cmpl-6gz4L4zozpItH52pyqFSwo3A2tKZw",
    "object": "text_completion",
    "created": 1675702545,
    "model": "text-davinci-003",
    "choices": [
        {
            "text": "Sorry to hear that, is there anything I can do?",
            "index": 0,
            "logprobs": null,
            "finish_reason": "stop"
        }
    ],
    "usage": {
        "prompt_tokens": 83,
        "completion_tokens": 12,
        "total_tokens": 95
    }
}
*/
@Data
class ModelResponseDto {
    var id: String? = null
    var `object`: String? = null
    var created: Long? = null
    var model: String? = null
    var choices: List<ChoiceDto>? = null
    var usage: UsageDto? = null
}