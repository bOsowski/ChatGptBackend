package net.bosowski.chattergpt.data.models.ai;

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Data;
import lombok.ToString
import net.bosowski.chattergpt.data.dtos.ApiResponseDto
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import net.minidev.json.annotate.JsonIgnore
import javax.persistence.*;

@Entity
@Data
class ChatRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null;

    @OneToMany(cascade = [CascadeType.ALL])
    var messages: MutableList<Message> = ArrayList();

    var model: String = "text-davinci-003";

    var maxTokens: Int = 150;

    @OneToOne(cascade = [CascadeType.ALL])
    var apiResponse: ApiResponse? = null;

    var cost: Double = 0.0;

    @ManyToOne
    var oauthUser: OauthUser? = null
}
