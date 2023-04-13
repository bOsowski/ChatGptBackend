package net.bosowski.chattergpt.data.models.ai

import lombok.Data
import net.bosowski.chattergpt.data.dtos.ChoiceDto
import javax.persistence.*

@Data
@Entity
class Choice {

    constructor()
    constructor(choice: ChoiceDto?){
        this.text = choice?.text
        this.index = choice?.index
        this.logprobs = choice?.logprobs.toString()
        this.finish_reason = choice?.finish_reason
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    var text: String? = null

    @Column(name = "choice_index")
    var index: Int? = null

    @Column(columnDefinition = "TEXT")
    var logprobs: String? = null

    var finish_reason: String? = null
}