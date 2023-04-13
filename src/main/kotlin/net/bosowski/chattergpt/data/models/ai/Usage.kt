package net.bosowski.chattergpt.data.models.ai

import lombok.Data
import net.bosowski.chattergpt.data.dtos.UsageDto
import javax.persistence.*

@Data
@Entity
@Table(name = "api_usage")
class Usage {

    constructor(usage: UsageDto?){
        this.promptTokens = usage?.prompt_tokens
        this.completionTokens = usage?.completion_tokens
        this.totalTokens = usage?.total_tokens
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    var promptTokens: Int? = null
    var completionTokens: Int? = null
    var totalTokens: Int? = null
}
