package net.bosowski.chattergpt.data.models.ai;

import lombok.Data;
import net.bosowski.chattergpt.data.dtos.ApiResponseDto
import net.minidev.json.annotate.JsonIgnore
import javax.persistence.*;

@Entity
@Data
class ChatRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    var id: Long? = null;

    @OneToMany(cascade = [CascadeType.ALL])
    var messages: MutableList<Message> = ArrayList();

    var model: String = "text-davinci-003";

    var maxTokens: Int = 150;

    @OneToOne(cascade = [CascadeType.ALL])
    var apiResponse: ApiResponse? = null;

    var cost: Double = 0.0;
}
