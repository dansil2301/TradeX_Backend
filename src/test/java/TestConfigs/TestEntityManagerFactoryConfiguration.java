package TestConfigs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@TestConfiguration
@EntityScan({"Eco.TradeX.persistence.Entities.TraderRepository",
        "Eco.TradeX.persistence.Entities.PagesEntity",
        "Eco.TradeX.persistence.Entities.PagesVisitedEntity"})
public class TestEntityManagerFactoryConfiguration {

    @Bean(name="entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        return new LocalSessionFactoryBean();
    }
}
