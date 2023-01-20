package net.bosowski.chattergpt.data.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*
import javax.validation.constraints.NotNull

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
class OauthAttribute(
    @NotNull
    var attributeKey: String? = null,
    var attributeValue: String? = null
) {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
}