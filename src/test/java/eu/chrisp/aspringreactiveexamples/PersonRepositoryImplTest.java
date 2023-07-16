package eu.chrisp.aspringreactiveexamples;

import eu.chrisp.aspringreactiveexamples.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRepositoryImplTest {

    PersonRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PersonRepositoryImpl();
    }

    @Test
    void monoBlock() {
        Mono<Person> personMono = repository.getById(1);
        Person person = personMono.block();
        System.out.println("person_value: " + person);
    }

    @Test
    void monoSubscribe() {
        Mono<Person> personMono = repository.getById(1);
        personMono.subscribe(person -> System.out.println("person_value: " + person));
    }

    @Test
    void monoFunction() {
        Mono<Person> personMono = repository.getById(1);
        personMono
                .map(person -> {
                    System.out.println("in");
                    return person.getFirstName();
                })
                .subscribe(firstName -> {
                    System.out.println("firstName_value: " + firstName);
                });
    }

    @Test
    void fluxBlockFirst() {
        Flux<Person> personFlux = repository.findAll();
        Person person = personFlux.blockFirst();
        System.out.println("person_value: " + person);
    }

    @Test
    void fluxSubscribe() {

//        Given & When
        Flux<Person> personFlux = repository.findAll();

//        Then
        StepVerifier.create(personFlux).expectNextCount(4);
        personFlux.subscribe(person -> {
            System.out.println("person_value: " + person);
        });
    }

    @Test
    void fluxToListMono() {
        Flux<Person> personFlux = repository.findAll();
        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(list -> {
            list.forEach(person -> {
                System.out.println("person_value: " + person);
            });
        });
    }

    @Test
    void findPersonById() {
        final Integer id = 3;

        Flux<Person> personFlux = repository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();

        personMono.subscribe(person -> System.out.println("person_value: " + person));
    }

    @Test
    void findPersonByIdNotFound() {
        final Integer id = 8;

        Flux<Person> personFlux = repository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();

        personMono.subscribe(person -> System.out.println("person_value: " + person));
    }

    @Test
    void findPersonByIdNotFoundException() {
        final Integer id = 8;

        Flux<Person> personFlux = repository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single();

        personMono
                .doOnError(throwable -> System.out.println("I went boom"))
                .onErrorReturn(Person.builder().id(id).build())
                .subscribe(person -> System.out.println("person_value: " + person));
    }

    @Test
    void getById() {
//        Given
        final Integer id = 2;

//        When
        Mono<Person> personMono = repository.getById(id);

//        Then
        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        assertThat(personMono.block()).isNotNull();
    }

    @Test
    void getByIdNull() {
//        Given
        final Integer id = 123;

//        When
        Mono<Person> personMono = repository.getById(id);

//        Then
        StepVerifier.create(personMono).verifyComplete();
        assertThat(personMono.block()).isNull();
    }
}
