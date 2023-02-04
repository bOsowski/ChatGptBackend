package net.bosowski.chattergpt.data.models

import lombok.Data
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Data
@Entity
class ModelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @NotNull
    @Column(columnDefinition = "text")
    var prompt: String = ""

    @NotNull
    @Column(columnDefinition = "text")
    var completion: String = ""

    @NotNull
    var model: String = "text-davinci"

    @NotNull
    var tokensUsed: Int = 0
}