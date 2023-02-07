package net.bosowski.chattergpt.data.models.ai

import lombok.Data
import javax.persistence.*

@Data
@Entity
class ModelPricing {
    @Id
    @Column(name = "name", nullable = false)
    var name: String? = null

    var price: Double = 0.0
}