package org.acme;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;

public interface FruitResource extends PanacheEntityResource<Fruit, Long> {
}
