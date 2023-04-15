package net.bosowski.chattergpt.configuration

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class FlywayConfiguration @Autowired constructor(dataSource: DataSource?) {
    init {
        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate()
    }
}