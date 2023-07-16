package eu.chrisp.aspringreactiveexamples;

import eu.chrisp.aspringreactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface PersonRepository {

    Mono<Person> getById(Integer id);
    Flux<Person> findAll();
}
