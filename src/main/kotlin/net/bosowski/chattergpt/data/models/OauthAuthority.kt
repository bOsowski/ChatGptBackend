package net.bosowski.chattergpt.data.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.security.core.GrantedAuthority
import javax.persistence.*
import javax.validation.constraints.NotNull

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
class OauthAuthority(
    @NotNull
    var authorityString: String
) : GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null


    override fun getAuthority(): String {
        return authorityString
    }

}