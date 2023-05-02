package net.bosowski.chattergpt.data.models.ai;

import lombok.Data;
import net.bosowski.chattergpt.data.dtos.MessageDto
import javax.persistence.*;

@Entity
@Data
class ChatRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null;

    @OneToMany(cascade = [CascadeType.ALL])
    var messages: MutableList<MessageDto> = ArrayList();

    @OneToOne(cascade = [CascadeType.ALL])
    var modelRequest: ModelRequest? = null
}
