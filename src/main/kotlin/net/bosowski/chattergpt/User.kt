package net.bosowski.chattergpt

import lombok.Data
import org.jetbrains.annotations.NotNull
import javax.persistence.*
import java.util.Date
import javax.validation.constraints.Size

@Data
@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @NotNull
    @Size(min=3)
    var email: String? = null

    @NotNull
    var createdAt: Date = Date()

}