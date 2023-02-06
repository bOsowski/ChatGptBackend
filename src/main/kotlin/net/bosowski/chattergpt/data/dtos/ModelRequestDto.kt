package net.bosowski.chattergpt.data.dtos

import lombok.Data

/*
{
  "model": "text-davinci-003",
  "prompt": "Repeat the following phrase: 'this is a test'.",
  "max_tokens": 10,
  "temperature": 0,
  "presence_penalty": 0.6,
  "top_p": 1,
  "n": 1,
  "stream": false,
  "stop": ""
}
 */

@Data
class ModelRequestDto {
    var model: String = "text-davinci-003"
    var prompt: String? = null
    var temperature: Float = 0.9f
    var max_tokens: Int = 150
    var top_p: Float = 1f
    var frequency_penalty: Int = 0
    var n: Int = 1
    var presence_penalty: Float = 0f
    var stream: Boolean = false
    var stop: MutableList<String> = ArrayList()
}