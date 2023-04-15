package net.bosowski.chattergpt.data.dtos

import lombok.Data
import net.minidev.json.annotate.JsonIgnore
import javax.persistence.*

@Data
@Entity
class MessageDto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    var id: Long? = null

    var sender: String = ""
    var message: String = ""
}