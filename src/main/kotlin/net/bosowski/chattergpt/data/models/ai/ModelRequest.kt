package net.bosowski.chattergpt.data.models.ai;

import lombok.Data;
import net.bosowski.chattergpt.data.dtos.ModelRequestDto
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import javax.persistence.*;

@Entity
@Data
class ModelRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null;

    var model: String = "text-davinci-003";

    @Column(columnDefinition = "TEXT")
    var prompt: String? = null

    var temperature: Float = 0.9f

    var maxTokens: Int = 150;

    var topP: Float = 1F

    var frequencyPenalty: Int = 0

    var n: Int = 1

    var presencePenalty: Float = 0f

    var stream: Boolean = false

    @ElementCollection
    @CollectionTable(name = "model_request_stop")
    var stop: MutableList<String>? = null

    var cost: Float = 0f

    @ManyToOne
    var oauthUser: OauthUser? = null

    @OneToOne(cascade = [CascadeType.ALL])
    var apiResponse: ApiResponse? = null;

    fun toModelRequestDto(): ModelRequestDto {
        val modelRequestDto = ModelRequestDto()
        modelRequestDto.model = this.model
        modelRequestDto.prompt = this.prompt
        modelRequestDto.temperature = this.temperature
        modelRequestDto.max_tokens = this.maxTokens
        modelRequestDto.top_p = this.topP
        modelRequestDto.frequency_penalty = this.frequencyPenalty
        modelRequestDto.n = this.n
        modelRequestDto.presence_penalty = this.presencePenalty
        modelRequestDto.stream = this.stream
        modelRequestDto.stop = this.stop
        return modelRequestDto
    }
}
