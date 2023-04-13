package net.bosowski.chattergpt.data.models.ai

import lombok.Data
import net.bosowski.chattergpt.data.dtos.ApiResponseDto
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import javax.persistence.*

@Data
@Entity
class ApiResponse {

    constructor(apiResponseDto: ApiResponseDto?){
        this.openAiId = apiResponseDto?.id
        this.`object` = apiResponseDto?.`object`
        this.created = apiResponseDto?.created
        this.model = apiResponseDto?.model
        this.choices = apiResponseDto?.choices?.map { Choice(it) }
        this.usage = Usage(apiResponseDto?.usage)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    var openAiId: String? = null

    var `object`: String? = null

    var created: Long? = null

    var model: String? = null

    @OneToMany(cascade = [CascadeType.ALL])
    var choices: List<Choice>? = null

    @OneToOne(cascade = [CascadeType.ALL])
    var usage: Usage? = null
}