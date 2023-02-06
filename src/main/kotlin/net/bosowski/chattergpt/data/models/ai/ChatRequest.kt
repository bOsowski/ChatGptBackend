package net.bosowski.chattergpt.data.models.ai;

import lombok.Data;
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
}
