package net.bosowski.chattergpt.data.models.authentication

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.jetbrains.annotations.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import javax.persistence.*

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
class OauthUser : OidcUser {
    @OneToMany(cascade = [CascadeType.ALL])
    var oauthAttributes: List<OauthAttribute> = ArrayList()

    @OneToMany(cascade = [CascadeType.ALL])
    var oauthAuthorities: List<OauthAuthority> = ArrayList()

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    @NotNull
    var oauthId: String? = null

    @NotNull
    var availableCredits: Float = 0f

    @OneToOne(cascade = [CascadeType.ALL])
    var oauthToken: OauthToken? = null

    constructor()
    constructor(
        oauthAttributes: List<OauthAttribute>,
        oauthAuthorities: List<OauthAuthority>,
    ) : this() {
        this.oauthAttributes = oauthAttributes
        this.oauthAuthorities = oauthAuthorities
    }

    override fun getName(): String {
        return oauthAttributes.find { it.attributeKey == "name" }?.attributeValue ?: ""
    }

    override fun getAttributes(): Map<String?, String> {
        return oauthAttributes.associate { it.attributeKey to it.attributeValue.toString() }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return this.oauthAuthorities.distinct().toMutableList()
    }

    override fun getClaims(): MutableMap<String?, Any> {
        return oauthAttributes.associate { it.attributeKey to (it.attributeValue as Any) }.toMutableMap()
    }

    override fun getUserInfo(): OidcUserInfo {
        return OidcUserInfo(this.attributes)
    }

    override fun getIdToken(): OidcIdToken {
        return OidcIdToken(
            this.oauthToken?.tokenValue,
            this.oauthToken?.issuedAt?.toInstant(),
            this.oauthToken?.expiresAt?.toInstant(),
            this.attributes
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OauthUser

        if (oauthId != other.oauthId) return false
        if (availableCredits != other.availableCredits) return false

        return true
    }

    override fun hashCode(): Int {
        var result = oauthId?.hashCode() ?: 0
        result = 31 * result + availableCredits.hashCode()
        return result
    }
}